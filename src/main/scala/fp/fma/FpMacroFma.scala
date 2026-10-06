package harith.fp

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis
import harith.uint._

/**
  * A floating-point fused multiply-adder with inferred significand and alignment datapaths.
  *
  * fpga@fp64: delay[i/o/max] = 64.329ns/65.309ns/64.329ns  area = 11083luts + 0ff
  *
  * 55nm@fp64: delay[i/o/max] = 76.5721ns/76.5721ns/76.4466ns  area = 72919.28um²
  *
  * fpga@fp32: delay[i/o/max] = 49.853ns/50.416ns/49.853ns  area = 5107luts + 0ff
  *
  * 55nm@fp32: delay[i/o/max] = 36.9288ns/36.9288ns/36.9288ns  area = 28061.32um²
  *
  * fpga@fp16: delay[i/o/max] = 43.305ns/43.884ns/43.305ns  area = 2408luts + 0ff
  *
  * 55nm@fp16: delay[i/o/max] = 15.1467ns/15.1467ns/15.1467ns  area = 7418.32um²
  *
  * fpga@bf16: delay[i/o/max] = 40.720ns/41.292ns/40.720ns  area = 2213luts + 0ff
  *
  * 55nm@bf16: delay[i/o/max] = 15.6026ns/15.6026ns/15.6026ns  area = 6058.92um²
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

object FpMacroFmaFp64 extends App {
  ExportForAnalysis(
    new FpMacroFma(FpFormat.Fp64, FpFormat.Fp64, FpFormat.Fp64, FpFormat.Fp64),
    args,
    FpExport.opts,
  )
}

object FpMacroFmaFp32 extends App {
  ExportForAnalysis(
    new FpMacroFma(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32),
    args,
    FpExport.opts,
  )
}

object FpMacroFmaFp16 extends App {
  ExportForAnalysis(
    new FpMacroFma(FpFormat.Fp16, FpFormat.Fp16, FpFormat.Fp16, FpFormat.Fp16),
    args,
    FpExport.opts,
  )
}

object FpMacroFmaBf16 extends App {
  ExportForAnalysis(
    new FpMacroFma(FpFormat.Bf16, FpFormat.Bf16, FpFormat.Bf16, FpFormat.Bf16),
    args,
    FpExport.opts,
  )
}
