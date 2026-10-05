package harith.fp

import chisel3._
import chisel3.util._
import hammer.Export
import harith.uint._

/**
  * A floating-point fused multiply-adder with inferred significand and alignment datapaths.
  *
  * fpga@fp32: delay = 53.886ns  area = 5122luts + 0ff
  *
  * 55nm@fp32: delay = 51.6054ns  area = 26082.28um²
  *
  * @param aFmt   The format of the multiplicand
  * @param bFmt   The format of the multiplier
  * @param cFmt   The format of the addend
  * @param outFmt The format of the result
  * @param policy The numeric policy
  */
class FpMacroFma(
    aFmt:   FpFormat,
    bFmt:   FpFormat,
    cFmt:   FpFormat,
    outFmt: FpFormat,
    policy: FpPolicy = FpPolicy(),
) extends FpFmaBase(
      aFmt,
      bFmt,
      cFmt,
      outFmt,
      policy,
      w => Module(new UIntMacroMul(w)),
      w => Module(new UIntMacroAdd(w)),
    )

object FpMacroFma extends App {
  Export(
    new FpMacroFma(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32),
    args,
    FpExport.opts,
  )
}

/**
  * A bfloat16 by bfloat16 plus float32 to float32 fused multiply-adder.
  *
  * fpga@bf16: delay = 47.558ns  area = 3819luts + 0ff
  *
  * 55nm@bf16: delay = 21.7874ns  area = 10065.16um²
  *
  */
class FpBf16Fp32Fma extends FpMacroFma(FpFormat.Bf16, FpFormat.Bf16, FpFormat.Fp32, FpFormat.Fp32)

/**
  * A float16 by float16 plus float32 to float32 fused multiply-adder.
  *
  * fpga@fp16: delay = 48.020ns  area = 3853luts + 0ff
  *
  * 55nm@fp16: delay = 16.0147ns  area = 7742.84um²
  *
  */
class Fp16Fp32Fma extends FpMacroFma(FpFormat.Fp16, FpFormat.Fp16, FpFormat.Fp32, FpFormat.Fp32)

/**
  * A float32 fused multiply-adder.
  *
  * fpga@fp32: delay = 52.483ns  area = 4992luts + 0ff
  *
  * 55nm@fp32: delay = 43.2990ns  area = 26617.64um²
  *
  */
class Fp32Fma extends FpMacroFma(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32)

/**
  * A float64 fused multiply-adder.
  *
  * fpga@fp64: delay = 67.130ns  area = 11054luts + 0ff
  *
  * 55nm@fp64: delay = 66.4858ns  area = 77199.08um²
  *
  */
class Fp64Fma extends FpMacroFma(FpFormat.Fp64, FpFormat.Fp64, FpFormat.Fp64, FpFormat.Fp64)

object FpBf16Fp32Fma extends App { Export(new FpBf16Fp32Fma, args, FpExport.opts) }
object Fp16Fp32Fma   extends App { Export(new Fp16Fp32Fma, args, FpExport.opts)   }
object Fp32Fma       extends App { Export(new Fp32Fma, args, FpExport.opts)       }
object Fp64Fma       extends App { Export(new Fp64Fma, args, FpExport.opts)       }
