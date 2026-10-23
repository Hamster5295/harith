package harith.sint

import chisel3._
import harith.ExportForAnalysis
import harith.uint.UIntMacroMul
import harith.uint.UIntMul

/**
  * A signed multiplier built from an unsigned magnitude multiplier and sign correction.
  *
  * The operands are converted to magnitudes, multiplied by the supplied [[harith.uint.UIntMul]],
  * and the product is negated when the operand signs differ. This is the sign-magnitude
  * alternative to [[SIntMacroMul]].
  *
  * fpga@32bit: delay[i/o/max] = 11.896ns/12.467ns/11.896ns  area = 300luts + 0ff
  *
  * 55nm@32bit: delay[i/o/max] = 5.0521ns/5.0521ns/5.0521ns  area = 17607.24um²
  *
  * @param width The width of the operands
  * @param mul   The unsigned magnitude multiplier factory
  */
class SIntMagnitudeMul(val width: Int, mul: Int => UIntMul) extends SIntMul {
  val io = IO(new SIntMulIO(width))
  require(width > 0, "width must be positive")

  val a    = io.src1
  val b    = io.src2
  val sign = (a < 0.S) ^ (b < 0.S)

  val magnitude = Module(mul(width))
  magnitude.io.src1 := Mux(a < 0.S, -a, a).asUInt
  magnitude.io.src2 := Mux(b < 0.S, -b, b).asUInt

  val wide   = magnitude.io.output.pad(2 * width + 1).asSInt
  val signR  = SIntMulUtils.pipe(sign, magnitude.latency)
  val signed = Mux(signR, -wide, wide)
  io.output := signed(2 * width - 1, 0).asSInt

  override def latency: Int = magnitude.latency
}

object SIntMagnitudeMul extends App {
  ExportForAnalysis(
    new SIntMagnitudeMul(32, w => new UIntMacroMul(w)),
    args,
    SIntExport.opts,
  )
}
