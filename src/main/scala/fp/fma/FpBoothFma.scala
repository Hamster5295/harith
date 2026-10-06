package harith.fp

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis
import harith.uint._

/**
  * A floating-point fused multiply-adder with a modified Booth radix-4 tree significand multiplier.
  *
  * fpga@fp32: delay[i/o/max] = 52.331ns/52.911ns/52.331ns  area = 6531luts + 0ff
  *
  * 55nm@fp32: delay[i/o/max] = 34.6367ns/34.6367ns/33.8087ns  area = 29716.96um²
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
  ExportForAnalysis(
    new FpBoothFma(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32),
    args,
    FpExport.opts,
  )
}
