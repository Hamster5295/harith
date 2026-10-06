package harith.fp

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis
import harith.uint._

/**
  * A floating-point multiplier with an inferred significand multiplier.
  *
  * fpga@fp32: delay[i/o/max] = 24.908ns/25.495ns/24.908ns  area = 984luts + 0ff
  *
  * 55nm@fp32: delay[i/o/max] = 9.4294ns/9.4294ns/8.4766ns  area = 10503.08um²
  *
  * @param aFmt   The format of the first operand
  * @param bFmt   The format of the second operand
  * @param outFmt The format of the result
  * @param policy The numeric policy
  */
class FpMacroMul(aFmt: FpFormat, bFmt: FpFormat, outFmt: FpFormat, policy: FpPolicy = FpPolicy())
    extends FpMulBase(aFmt, bFmt, outFmt, policy, w => Module(new UIntMacroMul(w)))

object FpMacroMul extends App {
  ExportForAnalysis(
    new FpMacroMul(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32),
    args,
    FpExport.opts,
  )
}

/**
  * A bfloat16 by bfloat16 to float32 multiplier.
  *
  * fpga@bf16: delay[i/o/max] = 21.023ns/21.603ns/21.023ns  area = 529luts + 0ff
  *
  * 55nm@bf16: delay[i/o/max] = 5.9147ns/5.9147ns/4.9057ns  area = 2445.52um²
  *
  */
class FpBf16Fp32Mul extends FpMacroMul(FpFormat.Bf16, FpFormat.Bf16, FpFormat.Fp32)

/**
  * A float16 by float16 to float32 multiplier.
  *
  * fpga@fp16: delay[i/o/max] = 20.805ns/21.376ns/20.805ns  area = 514luts + 0ff
  *
  * 55nm@fp16: delay[i/o/max] = 4.3914ns/4.3914ns/2.8358ns  area = 2528.96um²
  *
  */
class Fp16Fp32Mul extends FpMacroMul(FpFormat.Fp16, FpFormat.Fp16, FpFormat.Fp32)

/**
  * A float32 multiplier.
  *
  * fpga@fp32: delay[i/o/max] = 24.908ns/25.495ns/24.908ns  area = 984luts + 0ff
  *
  * 55nm@fp32: delay[i/o/max] = 9.4294ns/9.4294ns/8.4766ns  area = 10503.08um²
  *
  */
class Fp32Mul extends FpMacroMul(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32)

/**
  * A float64 multiplier.
  *
  * fpga@fp64: delay[i/o/max] = 32.618ns/33.399ns/32.618ns  area = 2379luts + 0ff
  *
  * 55nm@fp64: delay[i/o/max] = 16.7999ns/16.7999ns/15.3632ns  area = 40251.68um²
  *
  */
class Fp64Mul extends FpMacroMul(FpFormat.Fp64, FpFormat.Fp64, FpFormat.Fp64)

object FpBf16Fp32Mul extends App { ExportForAnalysis(new FpBf16Fp32Mul, args, FpExport.opts) }
object Fp16Fp32Mul   extends App { ExportForAnalysis(new Fp16Fp32Mul, args, FpExport.opts)   }
object Fp32Mul       extends App { ExportForAnalysis(new Fp32Mul, args, FpExport.opts)       }
object Fp64Mul       extends App { ExportForAnalysis(new Fp64Mul, args, FpExport.opts)       }
