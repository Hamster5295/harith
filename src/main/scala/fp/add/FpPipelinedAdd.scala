package harith.fp

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis
import harith.uint._

/**
  * A pipelined floating-point adder.
  *
  * The wide alignment sum is computed by a pipelined
  * [[harith.uint.UIntPipelinedPrefixAdd]] whose register layers are `stages`, while the operand
  * decode, special handling and rounding stay combinational on the delayed operands. `stages = 0`
  * makes the adder combinational. NaN is canonical, per RISC-V.
  *
  * Note: only the wide alignment adder is pipelined internally by the underlying
  * [[harith.uint.UIntPipelinedPrefixAdd]]; the floating-point control is a plain register queue
  * matched to the adder latency and the decode/rounding logic around it stays combinational, so the
  * effective depth of that part depends on EDA retiming.
  *
  * fpga@fp32-2cyc: delay[i/o/max] = 19.973ns/25.614ns/25.022ns  area = 4952luts + 627ff
  *
  * 55nm@fp32-2cyc: delay[i/o/max] = 8.5569ns/21.8746ns/21.8746ns  area = 17788.96um²
  *
  * @param aFmt   The format of the first operand
  * @param bFmt   The format of the second operand
  * @param outFmt The format of the result
  * @param stages The number of pipeline register layers, which is also the latency
  * @param policy The numeric policy
  */
class FpPipelinedAdd(
    val aFmt:   FpFormat,
    val bFmt:   FpFormat,
    val outFmt: FpFormat,
    val stages: Int,
    policy:     FpPolicy = FpPolicy(),
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
  val adder = Module(new UIntPipelinedPrefixAdd(w0 + 1, PrefixStyle.KoggeStone, stages))
  val total = adder.io.output(w0, 0).asSInt
  val tSign = total(w0)
  val tMag  = Mux(tSign, (-total).asUInt, total.asUInt)

  adder.io.src1  := aVal0.pad(w0 + 1).asUInt
  adder.io.src2  := bVal0.pad(w0 + 1).asUInt
  adder.io.carry := false.B

  val src1R = FpUtils.pipe(io.src1, stages)
  val src2R = FpUtils.pipe(io.src2, stages)
  val rmR   = FpUtils.pipe(io.rm, stages)
  val eWR   = FpUtils.pipe(eW0, stages)
  val stR   = FpUtils.pipe(ast0 || bst0, stages)

  val a = FpUtils.decode(src1R, aFmt)
  val b = FpUtils.decode(src2R, bFmt)

  val aZero = if (policy.daz) a.isZero || a.isSubnormal else a.isZero
  val bZero = if (policy.daz) b.isZero || b.isSubnormal else b.isZero

  val expA  = a.exp - aFmt.manWidth.S(FpUtils.EW.W)
  val expB  = b.exp - bFmt.manWidth.S(FpUtils.EW.W)
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

object FpPipelinedAdd extends App {
  ExportForAnalysis(
    new FpPipelinedAdd(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, 2),
    args,
    FpExport.opts,
  )
}
