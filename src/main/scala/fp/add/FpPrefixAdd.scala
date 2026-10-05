package harith.fp

import chisel3._
import chisel3.util._
import hammer.Export
import harith.uint._

/**
  * A floating-point adder with a parallel prefix alignment adder, the fast option.
  *
  * delay = 45.192, area = 4818 @32bit@fpga
  * delay = 15.6692, area = 6928.88 @32bit@55nm
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
  Export(new FpPrefixAdd(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32), args, FpExport.opts)
}
