package harith.fp

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis
import harith.uint._

/**
  * A floating-point multiplier with a carry save array significand multiplier, the cheapest option.
  *
  * fpga@fp64: delay[i/o/max] = 57.435ns/57.833ns/57.435ns  area = 10088luts + 0ff
  *
  * 55nm@fp64: delay[i/o/max] = 25.1068ns/25.1068ns/25.1068ns  area = 54372.08um²
  *
  * fpga@fp32: delay[i/o/max] = 33.146ns/33.737ns/33.146ns  area = 2341luts + 0ff
  *
  * 55nm@fp32: delay[i/o/max] = 12.6329ns/12.6329ns/12.6329ns  area = 11450.88um²
  *
  * fpga@fp16: delay[i/o/max] = 26.467ns/27.055ns/26.467ns  area = 736luts + 0ff
  *
  * 55nm@fp16: delay[i/o/max] = 6.8266ns/6.8266ns/6.8266ns  area = 2887.08um²
  *
  * fpga@bf16: delay[i/o/max] = 22.485ns/23.057ns/22.485ns  area = 465luts + 0ff
  *
  * 55nm@bf16: delay[i/o/max] = 6.5880ns/6.5880ns/6.5880ns  area = 1956.64um²
  *
  * @param aFmt   The format of the first operand
  * @param bFmt   The format of the second operand
  * @param outFmt The format of the result
  * @param policy The numeric policy
  */
class FpArrayMul(aFmt: FpFormat, bFmt: FpFormat, outFmt: FpFormat, policy: FpPolicy = FpPolicy())
    extends FpMulBase(aFmt, bFmt, outFmt, policy, w => Module(new UIntArrayMul(w)))

object FpArrayMulFp64 extends App {
  ExportForAnalysis(
    new FpArrayMul(FpFormat.Fp64, FpFormat.Fp64, FpFormat.Fp64),
    args,
    FpExport.opts,
  )
}

object FpArrayMulFp32 extends App {
  ExportForAnalysis(
    new FpArrayMul(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32),
    args,
    FpExport.opts,
  )
}

object FpArrayMulFp16 extends App {
  ExportForAnalysis(
    new FpArrayMul(FpFormat.Fp16, FpFormat.Fp16, FpFormat.Fp16),
    args,
    FpExport.opts,
  )
}

object FpArrayMulBf16 extends App {
  ExportForAnalysis(
    new FpArrayMul(FpFormat.Bf16, FpFormat.Bf16, FpFormat.Bf16),
    args,
    FpExport.opts,
  )
}
