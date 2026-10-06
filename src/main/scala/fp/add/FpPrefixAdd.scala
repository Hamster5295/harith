package harith.fp

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis
import harith.uint._

/**
  * A floating-point adder with a parallel prefix alignment adder, the fast option.
  *
  * fpga@fp64: delay[i/o/max] = 50.114ns/50.686ns/50.114ns  area = 10087luts + 0ff
  *
  * 55nm@fp64: delay[i/o/max] = 21.8837ns/21.8837ns/21.8837ns  area = 13830.04um²
  *
  * fpga@fp32: delay[i/o/max] = 42.413ns/42.993ns/42.413ns  area = 4837luts + 0ff
  *
  * 55nm@fp32: delay[i/o/max] = 13.1978ns/13.1978ns/13.1978ns  area = 6136.48um²
  *
  * fpga@fp16: delay[i/o/max] = 39.409ns/40.156ns/39.409ns  area = 2378luts + 0ff
  *
  * 55nm@fp16: delay[i/o/max] = 8.9057ns/8.9057ns/8.9057ns  area = 3279.36um²
  *
  * fpga@bf16: delay[i/o/max] = 37.657ns/38.410ns/37.657ns  area = 2138luts + 0ff
  *
  * 55nm@bf16: delay[i/o/max] = 9.4356ns/9.4356ns/9.4356ns  area = 3161.76um²
  *
  * @param aFmt   The format of the first operand
  * @param bFmt   The format of the second operand
  * @param outFmt The format of the result
  * @param policy The numeric policy
  */
class FpPrefixAdd(aFmt: FpFormat, bFmt: FpFormat, outFmt: FpFormat, policy: FpPolicy = FpPolicy())
    extends FpAddBase(
      aFmt,
      bFmt,
      outFmt,
      policy,
      w => Module(new UIntPrefixAdd(w, PrefixStyle.KoggeStone)),
    )

object FpPrefixAddFp64 extends App {
  ExportForAnalysis(
    new FpPrefixAdd(FpFormat.Fp64, FpFormat.Fp64, FpFormat.Fp64),
    args,
    FpExport.opts,
  )
}

object FpPrefixAddFp32 extends App {
  ExportForAnalysis(
    new FpPrefixAdd(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32),
    args,
    FpExport.opts,
  )
}

object FpPrefixAddFp16 extends App {
  ExportForAnalysis(
    new FpPrefixAdd(FpFormat.Fp16, FpFormat.Fp16, FpFormat.Fp16),
    args,
    FpExport.opts,
  )
}

object FpPrefixAddBf16 extends App {
  ExportForAnalysis(
    new FpPrefixAdd(FpFormat.Bf16, FpFormat.Bf16, FpFormat.Bf16),
    args,
    FpExport.opts,
  )
}
