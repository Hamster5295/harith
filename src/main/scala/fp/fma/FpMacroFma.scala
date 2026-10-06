package harith.fp

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis
import harith.uint._

/**
  * A floating-point fused multiply-adder with inferred significand and alignment datapaths.
  *
  * fpga@fp32: delay[i/o/max] = 49.853ns/50.416ns/49.853ns  area = 5107luts + 0ff
  *
  * 55nm@fp32: delay[i/o/max] = 40.4856ns/40.4856ns/38.8996ns  area = 28242.20um²
  *
  * @param aFmt   The format of the multiplicand
  * @param bFmt   The format of the multiplier
  * @param cFmt   The format of the addend
  * @param outFmt The format of the result
  * @param policy The numeric policy
  */
class FpMacroFma(
    aFmt:   FpFormat,
    bFmt:   FpFormat,
    cFmt:   FpFormat,
    outFmt: FpFormat,
    policy: FpPolicy = FpPolicy(),
) extends FpFmaBase(
      aFmt,
      bFmt,
      cFmt,
      outFmt,
      policy,
      w => Module(new UIntMacroMul(w)),
      w => Module(new UIntMacroAdd(w)),
    )

object FpMacroFma extends App {
  ExportForAnalysis(
    new FpMacroFma(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32),
    args,
    FpExport.opts,
  )
}

/**
  * A bfloat16 by bfloat16 plus float32 to float32 fused multiply-adder.
  *
  * fpga@bf16: delay[i/o/max] = 43.892ns/44.456ns/43.892ns  area = 3864luts + 0ff
  *
  * 55nm@bf16: delay[i/o/max] = 21.0332ns/21.0332ns/19.8272ns  area = 10189.20um²
  *
  */
class FpBf16Fp32Fma extends FpMacroFma(FpFormat.Bf16, FpFormat.Bf16, FpFormat.Fp32, FpFormat.Fp32)

/**
  * A float16 by float16 plus float32 to float32 fused multiply-adder.
  *
  * fpga@fp16: delay[i/o/max] = 45.491ns/46.062ns/45.491ns  area = 3889luts + 0ff
  *
  * 55nm@fp16: delay[i/o/max] = 17.4532ns/17.4532ns/16.5831ns  area = 8588.72um²
  *
  */
class Fp16Fp32Fma extends FpMacroFma(FpFormat.Fp16, FpFormat.Fp16, FpFormat.Fp32, FpFormat.Fp32)

/**
  * A float32 fused multiply-adder.
  *
  * fpga@fp32: delay[i/o/max] = 49.853ns/50.416ns/49.853ns  area = 5107luts + 0ff
  *
  * 55nm@fp32: delay[i/o/max] = 40.4856ns/40.4856ns/38.8996ns  area = 28242.20um²
  *
  */
class Fp32Fma extends FpMacroFma(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32)

/**
  * A float64 fused multiply-adder.
  *
  * fpga@fp64: delay[i/o/max] = 64.329ns/65.309ns/64.329ns  area = 11083luts + 0ff
  *
  * 55nm@fp64: delay[i/o/max] = 75.8697ns/75.8697ns/74.9867ns  area = 72739.52um²
  *
  */
class Fp64Fma extends FpMacroFma(FpFormat.Fp64, FpFormat.Fp64, FpFormat.Fp64, FpFormat.Fp64)

object FpBf16Fp32Fma extends App { ExportForAnalysis(new FpBf16Fp32Fma, args, FpExport.opts) }
object Fp16Fp32Fma   extends App { ExportForAnalysis(new Fp16Fp32Fma, args, FpExport.opts)   }
object Fp32Fma       extends App { ExportForAnalysis(new Fp32Fma, args, FpExport.opts)       }
object Fp64Fma       extends App { ExportForAnalysis(new Fp64Fma, args, FpExport.opts)       }
