package harith.fp

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis
import harith.uint._

/**
  * A floating-point fused multiply-adder with a parallel prefix alignment adder.
  *
  * fpga@fp64: delay[i/o/max] = 65.041ns/66.021ns/65.041ns  area = 12901luts + 0ff
  *
  * 55nm@fp64: delay[i/o/max] = 85.5805ns/85.5805ns/85.5805ns  area = 76243.72um²
  *
  * fpga@fp32: delay[i/o/max] = 51.516ns/52.095ns/51.516ns  area = 6090luts + 0ff
  *
  * 55nm@fp32: delay[i/o/max] = 35.9554ns/35.9554ns/35.9554ns  area = 24540.60um²
  *
  * fpga@fp16: delay[i/o/max] = 45.782ns/46.762ns/45.782ns  area = 2939luts + 0ff
  *
  * 55nm@fp16: delay[i/o/max] = 25.5184ns/25.5184ns/25.5184ns  area = 11367.44um²
  *
  * fpga@bf16: delay[i/o/max] = 42.660ns/43.632ns/42.660ns  area = 2673luts + 0ff
  *
  * 55nm@bf16: delay[i/o/max] = 13.7281ns/13.7281ns/13.7281ns  area = 6730.08um²
  *
  * @param aFmt   The format of the multiplicand
  * @param bFmt   The format of the multiplier
  * @param cFmt   The format of the addend
  * @param outFmt The format of the result
  * @param policy The numeric policy
  */
class FpPrefixFma(
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
      w => Module(new UIntPrefixAdd(w, PrefixStyle.KoggeStone)),
    )

object FpPrefixFmaFp64 extends App {
  ExportForAnalysis(
    new FpPrefixFma(FpFormat.Fp64, FpFormat.Fp64, FpFormat.Fp64, FpFormat.Fp64),
    args,
    FpExport.opts,
  )
}

object FpPrefixFmaFp32 extends App {
  ExportForAnalysis(
    new FpPrefixFma(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32),
    args,
    FpExport.opts,
  )
}

object FpPrefixFmaFp16 extends App {
  ExportForAnalysis(
    new FpPrefixFma(FpFormat.Fp16, FpFormat.Fp16, FpFormat.Fp16, FpFormat.Fp16),
    args,
    FpExport.opts,
  )
}

object FpPrefixFmaBf16 extends App {
  ExportForAnalysis(
    new FpPrefixFma(FpFormat.Bf16, FpFormat.Bf16, FpFormat.Bf16, FpFormat.Bf16),
    args,
    FpExport.opts,
  )
}
