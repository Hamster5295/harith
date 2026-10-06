package harith.fp

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis
import harith.uint._

/**
  * A floating-point fused multiply-adder with an array significand multiplier.
  *
  * fpga@fp64: delay[i/o/max] = 85.418ns/85.990ns/85.418ns  area = 17572luts + 0ff
  *
  * 55nm@fp64: delay[i/o/max] = 61.8791ns/61.8791ns/61.8791ns  area = 89052.88um²
  *
  * fpga@fp32: delay[i/o/max] = 57.147ns/57.727ns/57.147ns  area = 6559luts + 0ff
  *
  * 55nm@fp32: delay[i/o/max] = 34.1085ns/34.1085ns/34.1085ns  area = 25570.16um²
  *
  * fpga@fp16: delay[i/o/max] = 46.289ns/47.278ns/46.289ns  area = 2668luts + 0ff
  *
  * 55nm@fp16: delay[i/o/max] = 15.8832ns/15.8832ns/15.8832ns  area = 6938.68um²
  *
  * fpga@bf16: delay[i/o/max] = 43.564ns/44.528ns/43.564ns  area = 2285luts + 0ff
  *
  * 55nm@bf16: delay[i/o/max] = 14.5860ns/14.5860ns/14.5860ns  area = 6154.96um²
  *
  * @param aFmt   The format of the multiplicand
  * @param bFmt   The format of the multiplier
  * @param cFmt   The format of the addend
  * @param outFmt The format of the result
  * @param policy The numeric policy
  */
class FpArrayFma(
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
      w => Module(new UIntArrayMul(w)),
      w => Module(new UIntMacroAdd(w)),
    )

object FpArrayFmaFp64 extends App {
  ExportForAnalysis(
    new FpArrayFma(FpFormat.Fp64, FpFormat.Fp64, FpFormat.Fp64, FpFormat.Fp64),
    args,
    FpExport.opts,
  )
}

object FpArrayFmaFp32 extends App {
  ExportForAnalysis(
    new FpArrayFma(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32),
    args,
    FpExport.opts,
  )
}

object FpArrayFmaFp16 extends App {
  ExportForAnalysis(
    new FpArrayFma(FpFormat.Fp16, FpFormat.Fp16, FpFormat.Fp16, FpFormat.Fp16),
    args,
    FpExport.opts,
  )
}

object FpArrayFmaBf16 extends App {
  ExportForAnalysis(
    new FpArrayFma(FpFormat.Bf16, FpFormat.Bf16, FpFormat.Bf16, FpFormat.Bf16),
    args,
    FpExport.opts,
  )
}
