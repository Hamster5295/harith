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
  * fpga@fp32: delay[i/o/max] = 18.533ns/19.522ns/18.533ns  area = 484luts + 0ff
  *
  * 55nm@fp32: delay[i/o/max] = 4.4324ns/4.4324ns/4.4324ns  area = 853.72um²
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

/**
  * A bfloat16 to float32 converter.
  *
  * fpga@bf16->fp32: delay[i/o/max] = 15.718ns/16.290ns/15.718ns  area = 329luts + 0ff
  *
  * 55nm@bf16->fp32: delay[i/o/max] = 1.2570ns/1.2570ns/1.2570ns  area = 74.20um²
  *
  */
class Bf16ToFp32 extends FpGenericConvert(FpFormat.Bf16, FpFormat.Fp32)

/**
  * A float16 to float32 converter.
  *
  * fpga@fp16->fp32: delay[i/o/max] = 14.596ns/15.187ns/14.596ns  area = 315luts + 0ff
  *
  * 55nm@fp16->fp32: delay[i/o/max] = 2.1520ns/2.1520ns/2.1520ns  area = 210.84um²
  *
  */
class Fp16ToFp32 extends FpGenericConvert(FpFormat.Fp16, FpFormat.Fp32)

/**
  * A float32 to float16 converter.
  *
  * fpga@fp32->fp16: delay[i/o/max] = 18.533ns/19.522ns/18.533ns  area = 484luts + 0ff
  *
  * 55nm@fp32->fp16: delay[i/o/max] = 4.4324ns/4.4324ns/4.4324ns  area = 853.72um²
  *
  */
class Fp32ToFp16 extends FpGenericConvert(FpFormat.Fp32, FpFormat.Fp16)

/**
  * A float32 to bfloat16 converter.
  *
  * fpga@fp32->bf16: delay[i/o/max] = 18.194ns/19.183ns/18.194ns  area = 478luts + 0ff
  *
  * 55nm@fp32->bf16: delay[i/o/max] = 2.8542ns/2.8542ns/2.8542ns  area = 351.12um²
  *
  */
class Fp32ToBf16 extends FpGenericConvert(FpFormat.Fp32, FpFormat.Bf16)

/**
  * A float64 to float32 converter.
  *
  * fpga@fp64->fp32: delay[i/o/max] = 21.106ns/21.557ns/21.106ns  area = 1040luts + 0ff
  *
  * 55nm@fp64->fp32: delay[i/o/max] = 6.4557ns/6.4557ns/6.4557ns  area = 1778.00um²
  *
  */
class Fp64ToFp32 extends FpGenericConvert(FpFormat.Fp64, FpFormat.Fp32)

/**
  * A float32 to float64 converter.
  *
  * fpga@fp32->fp64: delay[i/o/max] = 19.632ns/20.420ns/19.632ns  area = 707luts + 0ff
  *
  * 55nm@fp32->fp64: delay[i/o/max] = 3.5764ns/3.5764ns/3.5764ns  area = 565.32um²
  *
  */
class Fp32ToFp64 extends FpGenericConvert(FpFormat.Fp32, FpFormat.Fp64)

object FpGenericConvert extends App {
  ExportForAnalysis(new FpGenericConvert(FpFormat.Fp32, FpFormat.Fp16), args, FpExport.opts)
}

object Fp16ToFp32 extends App { ExportForAnalysis(new Fp16ToFp32, args, FpExport.opts) }
object Bf16ToFp32 extends App { ExportForAnalysis(new Bf16ToFp32, args, FpExport.opts) }
object Fp32ToFp16 extends App { ExportForAnalysis(new Fp32ToFp16, args, FpExport.opts) }
object Fp32ToBf16 extends App { ExportForAnalysis(new Fp32ToBf16, args, FpExport.opts) }
object Fp64ToFp32 extends App { ExportForAnalysis(new Fp64ToFp32, args, FpExport.opts) }
object Fp32ToFp64 extends App { ExportForAnalysis(new Fp32ToFp64, args, FpExport.opts) }
