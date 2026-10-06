package harith.fp

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis
import harith.uint._

/**
  * A floating-point fused multiply-adder with a ripple carry alignment adder.
  *
  * fpga@fp32: delay[i/o/max] = 63.167ns/63.738ns/63.167ns  area = 5256luts + 0ff
  *
  * 55nm@fp32: delay[i/o/max] = 41.1512ns/41.1512ns/41.1512ns  area = 28605.08um²
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

object FpRippleFma extends App {
  ExportForAnalysis(
    new FpRippleFma(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32),
    args,
    FpExport.opts,
  )
}
