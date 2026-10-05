package harith.uint

import chisel3._
import chisel3.util._
import hammer.Export

/**
  * An FMA composed from a [[UIntMul]] and a [[UIntAdd]].
  *
  * The supplied units are wired in series, so the product and the addend go through two carry
  * propagate adders. This is the flexible and reuse oriented option: pairing a small multiplier
  * with a ripple adder gives the cheapest FMA, while a tree multiplier with a prefix adder gives a
  * fast one. The latency is the sum of the two unit latencies.
  *
  * @param width The width of the operands
  * @param mul   The multiplier, whose output must be `2 * width` bits wide
  * @param adder The final adder, which must be `2 * width + 1` bits wide
  *
  * delay = 15.715, area = 1990 @32bit@fpga
  * delay = 5.6945, area = 11760.56 @32bit@55nm
  */
class UIntComposedFma(val width: Int, mul: => UIntMul, adder: => UIntAdd) extends UIntFma {
  val io = IO(new UIntFmaIO(width))
  require(width > 0, "width must be positive")

  val outputWidth = 2 * width + 1

  val multiplier = Module(mul)
  require(multiplier.io.output.getWidth == 2 * width, "the multiplier must be 2 * width bits wide")
  multiplier.io.src1 := io.mul1
  multiplier.io.src2 := io.mul2

  val finalAdd = Module(adder)
  require(
    finalAdd.io.src1.getWidth == outputWidth,
    "the final adder must be 2 * width + 1 bits wide",
  )
  finalAdd.io.src1  := multiplier.io.output.pad(outputWidth)
  finalAdd.io.src2  := io.add.pad(outputWidth)
  finalAdd.io.carry := false.B
  io.output         := finalAdd.io.output(2 * width, 0)

  override def latency: Int = multiplier.latency + finalAdd.latency
}

object UIntComposedFma extends App {
  Export(
    new UIntComposedFma(
      32,
      new UIntTreeMul(
        32,
        ReductionStyle.Dadda,
        new UIntPrefixAdd(64, PrefixStyle.KoggeStone),
      ),
      new UIntPrefixAdd(65, PrefixStyle.KoggeStone),
    ),
    args,
    Array(
      "--lowering-options=mitigateVivadoArrayIndexConstPropBug,disallowLocalVariables,disallowPackedArrays",
    ),
  )
}
