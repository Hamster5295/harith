package harith.fp

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis
import harith.uint._

/**
  * A floating-point fused multiply-adder with an AND partial product tree significand multiplier.
  *
  * fpga@fp64: delay[i/o/max] = 68.247ns/68.819ns/68.247ns  area = 14942luts + 0ff
  *
  * 55nm@fp64: delay[i/o/max] = 74.7898ns/74.7898ns/74.7898ns  area = 72748.76um²
  *
  * fpga@fp32: delay[i/o/max] = 51.426ns/52.006ns/51.426ns  area = 6007luts + 0ff
  *
  * 55nm@fp32: delay[i/o/max] = 41.0896ns/41.0896ns/41.0896ns  area = 26301.24um²
  *
  * fpga@fp16: delay[i/o/max] = 45.734ns/46.723ns/45.734ns  area = 2622luts + 0ff
  *
  * 55nm@fp16: delay[i/o/max] = 20.8538ns/20.8538ns/20.8538ns  area = 9047.36um²
  *
  * fpga@bf16: delay[i/o/max] = 43.161ns/44.035ns/43.161ns  area = 2332luts + 0ff
  *
  * 55nm@bf16: delay[i/o/max] = 14.0390ns/14.0390ns/14.0390ns  area = 6468.28um²
  *
  * @param aFmt   The format of the multiplicand
  * @param bFmt   The format of the multiplier
  * @param cFmt   The format of the addend
  * @param outFmt The format of the result
  * @param policy The numeric policy
  */
class FpTreeFma(
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
          new UIntTreeMul(w, ReductionStyle.Dadda, new UIntPrefixAdd(2 * w, PrefixStyle.KoggeStone)),
        ),
      w => Module(new UIntMacroAdd(w)),
    )

object FpTreeFmaFp64 extends App {
  ExportForAnalysis(
    new FpTreeFma(FpFormat.Fp64, FpFormat.Fp64, FpFormat.Fp64, FpFormat.Fp64),
    args,
    FpExport.opts,
  )
}

object FpTreeFmaFp32 extends App {
  ExportForAnalysis(
    new FpTreeFma(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32),
    args,
    FpExport.opts,
  )
}

object FpTreeFmaFp16 extends App {
  ExportForAnalysis(
    new FpTreeFma(FpFormat.Fp16, FpFormat.Fp16, FpFormat.Fp16, FpFormat.Fp16),
    args,
    FpExport.opts,
  )
}

object FpTreeFmaBf16 extends App {
  ExportForAnalysis(
    new FpTreeFma(FpFormat.Bf16, FpFormat.Bf16, FpFormat.Bf16, FpFormat.Bf16),
    args,
    FpExport.opts,
  )
}
