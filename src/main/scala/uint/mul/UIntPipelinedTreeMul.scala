package harith.uint

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis

/**
  * A pipelined carry save tree multiplier using AND partial products.
  *
  * The reduction levels are distributed over `stages` register layers and the two reduced rows are
  * added by the supplied [[UIntAdd]]. The throughput is one product per cycle and the overall
  * latency is `stages` plus the latency of the final adder, so a pipelined adder can shorten the
  * final add.
  *
  * fpga@32bit-2cyc: delay[i/o/max] = 5.976ns/4.530ns/4.947ns  area = 1597luts + 207ff
  *
  * 55nm@32bit-2cyc: delay[i/o/max] = 1.8291ns/2.6226ns/2.6226ns  area = 11401.88um²
  *
  * @param width          The width of the operands
  * @param reductionStyle The reduction style of the partial product tree
  * @param adder          The final carry propagate adder, which must be `2 * width` bits wide
  * @param stages         The number of pipeline register layers in the reduction tree
  */
class UIntPipelinedTreeMul(
    val width:          Int,
    val reductionStyle: ReductionStyle,
    adder:              => UIntAdd,
    val stages:         Int,
) extends Module
    with UIntMul {
  val io = IO(new UIntMulIO(width))
  require(width > 0, "width must be positive")
  require(stages >= 0, "stages must be non-negative")

  var columns = MulUtils.andColumns(io.src1, io.src2, width)
  val levels  = MulUtils.schedule(MulUtils.heights(columns), reductionStyle)
  val groups  = if (stages == 0) Seq(levels) else MulUtils.partition(levels, stages)

  for (group <- groups) {
    for (level <- group) columns = MulUtils.applyLevel(columns, level)
    if (stages > 0) columns = columns.map(_.map(bit => RegNext(bit)))
  }

  val (lower, upper) = MulUtils.toRows(columns)

  val finalAdd = Module(adder)
  require(finalAdd.io.src1.getWidth == 2 * width, "the final adder must be 2 * width bits wide")
  finalAdd.io.src1  := lower
  finalAdd.io.src2  := upper
  finalAdd.io.carry := false.B
  io.output         := finalAdd.io.output(2 * width - 1, 0)

  override def latency: Int = stages + finalAdd.latency
}

object UIntPipelinedTreeMul extends App {
  ExportForAnalysis(
    new UIntPipelinedTreeMul(
      32,
      ReductionStyle.Dadda,
      new UIntPrefixAdd(64, PrefixStyle.KoggeStone),
      2,
    ),
    args,
    Array(
      "--lowering-options=mitigateVivadoArrayIndexConstPropBug,disallowLocalVariables,disallowPackedArrays",
    ),
  )
}
