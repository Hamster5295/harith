package harith.fp

import chisel3._
import chisel3.util._
import hammer.Export
import harith.uint._

/**
  * A floating-point multiplier with an inferred significand multiplier.
  *
  * fpga@fp32: delay = 28.951ns  area = 1065luts + 0ff
  *
  * 55nm@fp32: delay = 12.0162ns  area = 11671.24um²
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
  *
  * fpga@bf16: delay = 23.834ns  area = 504luts + 0ff
  *
  * 55nm@bf16: delay = 6.4358ns  area = 2349.48um²
  *
  */
class FpBf16Fp32Mul extends FpMacroMul(FpFormat.Bf16, FpFormat.Bf16, FpFormat.Fp32)

/**
  * A float16 by float16 to float32 multiplier.
  *
  * fpga@fp16: delay = 23.482ns  area = 479luts + 0ff
  *
  * 55nm@fp16: delay = 4.5319ns  area = 2525.60um²
  *
  */
class Fp16Fp32Mul extends FpMacroMul(FpFormat.Fp16, FpFormat.Fp16, FpFormat.Fp32)

/**
  * A float32 multiplier.
  *
  * fpga@fp32: delay = 28.301ns  area = 968luts + 0ff
  *
  * 55nm@fp32: delay = 12.2959ns  area = 11529.00um²
  *
  */
class Fp32Mul extends FpMacroMul(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32)

/**
  * A float64 multiplier.
  *
  * fpga@fp64: delay = 36.608ns  area = 2463luts + 0ff
  *
  * 55nm@fp64: delay = 17.9020ns  area = 40382.44um²
  *
  */
class Fp64Mul extends FpMacroMul(FpFormat.Fp64, FpFormat.Fp64, FpFormat.Fp64)

object FpBf16Fp32Mul extends App { Export(new FpBf16Fp32Mul, args, FpExport.opts) }
object Fp16Fp32Mul   extends App { Export(new Fp16Fp32Mul, args, FpExport.opts)   }
object Fp32Mul       extends App { Export(new Fp32Mul, args, FpExport.opts)       }
object Fp64Mul       extends App { Export(new Fp64Mul, args, FpExport.opts)       }
