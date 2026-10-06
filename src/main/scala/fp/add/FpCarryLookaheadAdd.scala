package harith.fp

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis
import harith.uint._

/**
  * A floating-point adder with a hierarchical carry lookahead alignment adder.
  *
  * fpga@fp32: delay[i/o/max] = 42.789ns/43.369ns/42.789ns  area = 4312luts + 0ff
  *
  * 55nm@fp32: delay[i/o/max] = 14.0093ns/14.0093ns/12.9996ns  area = 6517.28um²
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
  ExportForAnalysis(
    new FpCarryLookaheadAdd(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32),
    args,
    FpExport.opts,
  )
}
