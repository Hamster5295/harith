package harith.fp

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis
import harith.uint._

/**
  * A floating-point multiplier with an AND partial product carry save tree significand multiplier.
  *
  * fpga@fp32: delay[i/o/max] = 26.628ns/27.219ns/26.628ns  area = 1864luts + 0ff
  *
  * 55nm@fp32: delay[i/o/max] = 11.0121ns/11.0121ns/11.0121ns  area = 8316.00um²
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
  ExportForAnalysis(new FpTreeMul(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32), args, FpExport.opts)
}
