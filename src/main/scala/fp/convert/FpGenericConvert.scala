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
  * delay = 21.135, area = 489 @32bit@fpga
  * delay = 4.5429, area = 769.44 @32bit@55nm
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
  */
class Bf16ToFp32 extends FpGenericConvert(FpFormat.Bf16, FpFormat.Fp32)

/**
  * A float16 to float32 converter.
  */
class Fp16ToFp32 extends FpGenericConvert(FpFormat.Fp16, FpFormat.Fp32)

/**
  * A float32 to float16 converter.
  */
class Fp32ToFp16 extends FpGenericConvert(FpFormat.Fp32, FpFormat.Fp16)

/**
  * A float32 to bfloat16 converter.
  */
class Fp32ToBf16 extends FpGenericConvert(FpFormat.Fp32, FpFormat.Bf16)

/**
  * A float64 to float32 converter.
  */
class Fp64ToFp32 extends FpGenericConvert(FpFormat.Fp64, FpFormat.Fp32)

/**
  * A float32 to float64 converter.
  */
class Fp32ToFp64 extends FpGenericConvert(FpFormat.Fp32, FpFormat.Fp64)

object FpGenericConvert extends App {
  Export(new FpGenericConvert(FpFormat.Fp32, FpFormat.Fp16), args, FpExport.opts)
}
