package harith.fp

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis
import harith.uint._

/**
  * A floating-point fused multiply-adder with a ripple carry alignment adder.
  *
  * fpga@fp64: delay[i/o/max] = 91.884ns/92.864ns/91.884ns  area = 11240luts + 0ff
  *
  * 55nm@fp64: delay[i/o/max] = 64.2359ns/64.2359ns/64.2329ns  area = 72953.16um²
  *
  * fpga@fp32: delay[i/o/max] = 63.167ns/63.738ns/63.167ns  area = 5256luts + 0ff
  *
  * 55nm@fp32: delay[i/o/max] = 41.1512ns/41.1512ns/41.1512ns  area = 28605.08um²
  *
  * fpga@fp16: delay[i/o/max] = 49.255ns/50.235ns/49.255ns  area = 2535luts + 0ff
  *
  * 55nm@fp16: delay[i/o/max] = 23.2811ns/23.2811ns/23.2811ns  area = 10918.32um²
  *
  * fpga@bf16: delay[i/o/max] = 46.132ns/47.104ns/46.132ns  area = 2345luts + 0ff
  *
  * 55nm@bf16: delay[i/o/max] = 13.2128ns/13.2128ns/13.2128ns  area = 5802.16um²
  *
  * @param aFmt   The format of the multiplicand
  * @param bFmt   The format of the multiplier
  * @param cFmt   The format of the addend
  * @param outFmt The format of the result
  * @param policy The numeric policy
  */
class FpRippleFma(
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
      w => Module(new UIntRippleAdd(w)),
    )

object FpRippleFmaFp64 extends App {
  ExportForAnalysis(
    new FpRippleFma(FpFormat.Fp64, FpFormat.Fp64, FpFormat.Fp64, FpFormat.Fp64),
    args,
    FpExport.opts,
  )
}

object FpRippleFmaFp32 extends App {
  ExportForAnalysis(
    new FpRippleFma(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32),
    args,
    FpExport.opts,
  )
}

object FpRippleFmaFp16 extends App {
  ExportForAnalysis(
    new FpRippleFma(FpFormat.Fp16, FpFormat.Fp16, FpFormat.Fp16, FpFormat.Fp16),
    args,
    FpExport.opts,
  )
}

object FpRippleFmaBf16 extends App {
  ExportForAnalysis(
    new FpRippleFma(FpFormat.Bf16, FpFormat.Bf16, FpFormat.Bf16, FpFormat.Bf16),
    args,
    FpExport.opts,
  )
}
