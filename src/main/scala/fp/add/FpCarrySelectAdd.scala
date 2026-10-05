package harith.fp

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis
import harith.uint._

/**
  * A floating-point adder with a block carry select alignment adder.
  *
  * fpga@fp32: delay[i/o/max] = 45.991ns/46.571ns/45.991ns  area = 4226luts + 0ff
  *
  * 55nm@fp32: delay[i/o/max] = 14.6117ns/14.6117ns/14.6117ns  area = 5542.88um²
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
  ExportForAnalysis(
    new FpCarrySelectAdd(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32),
    args,
    FpExport.opts,
  )
}
