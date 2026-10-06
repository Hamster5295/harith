package harith.fp

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis
import harith.uint._

/**
  * A floating-point multiplier with a modified Booth radix-4 tree significand multiplier.
  *
  * fpga@fp64: delay[i/o/max] = 36.213ns/36.611ns/36.213ns  area = 8701luts + 0ff
  *
  * 55nm@fp64: delay[i/o/max] = 17.5670ns/17.5670ns/17.5670ns  area = 44994.32um²
  *
  * fpga@fp32: delay[i/o/max] = 27.629ns/28.220ns/27.629ns  area = 2370luts + 0ff
  *
  * 55nm@fp32: delay[i/o/max] = 11.8575ns/11.8575ns/11.8575ns  area = 12906.60um²
  *
  * fpga@fp16: delay[i/o/max] = 26.835ns/27.233ns/26.835ns  area = 858luts + 0ff
  *
  * 55nm@fp16: delay[i/o/max] = 7.0969ns/7.0969ns/7.0969ns  area = 3496.36um²
  *
  * fpga@bf16: delay[i/o/max] = 22.525ns/23.097ns/22.525ns  area = 549luts + 0ff
  *
  * 55nm@bf16: delay[i/o/max] = 6.2379ns/6.2379ns/6.2379ns  area = 2506.56um²
  *
  * @param aFmt   The format of the first operand
  * @param bFmt   The format of the second operand
  * @param outFmt The format of the result
  * @param policy The numeric policy
  */
class FpBoothMul(aFmt: FpFormat, bFmt: FpFormat, outFmt: FpFormat, policy: FpPolicy = FpPolicy())
    extends FpMulBase(
      aFmt,
      bFmt,
      outFmt,
      policy,
      w =>
        Module(
          new UIntBoothMul(
            w,
            ReductionStyle.Dadda,
            new UIntPrefixAdd(2 * w, PrefixStyle.KoggeStone),
          ),
        ),
    )

object FpBoothMulFp64 extends App {
  ExportForAnalysis(
    new FpBoothMul(FpFormat.Fp64, FpFormat.Fp64, FpFormat.Fp64),
    args,
    FpExport.opts,
  )
}

object FpBoothMulFp32 extends App {
  ExportForAnalysis(
    new FpBoothMul(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32),
    args,
    FpExport.opts,
  )
}

object FpBoothMulFp16 extends App {
  ExportForAnalysis(
    new FpBoothMul(FpFormat.Fp16, FpFormat.Fp16, FpFormat.Fp16),
    args,
    FpExport.opts,
  )
}

object FpBoothMulBf16 extends App {
  ExportForAnalysis(
    new FpBoothMul(FpFormat.Bf16, FpFormat.Bf16, FpFormat.Bf16),
    args,
    FpExport.opts,
  )
}
