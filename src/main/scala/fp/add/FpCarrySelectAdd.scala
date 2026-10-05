package harith.fp

import chisel3._
import chisel3.util._
import hammer.Export
import harith.uint._

/**
  * A floating-point adder with a block carry select alignment adder.
  *
  * delay = 48.919, area = 4194 @32bit@fpga
  * delay = 21.5805, area = 7995.12 @32bit@55nm
  *
  * @param aFmt   The format of the first operand
  * @param bFmt   The format of the second operand
  * @param outFmt The format of the result
  * @param policy The numeric policy
  */
class FpCarrySelectAdd(
    aFmt:   FpFormat,
    bFmt:   FpFormat,
    outFmt: FpFormat,
    policy: FpPolicy = FpPolicy(),
) extends FpAddBase(aFmt, bFmt, outFmt, policy, w => Module(new UIntCarrySelectAdd(w, 4)))

object FpCarrySelectAdd extends App {
  Export(new FpCarrySelectAdd(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32), args, FpExport.opts)
}
