package harith.fp

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis
import harith.uint._

/**
  * A floating-point adder with a block carry select alignment adder.
  *
  * fpga@fp64: delay[i/o/max] = 57.265ns/57.845ns/57.265ns  area = 8943luts + 0ff
  *
  * 55nm@fp64: delay[i/o/max] = 23.2826ns/23.2826ns/23.2826ns  area = 12961.20um²
  *
  * fpga@fp32: delay[i/o/max] = 45.991ns/46.571ns/45.991ns  area = 4226luts + 0ff
  *
  * 55nm@fp32: delay[i/o/max] = 14.6117ns/14.6117ns/14.6117ns  area = 5542.88um²
  *
  * fpga@fp16: delay[i/o/max] = 40.538ns/41.232ns/40.538ns  area = 2160luts + 0ff
  *
  * 55nm@fp16: delay[i/o/max] = 9.1653ns/9.1653ns/9.1653ns  area = 3114.44um²
  *
  * fpga@bf16: delay[i/o/max] = 39.206ns/40.065ns/39.206ns  area = 1983luts + 0ff
  *
  * 55nm@bf16: delay[i/o/max] = 8.6955ns/8.6955ns/8.6955ns  area = 2964.64um²
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

object FpCarrySelectAddFp64 extends App {
  ExportForAnalysis(
    new FpCarrySelectAdd(FpFormat.Fp64, FpFormat.Fp64, FpFormat.Fp64),
    args,
    FpExport.opts,
  )
}

object FpCarrySelectAddFp32 extends App {
  ExportForAnalysis(
    new FpCarrySelectAdd(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32),
    args,
    FpExport.opts,
  )
}

object FpCarrySelectAddFp16 extends App {
  ExportForAnalysis(
    new FpCarrySelectAdd(FpFormat.Fp16, FpFormat.Fp16, FpFormat.Fp16),
    args,
    FpExport.opts,
  )
}

object FpCarrySelectAddBf16 extends App {
  ExportForAnalysis(
    new FpCarrySelectAdd(FpFormat.Bf16, FpFormat.Bf16, FpFormat.Bf16),
    args,
    FpExport.opts,
  )
}
