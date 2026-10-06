package harith.fp

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis

/**
  * A floating-point format converter.
  *
  * The value is decoded to its exact significand and exponent and re-rounded to the destination
  * format. Special values map to the destination encoding; NaN is canonical, per RISC-V.
  *
  * fpga@fp64->fp32: delay[i/o/max] = 21.106ns/21.557ns/21.106ns  area = 1040luts + 0ff
  *
  * 55nm@fp64->fp32: delay[i/o/max] = 6.4557ns/6.4557ns/6.4557ns  area = 1778.00um²
  *
  * fpga@fp64->fp16: delay[i/o/max] = 20.787ns/21.529ns/20.787ns  area = 936luts + 0ff
  *
  * 55nm@fp64->fp16: delay[i/o/max] = 5.2308ns/5.2308ns/5.2308ns  area = 1114.96um²
  *
  * fpga@fp64->bf16: delay[i/o/max] = 21.005ns/21.691ns/21.005ns  area = 931luts + 0ff
  *
  * 55nm@fp64->bf16: delay[i/o/max] = 4.8884ns/4.8884ns/4.8342ns  area = 1086.12um²
  *
  * fpga@fp32->fp64: delay[i/o/max] = 19.632ns/20.420ns/19.632ns  area = 707luts + 0ff
  *
  * 55nm@fp32->fp64: delay[i/o/max] = 3.5764ns/3.5764ns/3.5764ns  area = 565.32um²
  *
  * fpga@fp32->fp16: delay[i/o/max] = 18.533ns/19.522ns/18.533ns  area = 484luts + 0ff
  *
  * 55nm@fp32->fp16: delay[i/o/max] = 4.4324ns/4.4324ns/4.4324ns  area = 853.72um²
  *
  * fpga@fp32->bf16: delay[i/o/max] = 18.194ns/19.183ns/18.194ns  area = 478luts + 0ff
  *
  * 55nm@fp32->bf16: delay[i/o/max] = 2.8542ns/2.8542ns/2.8542ns  area = 351.12um²
  *
  * fpga@fp16->fp64: delay[i/o/max] = 17.826ns/18.406ns/17.826ns  area = 486luts + 0ff
  *
  * 55nm@fp16->fp64: delay[i/o/max] = 2.4871ns/2.4871ns/2.4871ns  area = 232.96um²
  *
  * fpga@fp16->fp32: delay[i/o/max] = 14.596ns/15.187ns/14.596ns  area = 315luts + 0ff
  *
  * 55nm@fp16->fp32: delay[i/o/max] = 2.1520ns/2.1520ns/2.1520ns  area = 210.84um²
  *
  * fpga@fp16->bf16: delay[i/o/max] = 14.189ns/14.761ns/14.189ns  area = 243luts + 0ff
  *
  * 55nm@fp16->bf16: delay[i/o/max] = 2.3162ns/2.3162ns/2.3162ns  area = 331.80um²
  *
  * fpga@bf16->fp64: delay[i/o/max] = 17.372ns/17.952ns/17.372ns  area = 458luts + 0ff
  *
  * 55nm@bf16->fp64: delay[i/o/max] = 2.4125ns/2.4125ns/2.4125ns  area = 229.04um²
  *
  * fpga@bf16->fp32: delay[i/o/max] = 15.718ns/16.290ns/15.718ns  area = 329luts + 0ff
  *
  * 55nm@bf16->fp32: delay[i/o/max] = 1.2570ns/1.2570ns/1.2570ns  area = 74.20um²
  *
  * fpga@bf16->fp16: delay[i/o/max] = 15.857ns/16.437ns/15.857ns  area = 258luts + 0ff
  *
  * 55nm@bf16->fp16: delay[i/o/max] = 4.1644ns/4.1644ns/4.1644ns  area = 594.16um²
  *
  * @param inFmt  The input format
  * @param outFmt The output format
  * @param policy The numeric policy
  */
class FpGenericConvert(
    val inFmt:  FpFormat,
    val outFmt: FpFormat,
    policy:     FpPolicy = FpPolicy(),
) extends FpConvert {
  val io = IO(new FpConvertIO(inFmt, outFmt))

  val a = FpUtils.decode(io.src, inFmt)

  val exp     = a.exp - inFmt.manWidth.S(FpUtils.EW.W)
  val rounded = FpUtils.roundPack(a.sign, a.sig, exp, outFmt, io.rm, policy)

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

object Fp64ToFp32 extends App {
  ExportForAnalysis(new FpGenericConvert(FpFormat.Fp64, FpFormat.Fp32), args, FpExport.opts)
}

object Fp64ToFp16 extends App {
  ExportForAnalysis(new FpGenericConvert(FpFormat.Fp64, FpFormat.Fp16), args, FpExport.opts)
}

object Fp64ToBf16 extends App {
  ExportForAnalysis(new FpGenericConvert(FpFormat.Fp64, FpFormat.Bf16), args, FpExport.opts)
}

object Fp32ToFp64 extends App {
  ExportForAnalysis(new FpGenericConvert(FpFormat.Fp32, FpFormat.Fp64), args, FpExport.opts)
}

object Fp32ToFp16 extends App {
  ExportForAnalysis(new FpGenericConvert(FpFormat.Fp32, FpFormat.Fp16), args, FpExport.opts)
}

object Fp32ToBf16 extends App {
  ExportForAnalysis(new FpGenericConvert(FpFormat.Fp32, FpFormat.Bf16), args, FpExport.opts)
}

object Fp16ToFp64 extends App {
  ExportForAnalysis(new FpGenericConvert(FpFormat.Fp16, FpFormat.Fp64), args, FpExport.opts)
}

object Fp16ToFp32 extends App {
  ExportForAnalysis(new FpGenericConvert(FpFormat.Fp16, FpFormat.Fp32), args, FpExport.opts)
}

object Fp16ToBf16 extends App {
  ExportForAnalysis(new FpGenericConvert(FpFormat.Fp16, FpFormat.Bf16), args, FpExport.opts)
}

object Bf16ToFp64 extends App {
  ExportForAnalysis(new FpGenericConvert(FpFormat.Bf16, FpFormat.Fp64), args, FpExport.opts)
}

object Bf16ToFp32 extends App {
  ExportForAnalysis(new FpGenericConvert(FpFormat.Bf16, FpFormat.Fp32), args, FpExport.opts)
}

object Bf16ToFp16 extends App {
  ExportForAnalysis(new FpGenericConvert(FpFormat.Bf16, FpFormat.Fp16), args, FpExport.opts)
}
