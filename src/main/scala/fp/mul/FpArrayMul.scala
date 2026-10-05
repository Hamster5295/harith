package harith.fp

import chisel3._
import chisel3.util._
import hammer.Export
import harith.uint._

/**
  * A floating-point multiplier with a carry save array significand multiplier, the cheapest option.
  *
  * fpga@fp32: delay = 36.003ns  area = 2319luts + 0ff
  *
  * 55nm@fp32: delay = 13.2657ns  area = 11438.56um²
  *
  * @param aFmt   The format of the first operand
  * @param bFmt   The format of the second operand
  * @param outFmt The format of the result
  * @param policy The numeric policy
  */
class FpArrayMul(aFmt: FpFormat, bFmt: FpFormat, outFmt: FpFormat, policy: FpPolicy = FpPolicy())
    extends FpMulBase(aFmt, bFmt, outFmt, policy, w => Module(new UIntArrayMul(w)))

object FpArrayMul extends App {
  Export(new FpArrayMul(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32), args, FpExport.opts)
}
