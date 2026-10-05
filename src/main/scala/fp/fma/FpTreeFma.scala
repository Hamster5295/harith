package harith.fp

import chisel3._
import chisel3.util._
import hammer.Export
import harith.uint._

/**
  * A floating-point fused multiply-adder with an AND partial product tree significand multiplier.
  *
  * fpga@fp32: delay = 54.715ns  area = 5948luts + 0ff
  *
  * 55nm@fp32: delay = 45.5888ns  area = 28058.80um²
  *
  * @param aFmt   The format of the multiplicand
  * @param bFmt   The format of the multiplier
  * @param cFmt   The format of the addend
  * @param outFmt The format of the result
  * @param policy The numeric policy
  */
class FpTreeFma(
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
          new UIntTreeMul(w, ReductionStyle.Dadda, new UIntPrefixAdd(2 * w, PrefixStyle.KoggeStone)),
        ),
      w => Module(new UIntMacroAdd(w)),
    )

object FpTreeFma extends App {
  Export(
    new FpTreeFma(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32),
    args,
    FpExport.opts,
  )
}
