package harith.fp

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis
import harith.uint._

/**
  * A floating-point adder with a parallel prefix alignment adder, the fast option.
  *
  * fpga@fp32: delay[i/o/max] = 42.413ns/42.993ns/42.413ns  area = 4837luts + 0ff
  *
  * 55nm@fp32: delay[i/o/max] = 13.1978ns/13.1978ns/13.1978ns  area = 6136.48um²
  *
  * @param aFmt   The format of the first operand
  * @param bFmt   The format of the second operand
  * @param outFmt The format of the result
  * @param policy The numeric policy
  */
class FpPrefixAdd(aFmt: FpFormat, bFmt: FpFormat, outFmt: FpFormat, policy: FpPolicy = FpPolicy())
    extends FpAddBase(
      aFmt,
      bFmt,
      outFmt,
      policy,
      w => Module(new UIntPrefixAdd(w, PrefixStyle.KoggeStone)),
    )

object FpPrefixAdd extends App {
  ExportForAnalysis(
    new FpPrefixAdd(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32),
    args,
    FpExport.opts,
  )
}
