package harith.fp

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis
import harith.uint._

/**
  * A floating-point adder with a ripple carry alignment adder, the cheapest option.
  *
  * fpga@fp32: delay[i/o/max] = 51.462ns/52.042ns/51.462ns  area = 4107luts + 0ff
  *
  * 55nm@fp32: delay[i/o/max] = 14.2377ns/14.2377ns/14.2377ns  area = 5554.36um²
  *
  * @param aFmt   The format of the first operand
  * @param bFmt   The format of the second operand
  * @param outFmt The format of the result
  * @param policy The numeric policy
  */
class FpRippleAdd(aFmt: FpFormat, bFmt: FpFormat, outFmt: FpFormat, policy: FpPolicy = FpPolicy())
    extends FpAddBase(aFmt, bFmt, outFmt, policy, w => Module(new UIntRippleAdd(w)))

object FpRippleAdd extends App {
  ExportForAnalysis(
    new FpRippleAdd(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32),
    args,
    FpExport.opts,
  )
}
