package harith.fp

import chisel3._
import chisel3.util._
import hammer.Export
import harith.uint._

/**
  * A floating-point adder with an inferred alignment adder.
  *
  * delay = 43.206, area = 3923 @32bit@fpga
  * delay = 14.3314, area = 7254.24 @32bit@55nm
  *
  * @param aFmt   The format of the first operand
  * @param bFmt   The format of the second operand
  * @param outFmt The format of the result
  * @param policy The numeric policy
  */
class FpMacroAdd(aFmt: FpFormat, bFmt: FpFormat, outFmt: FpFormat, policy: FpPolicy = FpPolicy())
    extends FpAddBase(aFmt, bFmt, outFmt, policy, w => Module(new UIntMacroAdd(w)))

object FpMacroAdd extends App {
  Export(new FpMacroAdd(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32), args, FpExport.opts)
}

/**
  * A bfloat16 plus float32 to float32 adder.
  */
class FpBf16Fp32Add extends FpMacroAdd(FpFormat.Bf16, FpFormat.Fp32, FpFormat.Fp32)

/**
  * A float16 plus float32 to float32 adder.
  */
class Fp16Fp32Add extends FpMacroAdd(FpFormat.Fp16, FpFormat.Fp32, FpFormat.Fp32)

/**
  * A float32 adder.
  */
class Fp32Add extends FpMacroAdd(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32)

/**
  * A float64 adder.
  */
class Fp64Add extends FpMacroAdd(FpFormat.Fp64, FpFormat.Fp64, FpFormat.Fp64)
