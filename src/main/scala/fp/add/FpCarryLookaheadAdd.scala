package harith.fp

import chisel3._
import chisel3.util._
import hammer.Export
import harith.uint._

/**
  * A floating-point adder with a hierarchical carry lookahead alignment adder.
  *
  * delay = 45.835, area = 4250 @32bit@fpga
  * delay = 14.0796, area = 7059.92 @32bit@55nm
  *
  * @param aFmt   The format of the first operand
  * @param bFmt   The format of the second operand
  * @param outFmt The format of the result
  * @param policy The numeric policy
  */
class FpCarryLookaheadAdd(
    aFmt:   FpFormat,
    bFmt:   FpFormat,
    outFmt: FpFormat,
    policy: FpPolicy = FpPolicy(),
) extends FpAddBase(aFmt, bFmt, outFmt, policy, w => Module(new UIntCarryLookaheadAdd(w, 4)))

object FpCarryLookaheadAdd extends App {
  Export(new FpCarryLookaheadAdd(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32), args, FpExport.opts)
}
