package harith.fp

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis
import harith.uint._

/**
  * A floating-point adder with an inferred alignment adder.
  *
  * fpga@fp64: delay[i/o/max] = 48.521ns/49.502ns/48.521ns  area = 8433luts + 0ff
  *
  * 55nm@fp64: delay[i/o/max] = 30.5496ns/30.5496ns/30.5496ns  area = 15172.64um²
  *
  * fpga@fp32: delay[i/o/max] = 40.688ns/41.268ns/40.688ns  area = 3925luts + 0ff
  *
  * 55nm@fp32: delay[i/o/max] = 14.5881ns/14.5881ns/14.5881ns  area = 5899.04um²
  *
  * fpga@fp16: delay[i/o/max] = 38.144ns/38.716ns/38.144ns  area = 1984luts + 0ff
  *
  * 55nm@fp16: delay[i/o/max] = 9.1341ns/9.1341ns/9.1341ns  area = 3145.80um²
  *
  * fpga@bf16: delay[i/o/max] = 37.046ns/37.610ns/37.046ns  area = 1814luts + 0ff
  *
  * 55nm@bf16: delay[i/o/max] = 10.0211ns/10.0211ns/10.0211ns  area = 3217.76um²
  *
  * @param aFmt   The format of the first operand
  * @param bFmt   The format of the second operand
  * @param outFmt The format of the result
  * @param policy The numeric policy
  */
class FpMacroAdd(aFmt: FpFormat, bFmt: FpFormat, outFmt: FpFormat, policy: FpPolicy = FpPolicy())
    extends FpAddBase(aFmt, bFmt, outFmt, policy, w => Module(new UIntMacroAdd(w)))

object FpMacroAddFp64 extends App {
  ExportForAnalysis(
    new FpMacroAdd(FpFormat.Fp64, FpFormat.Fp64, FpFormat.Fp64),
    args,
    FpExport.opts,
  )
}

object FpMacroAddFp32 extends App {
  ExportForAnalysis(
    new FpMacroAdd(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32),
    args,
    FpExport.opts,
  )
}

object FpMacroAddFp16 extends App {
  ExportForAnalysis(
    new FpMacroAdd(FpFormat.Fp16, FpFormat.Fp16, FpFormat.Fp16),
    args,
    FpExport.opts,
  )
}

object FpMacroAddBf16 extends App {
  ExportForAnalysis(
    new FpMacroAdd(FpFormat.Bf16, FpFormat.Bf16, FpFormat.Bf16),
    args,
    FpExport.opts,
  )
}
