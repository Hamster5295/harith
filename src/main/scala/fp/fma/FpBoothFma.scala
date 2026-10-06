package harith.fp

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis
import harith.uint._

/**
  * A floating-point fused multiply-adder with a modified Booth radix-4 tree significand multiplier.
  *
  * fpga@fp64: delay[i/o/max] = 65.193ns/65.765ns/65.193ns  area = 16250luts + 0ff
  *
  * 55nm@fp64: delay[i/o/max] = 52.9643ns/52.9643ns/52.9643ns  area = 75525.24um²
  *
  * fpga@fp32: delay[i/o/max] = 52.331ns/52.911ns/52.331ns  area = 6531luts + 0ff
  *
  * 55nm@fp32: delay[i/o/max] = 37.0841ns/37.0841ns/37.0841ns  area = 30194.92um²
  *
  * fpga@fp16: delay[i/o/max] = 45.982ns/46.971ns/45.982ns  area = 2768luts + 0ff
  *
  * 55nm@fp16: delay[i/o/max] = 24.4914ns/24.4914ns/24.4914ns  area = 11494.00um²
  *
  * fpga@bf16: delay[i/o/max] = 43.557ns/44.431ns/43.557ns  area = 2364luts + 0ff
  *
  * 55nm@bf16: delay[i/o/max] = 25.7625ns/25.7625ns/25.7625ns  area = 9553.32um²
  *
  * @param aFmt   The format of the multiplicand
  * @param bFmt   The format of the multiplier
  * @param cFmt   The format of the addend
  * @param outFmt The format of the result
  * @param policy The numeric policy
  */
class FpBoothFma(
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
      w =>
        Module(
          new UIntBoothMul(
            w,
            ReductionStyle.Dadda,
            new UIntPrefixAdd(2 * w, PrefixStyle.KoggeStone),
          ),
        ),
      w => Module(new UIntMacroAdd(w)),
    )

object FpBoothFmaFp64 extends App {
  ExportForAnalysis(
    new FpBoothFma(FpFormat.Fp64, FpFormat.Fp64, FpFormat.Fp64, FpFormat.Fp64),
    args,
    FpExport.opts,
  )
}

object FpBoothFmaFp32 extends App {
  ExportForAnalysis(
    new FpBoothFma(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32),
    args,
    FpExport.opts,
  )
}

object FpBoothFmaFp16 extends App {
  ExportForAnalysis(
    new FpBoothFma(FpFormat.Fp16, FpFormat.Fp16, FpFormat.Fp16, FpFormat.Fp16),
    args,
    FpExport.opts,
  )
}

object FpBoothFmaBf16 extends App {
  ExportForAnalysis(
    new FpBoothFma(FpFormat.Bf16, FpFormat.Bf16, FpFormat.Bf16, FpFormat.Bf16),
    args,
    FpExport.opts,
  )
}
