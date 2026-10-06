package harith.fp

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis
import harith.uint._

/**
  * The shared pipelined floating-point wrapping around a wide alignment adder.
  *
  * The alignment adder is built by `addFactory`, which returns an existing [[harith.uint.UIntAdd]]
  * whose own register layers are `stages` deep. The operand decode, special handling and rounding
  * stay combinational on the delayed operands, so the effective depth of that part depends on EDA
  * retiming. `stages = 0` makes the adder combinational. NaN is canonical, per RISC-V.
  *
  * @param aFmt       The format of the first operand
  * @param bFmt       The format of the second operand
  * @param outFmt     The format of the result
  * @param stages     The number of pipeline register layers, which is also the latency
  * @param policy     The numeric policy
  * @param addFactory The wide alignment adder factory, given the width and the stage count
  */
abstract class FpPipelinedAddBase(
    val aFmt:   FpFormat,
    val bFmt:   FpFormat,
    val outFmt: FpFormat,
    val stages: Int,
    policy:     FpPolicy,
    addFactory: (Int, Int) => UIntAdd,
) extends FpAdd {
  require(stages >= 0, "stages must be non-negative")

  override def latency: Int = stages

  val io = IO(new FpAddIO(aFmt, bFmt, outFmt))

  val a0 = FpUtils.decode(io.src1, aFmt)
  val b0 = FpUtils.decode(io.src2, bFmt)

  val keep  = outFmt.manWidth + 12
  val expA0 = a0.exp - aFmt.manWidth.S(FpUtils.EW.W)
  val expB0 = b0.exp - bFmt.manWidth.S(FpUtils.EW.W)
  val eA0   = expA0 + FpUtils.msbIndex(a0.sig).pad(FpUtils.EW).asSInt
  val eB0   = expB0 + FpUtils.msbIndex(b0.sig).pad(FpUtils.EW).asSInt
  val eW0   = Mux(eA0 > eB0, eA0, eB0) - keep.S(FpUtils.EW.W)

  val (ash0, ast0) = FpUtils.alignToExp(a0.sig, expA0, eW0, keep)
  val (bsh0, bst0) = FpUtils.alignToExp(b0.sig, expB0, eW0, keep)

  val w0    = math.max(ash0.getWidth, bsh0.getWidth)
  val aVal0 = Mux(a0.sign, -ash0.pad(w0), ash0.pad(w0))
  val bVal0 = Mux(b0.sign, -bsh0.pad(w0), bsh0.pad(w0))

  val adder = addFactory(w0 + 1, stages)
  val inner = adder.latency
  require(inner <= stages, "the pipelined adder latency must not exceed stages")

  adder.io.src1  := aVal0.pad(w0 + 1).asUInt
  adder.io.src2  := bVal0.pad(w0 + 1).asUInt
  adder.io.carry := false.B

  val totalU = FpUtils.pipe(adder.io.output, stages - inner)
  val total  = totalU(w0, 0).asSInt
  val tSign  = total(w0)
  val tMag   = Mux(tSign, (-total).asUInt, total.asUInt)

  val src1R = FpUtils.pipe(io.src1, stages)
  val src2R = FpUtils.pipe(io.src2, stages)
  val rmR   = FpUtils.pipe(io.rm, stages)
  val eWR   = FpUtils.pipe(eW0, stages)
  val stR   = FpUtils.pipe(ast0 || bst0, stages)

  val a = FpUtils.decode(src1R, aFmt)
  val b = FpUtils.decode(src2R, bFmt)

  val aZero = if (policy.daz) a.isZero || a.isSubnormal else a.isZero
  val bZero = if (policy.daz) b.isZero || b.isSubnormal else b.isZero

  val expAR = FpUtils.pipe(expA0, stages)
  val expBR = FpUtils.pipe(expB0, stages)

  val general = FpUtils.roundPack(tSign, tMag, eWR, outFmt, rmR, policy, stR)
  val aRound  = FpUtils.roundPack(a.sign, a.sig, expAR, outFmt, rmR, policy)
  val bRound  = FpUtils.roundPack(b.sign, b.sig, expBR, outFmt, rmR, policy)

  val oppositeInf = a.isInf && b.isInf && (a.sign =/= b.sign)
  val nanCase     = a.isNaN || b.isNaN || oppositeInf
  val infCase     = !nanCase && (a.isInf || b.isInf)
  val infSign     = Mux(a.isInf, a.sign, b.sign)

  val bothZero  = !nanCase && !infCase && aZero && bZero
  val aOnlyZero = !nanCase && !infCase && aZero && !bZero
  val bOnlyZero = !nanCase && !infCase && !aZero && bZero

  val zeroSign = Mux(a.sign === b.sign, a.sign, rmR === 2.U)

  val isSpecial = nanCase || infCase || bothZero
  val selNx     =
    Mux(isSpecial, false.B, Mux(aOnlyZero, bRound.nx, Mux(bOnlyZero, aRound.nx, general.nx)))
  val selUf =
    Mux(isSpecial, false.B, Mux(aOnlyZero, bRound.uf, Mux(bOnlyZero, aRound.uf, general.uf)))
  val selOf =
    Mux(isSpecial, false.B, Mux(aOnlyZero, bRound.of, Mux(bOnlyZero, aRound.of, general.of)))

  io.output := Mux(
    nanCase,
    outFmt.canonicalNaN,
    Mux(
      infCase,
      Cat(infSign, outFmt.infinityMag),
      Mux(
        bothZero,
        outFmt.zero(zeroSign),
        Mux(aOnlyZero, bRound.bits, Mux(bOnlyZero, aRound.bits, general.bits)),
      ),
    ),
  )

  io.fflags.nx := selNx
  io.fflags.uf := selUf
  io.fflags.of := selOf
  io.fflags.dz := false.B
  io.fflags.nv := oppositeInf
}

/**
  * A pipelined floating-point adder with a parallel prefix alignment adder.
  *
  * The prefix levels of the selected [[harith.uint.PrefixStyle]] are distributed over `stages`
  * register layers, while the floating-point control is a register queue matched to the adder
  * latency. `stages = 0` makes the adder combinational. NaN is canonical, per RISC-V.
  *
  * fpga@fp64-2cyc: delay[i/o/max] = 23.093ns/29.830ns/29.259ns  area = 9805luts + 1232ff
  *
  * 55nm@fp64-2cyc: delay[i/o/max] = 13.2229ns/33.5948ns/33.5948ns  area = 34074.88um²
  *
  * fpga@fp32-2cyc: delay[i/o/max] = 19.973ns/25.614ns/25.022ns  area = 4952luts + 627ff
  *
  * 55nm@fp32-2cyc: delay[i/o/max] = 8.5569ns/21.8746ns/21.8746ns  area = 17788.96um²
  *
  * fpga@fp16-2cyc: delay[i/o/max] = 17.190ns/22.361ns/21.790ns  area = 2295luts + 334ff
  *
  * 55nm@fp16-2cyc: delay[i/o/max] = 4.5925ns/17.5413ns/17.5413ns  area = 9081.52um²
  *
  * fpga@bf16-2cyc: delay[i/o/max] = 16.462ns/21.870ns/21.299ns  area = 2029luts + 331ff
  *
  * 55nm@bf16-2cyc: delay[i/o/max] = 5.5078ns/15.6008ns/15.6008ns  area = 7902.72um²
  *
  * @param aFmt   The format of the first operand
  * @param bFmt   The format of the second operand
  * @param outFmt The format of the result
  * @param stages The number of pipeline register layers, which is also the latency
  * @param policy The numeric policy
  */
class FpPipelinedPrefixAdd(
    aFmt:   FpFormat,
    bFmt:   FpFormat,
    outFmt: FpFormat,
    stages: Int,
    policy: FpPolicy = FpPolicy(),
) extends FpPipelinedAddBase(
      aFmt,
      bFmt,
      outFmt,
      stages,
      policy,
      (w, s) => Module(new UIntPipelinedPrefixAdd(w, PrefixStyle.KoggeStone, s)),
    )

object FpPipelinedPrefixAddFp64 extends App {
  ExportForAnalysis(
    new FpPipelinedPrefixAdd(FpFormat.Fp64, FpFormat.Fp64, FpFormat.Fp64, 2),
    args,
    FpExport.opts,
  )
}

object FpPipelinedPrefixAddFp32 extends App {
  ExportForAnalysis(
    new FpPipelinedPrefixAdd(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, 2),
    args,
    FpExport.opts,
  )
}

object FpPipelinedPrefixAddFp16 extends App {
  ExportForAnalysis(
    new FpPipelinedPrefixAdd(FpFormat.Fp16, FpFormat.Fp16, FpFormat.Fp16, 2),
    args,
    FpExport.opts,
  )
}

object FpPipelinedPrefixAddBf16 extends App {
  ExportForAnalysis(
    new FpPipelinedPrefixAdd(FpFormat.Bf16, FpFormat.Bf16, FpFormat.Bf16, 2),
    args,
    FpExport.opts,
  )
}

/**
  * A pipelined floating-point adder with a pipelined ripple carry alignment adder.
  *
  * The carry chain is cut into `stages` blocks, so the latency is the block count. The
  * floating-point control is a register queue matched to the adder latency. NaN is canonical, per
  * RISC-V.
  *
  * fpga@fp64-2cyc: delay[i/o/max] = 32.972ns/31.277ns/30.300ns  area = 8568luts + 800ff
  *
  * 55nm@fp64-2cyc: delay[i/o/max] = 12.5026ns/32.6793ns/32.6793ns  area = 29576.12um²
  *
  * fpga@fp32-2cyc: delay[i/o/max] = 22.847ns/27.023ns/26.054ns  area = 4115luts + 426ff
  *
  * 55nm@fp32-2cyc: delay[i/o/max] = 7.7297ns/22.5578ns/22.5578ns  area = 15058.68um²
  *
  * fpga@fp16-2cyc: delay[i/o/max] = 19.377ns/23.726ns/22.760ns  area = 1983luts + 236ff
  *
  * 55nm@fp16-2cyc: delay[i/o/max] = 4.8011ns/14.7619ns/14.7619ns  area = 7900.48um²
  *
  * fpga@bf16-2cyc: delay[i/o/max] = 17.751ns/23.085ns/22.514ns  area = 1790luts + 240ff
  *
  * 55nm@bf16-2cyc: delay[i/o/max] = 5.0970ns/15.8745ns/15.8745ns  area = 7479.92um²
  *
  * @param aFmt   The format of the first operand
  * @param bFmt   The format of the second operand
  * @param outFmt The format of the result
  * @param stages The number of pipeline register layers, which is also the latency
  * @param policy The numeric policy
  */
class FpPipelinedRippleAdd(
    aFmt:   FpFormat,
    bFmt:   FpFormat,
    outFmt: FpFormat,
    stages: Int,
    policy: FpPolicy = FpPolicy(),
) extends FpPipelinedAddBase(
      aFmt,
      bFmt,
      outFmt,
      stages,
      policy,
      (w, s) =>
        Module(new UIntPipelinedRippleAdd(w, if (s <= 0) w else math.max(1, (w + s - 1) / s))),
    ) {
  require(stages > 0, "the pipelined ripple adder needs at least one stage")
}

object FpPipelinedRippleAddFp64 extends App {
  ExportForAnalysis(
    new FpPipelinedRippleAdd(FpFormat.Fp64, FpFormat.Fp64, FpFormat.Fp64, 2),
    args,
    FpExport.opts,
  )
}

object FpPipelinedRippleAddFp32 extends App {
  ExportForAnalysis(
    new FpPipelinedRippleAdd(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, 2),
    args,
    FpExport.opts,
  )
}

object FpPipelinedRippleAddFp16 extends App {
  ExportForAnalysis(
    new FpPipelinedRippleAdd(FpFormat.Fp16, FpFormat.Fp16, FpFormat.Fp16, 2),
    args,
    FpExport.opts,
  )
}

object FpPipelinedRippleAddBf16 extends App {
  ExportForAnalysis(
    new FpPipelinedRippleAdd(FpFormat.Bf16, FpFormat.Bf16, FpFormat.Bf16, 2),
    args,
    FpExport.opts,
  )
}

/**
  * A pipelined floating-point adder with an inferred alignment adder.
  *
  * The alignment adder itself has no internal register layers, so the `stages` register queue delays
  * the operands and the control while the adder and the rounding tail stay combinational; the
  * effective depth of that tail depends on EDA retiming. NaN is canonical, per RISC-V.
  *
  * fpga@fp64-2cyc: delay[i/o/max] = 24.251ns/29.933ns/29.362ns  area = 8609luts + 710ff
  *
  * 55nm@fp64-2cyc: delay[i/o/max] = 13.3667ns/31.3051ns/31.3051ns  area = 30299.92um²
  *
  * fpga@fp32-2cyc: delay[i/o/max] = 18.449ns/25.795ns/25.203ns  area = 4120luts + 383ff
  *
  * 55nm@fp32-2cyc: delay[i/o/max] = 7.4952ns/31.2895ns/31.2895ns  area = 15043.56um²
  *
  * fpga@fp16-2cyc: delay[i/o/max] = 16.316ns/22.367ns/21.796ns  area = 1984luts + 215ff
  *
  * 55nm@fp16-2cyc: delay[i/o/max] = 4.4135ns/15.5903ns/15.5903ns  area = 8524.04um²
  *
  * fpga@bf16-2cyc: delay[i/o/max] = 15.710ns/21.870ns/21.299ns  area = 1772luts + 221ff
  *
  * 55nm@bf16-2cyc: delay[i/o/max] = 4.8244ns/15.2796ns/15.2796ns  area = 7106.40um²
  *
  * @param aFmt   The format of the first operand
  * @param bFmt   The format of the second operand
  * @param outFmt The format of the result
  * @param stages The number of pipeline register layers, which is also the latency
  * @param policy The numeric policy
  */
class FpPipelinedMacroAdd(
    aFmt:   FpFormat,
    bFmt:   FpFormat,
    outFmt: FpFormat,
    stages: Int,
    policy: FpPolicy = FpPolicy(),
) extends FpPipelinedAddBase(
      aFmt,
      bFmt,
      outFmt,
      stages,
      policy,
      (w, _) => Module(new UIntMacroAdd(w)),
    )

object FpPipelinedMacroAddFp64 extends App {
  ExportForAnalysis(
    new FpPipelinedMacroAdd(FpFormat.Fp64, FpFormat.Fp64, FpFormat.Fp64, 2),
    args,
    FpExport.opts,
  )
}

object FpPipelinedMacroAddFp32 extends App {
  ExportForAnalysis(
    new FpPipelinedMacroAdd(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, 2),
    args,
    FpExport.opts,
  )
}

object FpPipelinedMacroAddFp16 extends App {
  ExportForAnalysis(
    new FpPipelinedMacroAdd(FpFormat.Fp16, FpFormat.Fp16, FpFormat.Fp16, 2),
    args,
    FpExport.opts,
  )
}

object FpPipelinedMacroAddBf16 extends App {
  ExportForAnalysis(
    new FpPipelinedMacroAdd(FpFormat.Bf16, FpFormat.Bf16, FpFormat.Bf16, 2),
    args,
    FpExport.opts,
  )
}
