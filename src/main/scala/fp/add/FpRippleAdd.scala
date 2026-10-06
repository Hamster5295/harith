package harith.fp

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis
import harith.uint._

/**
  * A floating-point adder with a ripple carry alignment adder, the cheapest option.
  *
  * fpga@fp64: delay[i/o/max] = 72.702ns/73.274ns/72.702ns  area = 8591luts + 0ff
  *
  * 55nm@fp64: delay[i/o/max] = 31.3539ns/31.3539ns/31.3539ns  area = 13508.04um²
  *
  * fpga@fp32: delay[i/o/max] = 51.462ns/52.042ns/51.462ns  area = 4107luts + 0ff
  *
  * 55nm@fp32: delay[i/o/max] = 14.2377ns/14.2377ns/14.2377ns  area = 5554.36um²
  *
  * fpga@fp16: delay[i/o/max] = 42.856ns/43.550ns/42.856ns  area = 2088luts + 0ff
  *
  * 55nm@fp16: delay[i/o/max] = 9.4318ns/9.4318ns/9.4318ns  area = 3026.52um²
  *
  * fpga@bf16: delay[i/o/max] = 40.678ns/41.537ns/40.678ns  area = 1908luts + 0ff
  *
  * 55nm@bf16: delay[i/o/max] = 9.5703ns/9.5703ns/9.5196ns  area = 3258.92um²
  *
  * @param aFmt   The format of the first operand
  * @param bFmt   The format of the second operand
  * @param outFmt The format of the result
  * @param policy The numeric policy
  */
class FpRippleAdd(aFmt: FpFormat, bFmt: FpFormat, outFmt: FpFormat, policy: FpPolicy = FpPolicy())
    extends FpAddBase(aFmt, bFmt, outFmt, policy, w => Module(new UIntRippleAdd(w)))

object FpRippleAddFp64 extends App {
  ExportForAnalysis(
    new FpRippleAdd(FpFormat.Fp64, FpFormat.Fp64, FpFormat.Fp64),
    args,
    FpExport.opts,
  )
}

object FpRippleAddFp32 extends App {
  ExportForAnalysis(
    new FpRippleAdd(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32),
    args,
    FpExport.opts,
  )
}

object FpRippleAddFp16 extends App {
  ExportForAnalysis(
    new FpRippleAdd(FpFormat.Fp16, FpFormat.Fp16, FpFormat.Fp16),
    args,
    FpExport.opts,
  )
}

object FpRippleAddBf16 extends App {
  ExportForAnalysis(
    new FpRippleAdd(FpFormat.Bf16, FpFormat.Bf16, FpFormat.Bf16),
    args,
    FpExport.opts,
  )
}
