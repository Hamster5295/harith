package harith.uint

import chisel3._
import chisel3.util._

/**
  * A fused multiply-adder using AND partial products.
  *
  * The addend is merged into the partial product heap, so the reduction tree produces two rows that
  * a single carry propagate adder resolves. It is one adder cheaper than [[UIntComposedFma]].
  *
  * @param width          The width of the operands
  * @param reductionStyle The reduction style of the partial product tree
  * @param adder          The final carry propagate adder, which must be `2 * width + 1` bits wide
  */
class UIntTreeFma(val width: Int, val reductionStyle: ReductionStyle, adder: => UIntAdder)
    extends UIntFma {
  val io = IO(new UIntFmaIO(width))
  require(width > 0, "width must be positive")

  val outputWidth = 2 * width + 1

  val partial        = MultiplierUtils.andColumns(io.mul1, io.mul2, width)
  val columns        = FmaUtils.withAddend(partial, io.add, outputWidth)
  val (lower, upper) = MultiplierUtils.toRows(MultiplierUtils.reduce(columns, reductionStyle))

  val finalAdder = Module(adder)
  require(
    finalAdder.io.src1.getWidth == outputWidth,
    "the final adder must be 2 * width + 1 bits wide",
  )
  finalAdder.io.src1  := lower
  finalAdder.io.src2  := upper
  finalAdder.io.carry := false.B
  io.output           := finalAdder.io.output(2 * width, 0)

  override def latency: Int = finalAdder.latency
}
