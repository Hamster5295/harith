package harith.uint

import chisel3._
import chisel3.util._

/**
  * A pipelined fused multiply-adder using modified Booth radix-4 partial products.
  *
  * The addend is merged into the Booth partial product heap, the reduction levels are distributed
  * over `stages` register layers and the two reduced rows are added by the supplied [[UIntAdder]].
  * The throughput is one FMA per cycle and the latency is `stages` plus the adder latency.
  *
  * @param width          The width of the operands
  * @param reductionStyle The reduction style of the partial product tree
  * @param adder          The final carry propagate adder, which must be `2 * width + 1` bits wide
  * @param stages         The number of pipeline register layers in the reduction tree
  */
class UIntPipelinedBoothFma(
    val width:          Int,
    val reductionStyle: ReductionStyle,
    adder:              => UIntAdder,
    val stages:         Int,
) extends UIntFma {
  val io = IO(new UIntFmaIO(width))
  require(width > 0, "width must be positive")
  require(stages >= 0, "stages must be non-negative")

  val outputWidth = 2 * width + 1

  var columns = FmaUtils.withAddend(
    MultiplierUtils.boothColumns(io.src1, io.src2, width),
    io.addend,
    outputWidth,
  )
  val levels = MultiplierUtils.schedule(MultiplierUtils.heights(columns), reductionStyle)
  val groups = if (stages == 0) Seq(levels) else MultiplierUtils.partition(levels, stages)

  for (group <- groups) {
    for (level <- group) columns = MultiplierUtils.applyLevel(columns, level)
    if (stages > 0) columns = columns.map(_.map(bit => RegNext(bit)))
  }

  val (lower, upper) = MultiplierUtils.toRows(columns)

  val finalAdder = Module(adder)
  require(
    finalAdder.io.src1.getWidth == outputWidth,
    "the final adder must be 2 * width + 1 bits wide",
  )
  finalAdder.io.src1  := lower
  finalAdder.io.src2  := upper
  finalAdder.io.carry := false.B
  io.output           := finalAdder.io.output(2 * width, 0)

  override def latency: Int = stages + finalAdder.latency
}
