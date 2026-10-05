package harith.fp

import chisel3._
import chisel3.util._
import hammer.Export
import harith.uint._

/**
  * A floating-point fused multiply-adder with a ripple carry alignment adder.
  *
  * delay = 67.529, area = 5380 @32bit@fpga
  * delay = 40.9942, area = 30536.52 @32bit@55nm
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
  Export(
    new FpRippleFma(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32),
    args,
    FpExport.opts,
  )
}
