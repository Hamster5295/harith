package harith.fp

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis
import harith.uint._

/**
  * A floating-point adder with a hierarchical carry lookahead alignment adder.
  *
  * fpga@fp64: delay[i/o/max] = 50.264ns/50.836ns/50.264ns  area = 8835luts + 0ff
  *
  * 55nm@fp64: delay[i/o/max] = 26.5524ns/26.5524ns/26.5524ns  area = 14996.80um²
  *
  * fpga@fp32: delay[i/o/max] = 42.789ns/43.369ns/42.789ns  area = 4312luts + 0ff
  *
  * 55nm@fp32: delay[i/o/max] = 13.6956ns/13.6956ns/13.6956ns  area = 6375.88um²
  *
  * fpga@fp16: delay[i/o/max] = 39.533ns/40.227ns/39.533ns  area = 2195luts + 0ff
  *
  * 55nm@fp16: delay[i/o/max] = 9.6391ns/9.6391ns/9.6391ns  area = 3378.48um²
  *
  * fpga@bf16: delay[i/o/max] = 38.701ns/39.560ns/38.701ns  area = 2064luts + 0ff
  *
  * 55nm@bf16: delay[i/o/max] = 9.9088ns/9.9088ns/9.9088ns  area = 3227.56um²
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

object FpCarryLookaheadAddFp64 extends App {
  ExportForAnalysis(
    new FpCarryLookaheadAdd(FpFormat.Fp64, FpFormat.Fp64, FpFormat.Fp64),
    args,
    FpExport.opts,
  )
}

object FpCarryLookaheadAddFp32 extends App {
  ExportForAnalysis(
    new FpCarryLookaheadAdd(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32),
    args,
    FpExport.opts,
  )
}

object FpCarryLookaheadAddFp16 extends App {
  ExportForAnalysis(
    new FpCarryLookaheadAdd(FpFormat.Fp16, FpFormat.Fp16, FpFormat.Fp16),
    args,
    FpExport.opts,
  )
}

object FpCarryLookaheadAddBf16 extends App {
  ExportForAnalysis(
    new FpCarryLookaheadAdd(FpFormat.Bf16, FpFormat.Bf16, FpFormat.Bf16),
    args,
    FpExport.opts,
  )
}
