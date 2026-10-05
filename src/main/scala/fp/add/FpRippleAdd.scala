package harith.fp

import chisel3._
import chisel3.util._
import hammer.Export
import harith.uint._

/**
  * A floating-point adder with a ripple carry alignment adder, the cheapest option.
  *
  * delay = 55.508, area = 4066 @32bit@fpga
  * delay = 17.0334, area = 7073.08 @32bit@55nm
  *
  * @param aFmt   The format of the first operand
  * @param bFmt   The format of the second operand
  * @param outFmt The format of the result
  * @param policy The numeric policy
  */
class FpRippleAdd(aFmt: FpFormat, bFmt: FpFormat, outFmt: FpFormat, policy: FpPolicy = FpPolicy())
    extends FpAddBase(aFmt, bFmt, outFmt, policy, w => Module(new UIntRippleAdd(w)))

object FpRippleAdd extends App {
  Export(new FpRippleAdd(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32), args, FpExport.opts)
}
