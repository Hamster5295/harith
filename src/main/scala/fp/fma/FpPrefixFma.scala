package harith.fp

import chisel3._
import chisel3.util._
import hammer.Export
import harith.uint._

/**
  * A floating-point fused multiply-adder with a parallel prefix alignment adder.
  *
  * fpga@fp32: delay = 54.672ns  area = 6155luts + 0ff
  *
  * 55nm@fp32: delay = 45.4307ns  area = 26786.48um²
  *
  * @param aFmt   The format of the multiplicand
  * @param bFmt   The format of the multiplier
  * @param cFmt   The format of the addend
  * @param outFmt The format of the result
  * @param policy The numeric policy
  */
class FpPrefixFma(
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
      w => Module(new UIntMacroMul(w)),
      w => Module(new UIntPrefixAdd(w, PrefixStyle.KoggeStone)),
    )

object FpPrefixFma extends App {
  Export(
    new FpPrefixFma(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32),
    args,
    FpExport.opts,
  )
}
