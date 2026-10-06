package harith.fp

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis
import harith.uint._

/**
  * A pipelined floating-point multiplier.
  *
  * The significand product is computed by a pipelined [[harith.uint.UIntPipelinedArrayMul]] whose
  * register layers are `stages`; the floating-point control is delayed by the same number of cycles
  * so that the rounding and special handling line up with the product. `stages = 0` makes the
  * multiplier combinational, equivalently to [[FpArrayMul]]. NaN is canonical, per RISC-V.
  *
  * Note: only the significand multiplier is pipelined internally by the underlying
  * [[harith.uint.UIntPipelinedArrayMul]]; the floating-point control is a plain register queue
  * matched to the multiplier latency and the rounding/special logic after it stays combinational,
  * so the effective depth of that part depends on EDA retiming.
  *
  * fpga@fp32-2cyc: delay[i/o/max] = 6.057ns/22.781ns/22.210ns  area = 1889luts + 203ff
  *
  * 55nm@fp32-2cyc: delay[i/o/max] = 2.0752ns/8.4064ns/7.2036ns  area = 9651.32um²
  *
  * @param aFmt   The format of the first operand
  * @param bFmt   The format of the second operand
  * @param outFmt The format of the result
  * @param stages The number of pipeline register layers, which is also the latency
  * @param policy The numeric policy
  */
class FpPipelinedMul(
    val aFmt:   FpFormat,
    val bFmt:   FpFormat,
    val outFmt: FpFormat,
    val stages: Int,
    policy:     FpPolicy = FpPolicy(),
) extends FpMul {
  require(stages >= 0, "stages must be non-negative")

  override def latency: Int = stages

  val io = IO(new FpMulIO(aFmt, bFmt, outFmt))

  val a = FpUtils.decode(io.src1, aFmt)
  val b = FpUtils.decode(io.src2, bFmt)

  val aZero = if (policy.daz) a.isZero || a.isSubnormal else a.isZero
  val bZero = if (policy.daz) b.isZero || b.isSubnormal else b.isZero

  val sign  = a.sign ^ b.sign
  val w     = math.max(aFmt.manWidth, bFmt.manWidth) + 1
  val mul   = Module(new UIntPipelinedArrayMul(w, stages))
  val eProd = a.exp + b.exp - (aFmt.manWidth + bFmt.manWidth).S(FpUtils.EW.W)

  mul.io.src1 := a.sig.pad(w)
  mul.io.src2 := b.sig.pad(w)

  val signR  = FpUtils.pipe(sign, stages)
  val eProdR = FpUtils.pipe(eProd, stages)
  val rmR    = FpUtils.pipe(io.rm, stages)
  val aZeroR = FpUtils.pipe(aZero, stages)
  val bZeroR = FpUtils.pipe(bZero, stages)
  val aInfR  = FpUtils.pipe(a.isInf, stages)
  val bInfR  = FpUtils.pipe(b.isInf, stages)
  val aNanR  = FpUtils.pipe(a.isNaN, stages)
  val bNanR  = FpUtils.pipe(b.isNaN, stages)

  val rounded = FpUtils.roundPack(signR, mul.io.output, eProdR, outFmt, rmR, policy)

  val nanCase  = aNanR || bNanR || (aInfR && bZeroR) || (aZeroR && bInfR)
  val infCase  = !nanCase && (aInfR || bInfR)
  val zeroCase = !nanCase && !infCase && (aZeroR || bZeroR)
  val special  = nanCase || infCase || zeroCase
  val invalid  = (aInfR && bZeroR) || (aZeroR && bInfR)

  io.output := Mux(
    nanCase,
    outFmt.canonicalNaN,
    Mux(
      infCase,
      Cat(signR, outFmt.infinityMag),
      Mux(zeroCase, outFmt.zero(signR), rounded.bits),
    ),
  )

  io.fflags.nx := Mux(special, false.B, rounded.nx)
  io.fflags.uf := Mux(special, false.B, rounded.uf)
  io.fflags.of := Mux(special, false.B, rounded.of)
  io.fflags.dz := false.B
  io.fflags.nv := invalid
}

object FpPipelinedMul extends App {
  ExportForAnalysis(
    new FpPipelinedMul(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, 2),
    args,
    FpExport.opts,
  )
}
