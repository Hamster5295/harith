package harith.fp

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis
import harith.uint._

/**
  * A floating-point multiplier with an inferred significand multiplier.
  *
  * fpga@fp64: delay[i/o/max] = 32.618ns/33.399ns/32.618ns  area = 2379luts + 0ff
  *
  * 55nm@fp64: delay[i/o/max] = 17.2452ns/17.2452ns/17.2452ns  area = 39994.92um²
  *
  * fpga@fp32: delay[i/o/max] = 24.908ns/25.495ns/24.908ns  area = 984luts + 0ff
  *
  * 55nm@fp32: delay[i/o/max] = 9.4441ns/9.4441ns/9.4441ns  area = 10452.12um²
  *
  * fpga@fp16: delay[i/o/max] = 23.001ns/23.596ns/23.001ns  area = 490luts + 0ff
  *
  * 55nm@fp16: delay[i/o/max] = 6.4737ns/6.4737ns/6.4737ns  area = 2972.48um²
  *
  * fpga@bf16: delay[i/o/max] = 20.551ns/21.123ns/20.551ns  area = 461luts + 0ff
  *
  * 55nm@bf16: delay[i/o/max] = 6.4107ns/6.4107ns/6.4107ns  area = 1971.48um²
  *
  * @param aFmt   The format of the first operand
  * @param bFmt   The format of the second operand
  * @param outFmt The format of the result
  * @param policy The numeric policy
  */
class FpMacroMul(aFmt: FpFormat, bFmt: FpFormat, outFmt: FpFormat, policy: FpPolicy = FpPolicy())
    extends FpMulBase(aFmt, bFmt, outFmt, policy, w => Module(new UIntMacroMul(w)))

object FpMacroMulFp64 extends App {
  ExportForAnalysis(
    new FpMacroMul(FpFormat.Fp64, FpFormat.Fp64, FpFormat.Fp64),
    args,
    FpExport.opts,
  )
}

object FpMacroMulFp32 extends App {
  ExportForAnalysis(
    new FpMacroMul(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32),
    args,
    FpExport.opts,
  )
}

object FpMacroMulFp16 extends App {
  ExportForAnalysis(
    new FpMacroMul(FpFormat.Fp16, FpFormat.Fp16, FpFormat.Fp16),
    args,
    FpExport.opts,
  )
}

object FpMacroMulBf16 extends App {
  ExportForAnalysis(
    new FpMacroMul(FpFormat.Bf16, FpFormat.Bf16, FpFormat.Bf16),
    args,
    FpExport.opts,
  )
}
