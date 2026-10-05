package harith.fp

import chisel3._
import chisel3.util._
import hammer.Export
import harith.uint._

/**
  * A floating-point multiplier with an inferred significand multiplier.
  *
  * delay = 28.951, area = 1065 @32bit@fpga
  * delay = 12.0162, area = 11671.24 @32bit@55nm
  *
  * @param aFmt   The format of the first operand
  * @param bFmt   The format of the second operand
  * @param outFmt The format of the result
  * @param policy The numeric policy
  */
class FpMacroMul(aFmt: FpFormat, bFmt: FpFormat, outFmt: FpFormat, policy: FpPolicy = FpPolicy())
    extends FpMulBase(aFmt, bFmt, outFmt, policy, w => Module(new UIntMacroMul(w)))

object FpMacroMul extends App {
  Export(new FpMacroMul(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32), args, FpExport.opts)
}

/**
  * A bfloat16 by bfloat16 to float32 multiplier.
  */
class FpBf16Fp32Mul extends FpMacroMul(FpFormat.Bf16, FpFormat.Bf16, FpFormat.Fp32)

/**
  * A float16 by float16 to float32 multiplier.
  */
class Fp16Fp32Mul extends FpMacroMul(FpFormat.Fp16, FpFormat.Fp16, FpFormat.Fp32)

/**
  * A float32 multiplier.
  */
class Fp32Mul extends FpMacroMul(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32)

/**
  * A float64 multiplier.
  */
class Fp64Mul extends FpMacroMul(FpFormat.Fp64, FpFormat.Fp64, FpFormat.Fp64)
