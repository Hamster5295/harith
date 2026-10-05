package harith.fp

import chisel3._
import chisel3.util._
import hammer.Export

/**
  * A pipelined floating-point format converter.
  *
  * The operand is delayed by `stages` and the re-rounding is combinational on the delayed operand,
  * so the throughput is one conversion per cycle. `stages = 0` makes the converter combinational,
  * equivalently to [[FpGenericConvert]]. NaN is canonical, per RISC-V.
  *
  * @param inFmt  The input format
  * @param outFmt The output format
  * @param stages The number of pipeline register layers, which is also the latency
  * @param policy The numeric policy
  */
class FpPipelinedConvert(
    val inFmt:  FpFormat,
    val outFmt: FpFormat,
    val stages: Int,
    policy:     FpPolicy = FpPolicy(),
) extends FpConvert {
  require(stages >= 0, "stages must be non-negative")

  override def latency: Int = stages

  val io = IO(new FpConvertIO(inFmt, outFmt))

  val srcR = FpUtils.pipe(io.src, stages)
  val rmR  = FpUtils.pipe(io.rm, stages)

  val a = FpUtils.decode(srcR, inFmt)

  val exp     = a.exp - inFmt.manWidth.S(FpUtils.EW.W)
  val rounded = FpUtils.roundPack(a.sign, a.sig, exp, outFmt, rmR, policy)

  val nanCase  = a.isNaN
  val infCase  = !nanCase && a.isInf
  val zeroCase = !nanCase && !infCase && a.isZero

  io.output := Mux(
    nanCase,
    outFmt.canonicalNaN,
    Mux(infCase, Cat(a.sign, outFmt.infinityMag), Mux(zeroCase, outFmt.zero(a.sign), rounded.bits)),
  )

  io.fflags.nx := Mux(nanCase || infCase || zeroCase, false.B, rounded.nx)
  io.fflags.uf := Mux(nanCase || infCase || zeroCase, false.B, rounded.uf)
  io.fflags.of := Mux(nanCase || infCase || zeroCase, false.B, rounded.of)
  io.fflags.dz := false.B
  io.fflags.nv := false.B
}

object FpPipelinedConvert extends App {
  Export(new FpPipelinedConvert(FpFormat.Fp32, FpFormat.Fp16, 2), args, FpExport.opts)
}
