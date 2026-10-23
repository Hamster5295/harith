package harith.sint

import chisel3._
import harith.ExportForAnalysis
import harith.uint.UIntMacroMul
import harith.uint.UIntMul

/**
  * A signed FMA composed from an unsigned magnitude multiplier and a signed add.
  *
  * The significands are converted to magnitudes, multiplied by the supplied [[harith.uint.UIntMul]]
  * and negated according to the operand signs, then the signed addend is added. This is the
  * sign-magnitude alternative to [[SIntMacroFma]].
  *
  * fpga@32bit: delay[i/o/max] = 11.330ns/11.901ns/11.330ns  area = 239luts + 0ff
  *
  * 55nm@32bit: delay[i/o/max] = 5.8455ns/5.8455ns/5.8455ns  area = 18356.80um²
  *
  * @param width The width of the multiplicands
  * @param mul   The unsigned magnitude multiplier factory
  */
class SIntComposedFma(val width: Int, mul: Int => UIntMul) extends SIntFma {
  val io = IO(new SIntFmaIO(width))
  require(width > 0, "width must be positive")

  val a     = io.mul1
  val b     = io.mul2
  val signP = (a < 0.S) ^ (b < 0.S)

  val magnitude = Module(mul(width))
  magnitude.io.src1 := Mux(a < 0.S, -a, a).asUInt
  magnitude.io.src2 := Mux(b < 0.S, -b, b).asUInt

  val prodWide = magnitude.io.output.pad(2 * width + 1).asSInt
  val signR    = SIntMulUtils.pipe(signP, magnitude.latency)
  val addR     = SIntMulUtils.pipe(io.add, magnitude.latency)
  val product  = Mux(signR, -prodWide, prodWide)
  val addend   = addR.pad(2 * width + 1)
  io.output := product + addend

  override def latency: Int = magnitude.latency
}

object SIntComposedFma extends App {
  ExportForAnalysis(
    new SIntComposedFma(32, w => new UIntMacroMul(w)),
    args,
    SIntExport.opts,
  )
}
