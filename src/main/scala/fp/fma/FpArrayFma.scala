package harith.fp

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis
import harith.uint._

/**
  * A floating-point fused multiply-adder with an array significand multiplier.
  *
  * fpga@fp32: delay[i/o/max] = 57.147ns/57.727ns/57.147ns  area = 6559luts + 0ff
  *
  * 55nm@fp32: delay[i/o/max] = 34.1085ns/34.1085ns/34.1085ns  area = 25570.16um²
  *
  * @param aFmt   The format of the multiplicand
  * @param bFmt   The format of the multiplier
  * @param cFmt   The format of the addend
  * @param outFmt The format of the result
  * @param policy The numeric policy
  */
class FpArrayFma(
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
      w => Module(new UIntArrayMul(w)),
      w => Module(new UIntMacroAdd(w)),
    )

object FpArrayFma extends App {
  ExportForAnalysis(
    new FpArrayFma(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32),
    args,
    FpExport.opts,
  )
}
