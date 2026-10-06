package harith.fp

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis
import harith.uint._

/**
  * A floating-point multiplier with an AND partial product carry save tree significand multiplier.
  *
  * fpga@fp64: delay[i/o/max] = 39.064ns/39.462ns/39.064ns  area = 6939luts + 0ff
  *
  * 55nm@fp64: delay[i/o/max] = 19.0265ns/19.0265ns/19.0265ns  area = 36041.60um²
  *
  * fpga@fp32: delay[i/o/max] = 26.628ns/27.219ns/26.628ns  area = 1864luts + 0ff
  *
  * 55nm@fp32: delay[i/o/max] = 11.0121ns/11.0121ns/11.0121ns  area = 8316.00um²
  *
  * fpga@fp16: delay[i/o/max] = 23.476ns/24.072ns/23.476ns  area = 715luts + 0ff
  *
  * 55nm@fp16: delay[i/o/max] = 7.0437ns/7.0437ns/7.0437ns  area = 2772.56um²
  *
  * fpga@bf16: delay[i/o/max] = 22.655ns/23.227ns/22.655ns  area = 523luts + 0ff
  *
  * 55nm@bf16: delay[i/o/max] = 6.1560ns/6.1560ns/6.1560ns  area = 2072.84um²
  *
  * @param aFmt   The format of the first operand
  * @param bFmt   The format of the second operand
  * @param outFmt The format of the result
  * @param policy The numeric policy
  */
class FpTreeMul(aFmt: FpFormat, bFmt: FpFormat, outFmt: FpFormat, policy: FpPolicy = FpPolicy())
    extends FpMulBase(
      aFmt,
      bFmt,
      outFmt,
      policy,
      w =>
        Module(
          new UIntTreeMul(w, ReductionStyle.Dadda, new UIntPrefixAdd(2 * w, PrefixStyle.KoggeStone)),
        ),
    )

object FpTreeMulFp64 extends App {
  ExportForAnalysis(new FpTreeMul(FpFormat.Fp64, FpFormat.Fp64, FpFormat.Fp64), args, FpExport.opts)
}

object FpTreeMulFp32 extends App {
  ExportForAnalysis(new FpTreeMul(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32), args, FpExport.opts)
}

object FpTreeMulFp16 extends App {
  ExportForAnalysis(new FpTreeMul(FpFormat.Fp16, FpFormat.Fp16, FpFormat.Fp16), args, FpExport.opts)
}

object FpTreeMulBf16 extends App {
  ExportForAnalysis(new FpTreeMul(FpFormat.Bf16, FpFormat.Bf16, FpFormat.Bf16), args, FpExport.opts)
}
