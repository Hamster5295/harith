package harith.fp

import chisel3._
import chisel3.util._
import hammer.Export
import harith.uint._

/**
  * A floating-point multiplier with a modified Booth radix-4 tree significand multiplier.
  *
  * fpga@fp32: delay = 30.310ns  area = 2378luts + 0ff
  *
  * 55nm@fp32: delay = 12.1188ns  area = 12647.88um²
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
  Export(new FpBoothMul(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32), args, FpExport.opts)
}
