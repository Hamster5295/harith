package harith.fp

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis
import harith.uint._

/**
  * A pipelined floating-point fused multiply-adder.
  *
  * The significand product is computed by a pipelined
  * [[harith.uint.UIntPipelinedArrayMul]] whose register layers are `stages`, then the alignment,
  * addition and rounding are combinational on the delayed operands. `stages = 0` makes the FMA
  * combinational. NaN is canonical, per RISC-V.
  *
  * Note: only the significand product is pipelined internally by the underlying
  * [[harith.uint.UIntPipelinedArrayMul]]; the operands are delayed by a plain register queue matched
  * to the multiplier latency and the whole alignment/add/round tail stays combinational, so the
  * effective depth of that part depends on EDA retiming.
  *
  * fpga@fp64-2cyc: delay[i/o/max] = 12.581ns/63.282ns/62.711ns  area = 17188luts + 696ff
  *
  * 55nm@fp64-2cyc: delay[i/o/max] = 4.8029ns/31.7407ns/31.7407ns  area = 51201.08um²
  *
  * fpga@fp32-2cyc: delay[i/o/max] = 6.065ns/47.980ns/46.992ns  area = 6038luts + 327ff
  *
  * 55nm@fp32-2cyc: delay[i/o/max] = 2.0449ns/21.9171ns/21.9171ns  area = 17519.04um²
  *
  * fpga@fp16-2cyc: delay[i/o/max] = 3.137ns/40.049ns/39.478ns  area = 2632luts + 160ff
  *
  * 55nm@fp16-2cyc: delay[i/o/max] = 0.9560ns/16.4629ns/16.4629ns  area = 7754.32um²
  *
  * fpga@bf16-2cyc: delay[i/o/max] = 2.712ns/37.846ns/37.275ns  area = 2294luts + 143ff
  *
  * 55nm@bf16-2cyc: delay[i/o/max] = 0.5774ns/13.2360ns/13.2360ns  area = 6961.64um²
  *
  * @param aFmt   The format of the multiplicand
  * @param bFmt   The format of the multiplier
  * @param cFmt   The format of the addend
  * @param outFmt The format of the result
  * @param stages The number of pipeline register layers, which is also the latency
  * @param policy The numeric policy
  */
class FpPipelinedFma(
    val aFmt:   FpFormat,
    val bFmt:   FpFormat,
    val cFmt:   FpFormat,
    val outFmt: FpFormat,
    val stages: Int,
    policy:     FpPolicy = FpPolicy(),
) extends FpFma {
  require(stages >= 0, "stages must be non-negative")

  override def latency: Int = stages

  val io = IO(new FpFmaIO(aFmt, bFmt, cFmt, outFmt))

  val a0 = FpUtils.decode(io.src1, aFmt)
  val b0 = FpUtils.decode(io.src2, bFmt)

  val mw   = math.max(aFmt.manWidth, bFmt.manWidth) + 1
  val mul  = Module(new UIntPipelinedArrayMul(mw, stages))
  val prod = mul.io.output

  mul.io.src1 := a0.sig.pad(mw)
  mul.io.src2 := b0.sig.pad(mw)

  val src1R = FpUtils.pipe(io.src1, stages)
  val src2R = FpUtils.pipe(io.src2, stages)
  val addR  = FpUtils.pipe(io.add, stages)
  val rmR   = FpUtils.pipe(io.rm, stages)

  val a = FpUtils.decode(src1R, aFmt)
  val b = FpUtils.decode(src2R, bFmt)
  val c = FpUtils.decode(addR, cFmt)

  val aZero = if (policy.daz) a.isZero || a.isSubnormal else a.isZero
  val bZero = if (policy.daz) b.isZero || b.isSubnormal else b.isZero
  val cZero = if (policy.daz) c.isZero || c.isSubnormal else c.isZero

  val signP = a.sign ^ b.sign
  val x     = a.exp + b.exp - (aFmt.manWidth + bFmt.manWidth).S(FpUtils.EW.W)
  val csig  = c.sig
  val y     = c.exp - cFmt.manWidth.S(FpUtils.EW.W)

  val keep = outFmt.manWidth + 12
  val pP   = FpUtils.msbIndex(prod)
  val pC   = FpUtils.msbIndex(csig)
  val eP   = x + pP.pad(FpUtils.EW).asSInt
  val eC   = y + pC.pad(FpUtils.EW).asSInt
  val eW   = Mux(eP > eC, eP, eC) - keep.S(FpUtils.EW.W)

  val (psh, pst) = FpUtils.alignToExp(prod, x, eW, keep)
  val (csh, cst) = FpUtils.alignToExp(csig, y, eW, keep)

  val pVal  = Mux(signP, -psh, psh)
  val cVal  = Mux(c.sign, -csh, csh)
  val sum   = pVal.pad(pVal.getWidth + 1) + cVal.pad(pVal.getWidth + 1)
  val tSign = sum(pVal.getWidth)
  val tMag  = Mux(tSign, (-sum).asUInt, sum.asUInt)

  val general = FpUtils.roundPack(tSign, tMag, eW, outFmt, rmR, policy, pst || cst)
  val cRound  = FpUtils.roundPack(c.sign, csig, y, outFmt, rmR, policy)
  val pRound  = FpUtils.roundPack(signP, prod, x, outFmt, rmR, policy)

  val prodInf  = !aZero && !bZero && (a.isInf || b.isInf)
  val invalid  = (a.isInf && bZero) || (aZero && b.isInf)
  val infMinus = prodInf && c.isInf && (signP =/= c.sign)
  val nanCase  = a.isNaN || b.isNaN || c.isNaN || invalid || infMinus
  val infCase  = !nanCase && (prodInf || c.isInf)
  val infSign  = Mux(prodInf, signP, c.sign)

  val prodZero = !nanCase && !infCase && (aZero || bZero)
  val bothZero = prodZero && cZero
  val mulZero  = prodZero && !cZero
  val addZero  = !nanCase && !infCase && !prodZero && cZero

  val zeroSign = Mux(signP === c.sign, signP, rmR === 2.U)

  val isSpecial = nanCase || infCase || bothZero
  val selNx = Mux(isSpecial, false.B, Mux(mulZero, cRound.nx, Mux(addZero, pRound.nx, general.nx)))
  val selUf = Mux(isSpecial, false.B, Mux(mulZero, cRound.uf, Mux(addZero, pRound.uf, general.uf)))
  val selOf = Mux(isSpecial, false.B, Mux(mulZero, cRound.of, Mux(addZero, pRound.of, general.of)))

  io.output := Mux(
    nanCase,
    outFmt.canonicalNaN,
    Mux(
      infCase,
      Cat(infSign, outFmt.infinityMag),
      Mux(
        bothZero,
        outFmt.zero(zeroSign),
        Mux(mulZero, cRound.bits, Mux(addZero, pRound.bits, general.bits)),
      ),
    ),
  )

  io.fflags.nx := selNx
  io.fflags.uf := selUf
  io.fflags.of := selOf
  io.fflags.dz := false.B
  io.fflags.nv := invalid || infMinus
}

object FpPipelinedFmaFp64 extends App {
  ExportForAnalysis(
    new FpPipelinedFma(FpFormat.Fp64, FpFormat.Fp64, FpFormat.Fp64, FpFormat.Fp64, 2),
    args,
    FpExport.opts,
  )
}

object FpPipelinedFmaFp32 extends App {
  ExportForAnalysis(
    new FpPipelinedFma(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, 2),
    args,
    FpExport.opts,
  )
}

object FpPipelinedFmaFp16 extends App {
  ExportForAnalysis(
    new FpPipelinedFma(FpFormat.Fp16, FpFormat.Fp16, FpFormat.Fp16, FpFormat.Fp16, 2),
    args,
    FpExport.opts,
  )
}

object FpPipelinedFmaBf16 extends App {
  ExportForAnalysis(
    new FpPipelinedFma(FpFormat.Bf16, FpFormat.Bf16, FpFormat.Bf16, FpFormat.Bf16, 2),
    args,
    FpExport.opts,
  )
}
