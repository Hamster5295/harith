package harith.fp

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis
import harith.uint._

/**
  * A floating-point multiplier with a modified Booth radix-4 tree significand multiplier.
  *
  * fpga@fp32: delay[i/o/max] = 27.629ns/28.220ns/27.629ns  area = 2370luts + 0ff
  *
  * 55nm@fp32: delay[i/o/max] = 11.8575ns/11.8575ns/11.8575ns  area = 12906.60um²
  *
  * @param aFmt   The format of the first operand
  * @param bFmt   The format of the second operand
  * @param outFmt The format of the result
  * @param policy The numeric policy
  */
class FpBoothMul(aFmt: FpFormat, bFmt: FpFormat, outFmt: FpFormat, policy: FpPolicy = FpPolicy())
    extends FpMulBase(
      aFmt,
      bFmt,
      outFmt,
      policy,
      w =>
        Module(
          new UIntBoothMul(
            w,
            ReductionStyle.Dadda,
            new UIntPrefixAdd(2 * w, PrefixStyle.KoggeStone),
          ),
        ),
    )

object FpBoothMul extends App {
  ExportForAnalysis(
    new FpBoothMul(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32),
    args,
    FpExport.opts,
  )
}
