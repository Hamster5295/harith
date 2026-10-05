package harith.fp

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis
import harith.uint._

/**
  * A floating-point adder with an inferred alignment adder.
  *
  * fpga@fp32: delay[i/o/max] = 40.688ns/41.268ns/40.688ns  area = 3925luts + 0ff
  *
  * 55nm@fp32: delay[i/o/max] = 14.5881ns/14.5881ns/14.5881ns  area = 5899.04um²
  *
  * @param aFmt   The format of the first operand
  * @param bFmt   The format of the second operand
  * @param outFmt The format of the result
  * @param policy The numeric policy
  */
class FpMacroAdd(aFmt: FpFormat, bFmt: FpFormat, outFmt: FpFormat, policy: FpPolicy = FpPolicy())
    extends FpAddBase(aFmt, bFmt, outFmt, policy, w => Module(new UIntMacroAdd(w)))

object FpMacroAdd extends App {
  ExportForAnalysis(
    new FpMacroAdd(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32),
    args,
    FpExport.opts,
  )
}

/**
  * A bfloat16 plus float32 to float32 adder.
  *
  * fpga@bf16: delay[i/o/max] = 40.466ns/41.038ns/40.466ns  area = 3540luts + 0ff
  *
  * 55nm@bf16: delay[i/o/max] = 13.5780ns/13.5780ns/13.5780ns  area = 5701.92um²
  *
  */
class FpBf16Fp32Add extends FpMacroAdd(FpFormat.Bf16, FpFormat.Fp32, FpFormat.Fp32)

/**
  * A float16 plus float32 to float32 adder.
  *
  * fpga@fp16: delay[i/o/max] = 39.785ns/40.349ns/39.785ns  area = 3578luts + 0ff
  *
  * 55nm@fp16: delay[i/o/max] = 13.2556ns/13.2556ns/13.2556ns  area = 5394.20um²
  *
  */
class Fp16Fp32Add extends FpMacroAdd(FpFormat.Fp16, FpFormat.Fp32, FpFormat.Fp32)

/**
  * A float32 adder.
  *
  * fpga@fp32: delay[i/o/max] = 40.688ns/41.268ns/40.688ns  area = 3925luts + 0ff
  *
  * 55nm@fp32: delay[i/o/max] = 14.5881ns/14.5881ns/14.5881ns  area = 5899.04um²
  *
  */
class Fp32Add extends FpMacroAdd(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32)

/**
  * A float64 adder.
  *
  * fpga@fp64: delay[i/o/max] = 48.521ns/49.502ns/48.521ns  area = 8433luts + 0ff
  *
  * 55nm@fp64: delay[i/o/max] = 30.5496ns/30.5496ns/30.5496ns  area = 15172.64um²
  *
  */
class Fp64Add extends FpMacroAdd(FpFormat.Fp64, FpFormat.Fp64, FpFormat.Fp64)

object FpBf16Fp32Add extends App { ExportForAnalysis(new FpBf16Fp32Add, args, FpExport.opts) }
object Fp16Fp32Add   extends App { ExportForAnalysis(new Fp16Fp32Add, args, FpExport.opts)   }
object Fp32Add       extends App { ExportForAnalysis(new Fp32Add, args, FpExport.opts)       }
object Fp64Add       extends App { ExportForAnalysis(new Fp64Add, args, FpExport.opts)       }
