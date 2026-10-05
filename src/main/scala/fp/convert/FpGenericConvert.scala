package harith.fp

import chisel3._
import chisel3.util._
import hammer.Export

/**
  * A floating-point format converter.
  *
  * The value is decoded to its exact significand and exponent and re-rounded to the destination
  * format. Special values map to the destination encoding; NaN is canonical, per RISC-V.
  *
  * fpga@fp32: delay = 21.135ns  area = 489luts + 0ff
  *
  * 55nm@fp32: delay = 4.5429ns  area = 769.44um²
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
  * fpga@bf16->fp32: delay = 18.529ns  area = 310luts + 0ff
  *
  * 55nm@bf16->fp32: delay = 1.6156ns  area = 86.24um²
  *
  */
class Bf16ToFp32 extends FpGenericConvert(FpFormat.Bf16, FpFormat.Fp32)

/**
  * A float16 to float32 converter.
  *
  * fpga@fp16->fp32: delay = 16.793ns  area = 281luts + 0ff
  *
  * 55nm@fp16->fp32: delay = 2.6403ns  area = 225.40um²
  *
  */
class Fp16ToFp32 extends FpGenericConvert(FpFormat.Fp16, FpFormat.Fp32)

/**
  * A float32 to float16 converter.
  *
  * fpga@fp32->fp16: delay = 21.135ns  area = 489luts + 0ff
  *
  * 55nm@fp32->fp16: delay = 4.5429ns  area = 769.44um²
  *
  */
class Fp32ToFp16 extends FpGenericConvert(FpFormat.Fp32, FpFormat.Fp16)

/**
  * A float32 to bfloat16 converter.
  *
  * fpga@fp32->bf16: delay = 21.200ns  area = 470luts + 0ff
  *
  * 55nm@fp32->bf16: delay = 3.3093ns  area = 365.96um²
  *
  */
class Fp32ToBf16 extends FpGenericConvert(FpFormat.Fp32, FpFormat.Bf16)

/**
  * A float64 to float32 converter.
  *
  * fpga@fp64->fp32: delay = 23.967ns  area = 1035luts + 0ff
  *
  * 55nm@fp64->fp32: delay = 6.9519ns  area = 1830.08um²
  *
  */
class Fp64ToFp32 extends FpGenericConvert(FpFormat.Fp64, FpFormat.Fp32)

/**
  * A float32 to float64 converter.
  *
  * fpga@fp32->fp64: delay = 22.046ns  area = 672luts + 0ff
  *
  * 55nm@fp32->fp64: delay = 3.6411ns  area = 582.12um²
  *
  */
class Fp32ToFp64 extends FpGenericConvert(FpFormat.Fp32, FpFormat.Fp64)

object FpGenericConvert extends App {
  Export(new FpGenericConvert(FpFormat.Fp32, FpFormat.Fp16), args, FpExport.opts)
}

object Fp16ToFp32 extends App { Export(new Fp16ToFp32, args, FpExport.opts) }
object Bf16ToFp32 extends App { Export(new Bf16ToFp32, args, FpExport.opts) }
object Fp32ToFp16 extends App { Export(new Fp32ToFp16, args, FpExport.opts) }
object Fp32ToBf16 extends App { Export(new Fp32ToBf16, args, FpExport.opts) }
object Fp64ToFp32 extends App { Export(new Fp64ToFp32, args, FpExport.opts) }
object Fp32ToFp64 extends App { Export(new Fp32ToFp64, args, FpExport.opts) }
