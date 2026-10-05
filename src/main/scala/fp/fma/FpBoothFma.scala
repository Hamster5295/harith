package harith.fp

import chisel3._
import chisel3.util._
import hammer.Export
import harith.uint._

/**
  * A floating-point fused multiply-adder with a modified Booth radix-4 tree significand multiplier.
  *
  * fpga@fp32: delay = 54.714ns  area = 6460luts + 0ff
  *
  * 55nm@fp32: delay = 35.4359ns  area = 30400.16um²
  *
  * @param aFmt   The format of the multiplicand
  * @param bFmt   The format of the multiplier
  * @param cFmt   The format of the addend
  * @param outFmt The format of the result
  * @param policy The numeric policy
  */
class FpBoothFma(
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
      w =>
        Module(
          new UIntBoothMul(
            w,
            ReductionStyle.Dadda,
            new UIntPrefixAdd(2 * w, PrefixStyle.KoggeStone),
          ),
        ),
      w => Module(new UIntMacroAdd(w)),
    )

object FpBoothFma extends App {
  Export(
    new FpBoothFma(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32),
    args,
    FpExport.opts,
  )
}
