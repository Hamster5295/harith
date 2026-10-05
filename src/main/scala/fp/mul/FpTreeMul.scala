package harith.fp

import chisel3._
import chisel3.util._
import hammer.Export
import harith.uint._

/**
  * A floating-point multiplier with an AND partial product carry save tree significand multiplier.
  *
  * delay = 29.481, area = 1841 @32bit@fpga
  * delay = 11.0304, area = 8116.64 @32bit@55nm
  *
  * @param aFmt   The format of the first operand
  * @param bFmt   The format of the second operand
  * @param outFmt The format of the result
  * @param policy The numeric policy
  */
class FpTreeMul(aFmt: FpFormat, bFmt: FpFormat, outFmt: FpFormat, policy: FpPolicy = FpPolicy())
    extends FpMulBase(
      aFmt,
      bFmt,
      outFmt,
      policy,
      w =>
        Module(
          new UIntTreeMul(w, ReductionStyle.Dadda, new UIntPrefixAdd(2 * w, PrefixStyle.KoggeStone)),
        ),
    )

object FpTreeMul extends App {
  Export(new FpTreeMul(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32), args, FpExport.opts)
}
