package harith.fp

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis

/**
  * A pipelined floating-point format converter.
  *
  * The operand is delayed by `stages` and the re-rounding is combinational on the delayed operand,
  * so the throughput is one conversion per cycle. `stages = 0` makes the converter combinational,
  * equivalently to [[FpGenericConvert]]. NaN is canonical, per RISC-V.
  *
  * Note: the `stages` register layers are a plain delay queue on the operands, not registers placed
  * at internal cut points; the conversion logic between them stays combinational and the intended
  * pipeline depth is only realised once the EDA tool retimes the queue into the logic.
  *
  * fpga@fp64->fp32-2cyc: delay[i/o/max] = 1.364ns/21.083ns/20.512ns  area = 1073luts + 132ff
  *
  * 55nm@fp64->fp32-2cyc: delay[i/o/max] = 0.0053ns/6.8752ns/6.8752ns  area = 2601.76um²
  *
  * fpga@fp64->fp16-2cyc: delay[i/o/max] = 1.364ns/20.703ns/19.435ns  area = 931luts + 132ff
  *
  * 55nm@fp64->fp16-2cyc: delay[i/o/max] = 0.0053ns/5.6125ns/5.6125ns  area = 1907.36um²
  *
  * fpga@fp64->bf16-2cyc: delay[i/o/max] = 1.364ns/20.170ns/19.506ns  area = 952luts + 132ff
  *
  * 55nm@fp64->bf16-2cyc: delay[i/o/max] = 0.0053ns/5.0410ns/5.0410ns  area = 1961.40um²
  *
  * fpga@fp32->fp64-2cyc: delay[i/o/max] = 1.364ns/19.703ns/19.132ns  area = 639luts + 68ff
  *
  * 55nm@fp32->fp64-2cyc: delay[i/o/max] = 0.0053ns/4.1978ns/4.1978ns  area = 959.00um²
  *
  * fpga@fp32->fp16-2cyc: delay[i/o/max] = 1.364ns/19.160ns/18.172ns  area = 504luts + 68ff
  *
  * 55nm@fp32->fp16-2cyc: delay[i/o/max] = 0.0053ns/4.8233ns/4.8233ns  area = 1248.52um²
  *
  * fpga@fp32->bf16-2cyc: delay[i/o/max] = 1.364ns/18.759ns/17.771ns  area = 487luts + 68ff
  *
  * 55nm@fp32->bf16-2cyc: delay[i/o/max] = 0.0053ns/2.8789ns/2.8789ns  area = 785.96um²
  *
  * fpga@fp16->fp64-2cyc: delay[i/o/max] = 1.364ns/17.633ns/16.653ns  area = 506luts + 36ff
  *
  * 55nm@fp16->fp64-2cyc: delay[i/o/max] = 0.0053ns/2.5191ns/2.5191ns  area = 413.56um²
  *
  * fpga@fp16->fp32-2cyc: delay[i/o/max] = 1.364ns/14.354ns/13.783ns  area = 293luts + 35ff
  *
  * 55nm@fp16->fp32-2cyc: delay[i/o/max] = 0.0053ns/2.3150ns/2.3150ns  area = 423.08um²
  *
  * fpga@fp16->bf16-2cyc: delay[i/o/max] = 1.364ns/13.677ns/13.106ns  area = 242luts + 35ff
  *
  * 55nm@fp16->bf16-2cyc: delay[i/o/max] = 0.0053ns/2.5211ns/2.5211ns  area = 557.48um²
  *
  * fpga@bf16->fp64-2cyc: delay[i/o/max] = 1.364ns/16.641ns/16.070ns  area = 380luts + 36ff
  *
  * 55nm@bf16->fp64-2cyc: delay[i/o/max] = 0.0053ns/2.8729ns/2.8729ns  area = 445.20um²
  *
  * fpga@bf16->fp32-2cyc: delay[i/o/max] = 1.364ns/15.865ns/15.294ns  area = 304luts + 36ff
  *
  * 55nm@bf16->fp32-2cyc: delay[i/o/max] = 0.0053ns/1.4419ns/1.4419ns  area = 271.60um²
  *
  * fpga@bf16->fp16-2cyc: delay[i/o/max] = 1.364ns/16.082ns/15.510ns  area = 280luts + 36ff
  *
  * 55nm@bf16->fp16-2cyc: delay[i/o/max] = 0.0053ns/4.6696ns/4.6696ns  area = 827.12um²
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

object FpPipelinedConvertFp64ToFp32 extends App {
  ExportForAnalysis(new FpPipelinedConvert(FpFormat.Fp64, FpFormat.Fp32, 2), args, FpExport.opts)
}

object FpPipelinedConvertFp64ToFp16 extends App {
  ExportForAnalysis(new FpPipelinedConvert(FpFormat.Fp64, FpFormat.Fp16, 2), args, FpExport.opts)
}

object FpPipelinedConvertFp64ToBf16 extends App {
  ExportForAnalysis(new FpPipelinedConvert(FpFormat.Fp64, FpFormat.Bf16, 2), args, FpExport.opts)
}

object FpPipelinedConvertFp32ToFp64 extends App {
  ExportForAnalysis(new FpPipelinedConvert(FpFormat.Fp32, FpFormat.Fp64, 2), args, FpExport.opts)
}

object FpPipelinedConvertFp32ToFp16 extends App {
  ExportForAnalysis(new FpPipelinedConvert(FpFormat.Fp32, FpFormat.Fp16, 2), args, FpExport.opts)
}

object FpPipelinedConvertFp32ToBf16 extends App {
  ExportForAnalysis(new FpPipelinedConvert(FpFormat.Fp32, FpFormat.Bf16, 2), args, FpExport.opts)
}

object FpPipelinedConvertFp16ToFp64 extends App {
  ExportForAnalysis(new FpPipelinedConvert(FpFormat.Fp16, FpFormat.Fp64, 2), args, FpExport.opts)
}

object FpPipelinedConvertFp16ToFp32 extends App {
  ExportForAnalysis(new FpPipelinedConvert(FpFormat.Fp16, FpFormat.Fp32, 2), args, FpExport.opts)
}

object FpPipelinedConvertFp16ToBf16 extends App {
  ExportForAnalysis(new FpPipelinedConvert(FpFormat.Fp16, FpFormat.Bf16, 2), args, FpExport.opts)
}

object FpPipelinedConvertBf16ToFp64 extends App {
  ExportForAnalysis(new FpPipelinedConvert(FpFormat.Bf16, FpFormat.Fp64, 2), args, FpExport.opts)
}

object FpPipelinedConvertBf16ToFp32 extends App {
  ExportForAnalysis(new FpPipelinedConvert(FpFormat.Bf16, FpFormat.Fp32, 2), args, FpExport.opts)
}

object FpPipelinedConvertBf16ToFp16 extends App {
  ExportForAnalysis(new FpPipelinedConvert(FpFormat.Bf16, FpFormat.Fp16, 2), args, FpExport.opts)
}
