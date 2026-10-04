package harith.uint

import chisel3._
import chisel3.util._

/**
  * A pipelined carry save tree multiplier using AND partial products.
  *
  * The reduction levels are distributed over `stages` register layers and the two reduced rows are
  * added by the supplied [[UIntAdder]]. The throughput is one product per cycle and the overall
  * latency is `stages` plus the latency of the final adder, so a pipelined adder can shorten the
  * final add.
  *
  * @param width          The width of the operands
  * @param reductionStyle The reduction style of the partial product tree
  * @param adder          The final carry propagate adder, which must be `2 * width` bits wide
  * @param stages         The number of pipeline register layers in the reduction tree
  */
class UIntPipelinedTreeMultiplier(
    val width:          Int,
    val reductionStyle: ReductionStyle,
    adder:              => UIntAdder,
    val stages:         Int,
) extends Module
    with UIntMultiplier {
  val io = IO(new UIntMultiplierIO(width))
  require(width > 0, "width must be positive")
  require(stages >= 0, "stages must be non-negative")

  var columns = MultiplierUtils.andColumns(io.src1, io.src2, width)
  val levels  = MultiplierUtils.schedule(MultiplierUtils.heights(columns), reductionStyle)
  val groups  = if (stages == 0) Seq(levels) else MultiplierUtils.partition(levels, stages)

  for (group <- groups) {
    for (level <- group) columns = MultiplierUtils.applyLevel(columns, level)
    if (stages > 0) columns = columns.map(_.map(bit => RegNext(bit)))
  }

  val (lower, upper) = MultiplierUtils.toRows(columns)

  val finalAdder = Module(adder)
  require(finalAdder.io.src1.getWidth == 2 * width, "the final adder must be 2 * width bits wide")
  finalAdder.io.src1  := lower
  finalAdder.io.src2  := upper
  finalAdder.io.carry := false.B
  io.output           := finalAdder.io.output(2 * width - 1, 0)

  override def latency: Int = stages + finalAdder.latency
}
