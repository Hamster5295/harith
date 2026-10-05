package harith.fp

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis
import harith.uint._

/**
  * A floating-point multiplier with a carry save array significand multiplier, the cheapest option.
  *
  * fpga@fp32: delay[i/o/max] = 33.146ns/33.737ns/33.146ns  area = 2341luts + 0ff
  *
  * 55nm@fp32: delay[i/o/max] = 12.6329ns/12.6329ns/12.6329ns  area = 11450.88um²
  *
  * @param aFmt   The format of the first operand
  * @param bFmt   The format of the second operand
  * @param outFmt The format of the result
  * @param policy The numeric policy
  */
class FpArrayMul(aFmt: FpFormat, bFmt: FpFormat, outFmt: FpFormat, policy: FpPolicy = FpPolicy())
    extends FpMulBase(aFmt, bFmt, outFmt, policy, w => Module(new UIntArrayMul(w)))

object FpArrayMul extends App {
  ExportForAnalysis(
    new FpArrayMul(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32),
    args,
    FpExport.opts,
  )
}
