package harith.uint

import chisel3._
import chisel3.util._

/**
  * A carry save tree multiplier using AND partial products.
  *
  * The partial product matrix is reduced to two rows by a Wallace or Dadda network and the two
  * rows are added by the supplied [[UIntAdder]]. The reduction is combinational, so the latency is
  * the latency of the final adder.
  *
  * @param width          The width of the operands
  * @param reductionStyle The reduction style of the partial product tree
  * @param adder          The final carry propagate adder, which must be `2 * width` bits wide
  */
class UIntTreeMultiplier(
    val width:          Int,
    val reductionStyle: ReductionStyle,
    adder:              => UIntAdder,
) extends Module
    with UIntMultiplier {
  val io = IO(new UIntMultiplierIO(width))
  require(width > 0, "width must be positive")

  val columns =
    MultiplierUtils.reduce(MultiplierUtils.andColumns(io.src1, io.src2, width), reductionStyle)
  val (lower, upper) = MultiplierUtils.toRows(columns)

  val finalAdder = Module(adder)
  require(finalAdder.io.src1.getWidth == 2 * width, "the final adder must be 2 * width bits wide")
  finalAdder.io.src1  := lower
  finalAdder.io.src2  := upper
  finalAdder.io.carry := false.B
  io.output           := finalAdder.io.output(2 * width - 1, 0)

  override def latency: Int = finalAdder.latency
}
