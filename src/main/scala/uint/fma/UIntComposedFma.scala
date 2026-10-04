package harith.uint

import chisel3._
import chisel3.util._

/**
  * An FMA composed from a [[UIntMultiplier]] and a [[UIntAdder]].
  *
  * The supplied units are wired in series, so the product and the addend go through two carry
  * propagate adders. This is the flexible and reuse oriented option: pairing a small multiplier
  * with a ripple adder gives the cheapest FMA, while a tree multiplier with a prefix adder gives a
  * fast one. The latency is the sum of the two unit latencies.
  *
  * @param width The width of the operands
  * @param mul   The multiplier, whose output must be `2 * width` bits wide
  * @param adder The final adder, which must be `2 * width + 1` bits wide
  */
class UIntComposedFma(val width: Int, mul: => UIntMultiplier, adder: => UIntAdder) extends UIntFma {
  val io = IO(new UIntFmaIO(width))
  require(width > 0, "width must be positive")

  val outputWidth = 2 * width + 1

  val multiplier = Module(mul)
  require(multiplier.io.output.getWidth == 2 * width, "the multiplier must be 2 * width bits wide")
  multiplier.io.src1 := io.src1
  multiplier.io.src2 := io.src2

  val finalAdder = Module(adder)
  require(
    finalAdder.io.src1.getWidth == outputWidth,
    "the final adder must be 2 * width + 1 bits wide",
  )
  finalAdder.io.src1  := multiplier.io.output.pad(outputWidth)
  finalAdder.io.src2  := io.addend.pad(outputWidth)
  finalAdder.io.carry := false.B
  io.output           := finalAdder.io.output(2 * width, 0)

  override def latency: Int = multiplier.latency + finalAdder.latency
}
