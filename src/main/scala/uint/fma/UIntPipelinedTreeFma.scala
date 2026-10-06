package harith.uint

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis

/**
  * A pipelined fused multiply-adder using AND partial products.
  *
  * The addend is merged into the partial product heap, the reduction levels are distributed over
  * `stages` register layers and the two reduced rows are added by the supplied [[UIntAdd]]. The
  * throughput is one FMA per cycle and the latency is `stages` plus the adder latency.
  *
  * fpga@32bit-2cyc: delay[i/o/max] = 6.132ns/4.783ns/4.947ns  area = 1683luts + 211ff
  *
  * 55nm@32bit-2cyc: delay[i/o/max] = 1.9352ns/3.4922ns/2.9567ns  area = 12281.92um²
  *
  * @param width          The width of the operands
  * @param reductionStyle The reduction style of the partial product tree
  * @param adder          The final carry propagate adder, which must be `2 * width + 1` bits wide
  * @param stages         The number of pipeline register layers in the reduction tree
  */
class UIntPipelinedTreeFma(
    val width:          Int,
    val reductionStyle: ReductionStyle,
    adder:              => UIntAdd,
    val stages:         Int,
) extends UIntFma {
  val io = IO(new UIntFmaIO(width))
  require(width > 0, "width must be positive")
  require(stages >= 0, "stages must be non-negative")

  val outputWidth = 2 * width + 1

  var columns =
    FmaUtils.withAddend(MulUtils.andColumns(io.mul1, io.mul2, width), io.add, outputWidth)
  val levels = MulUtils.schedule(MulUtils.heights(columns), reductionStyle)
  val groups = if (stages == 0) Seq(levels) else MulUtils.partition(levels, stages)

  for (group <- groups) {
    for (level <- group) columns = MulUtils.applyLevel(columns, level)
    if (stages > 0) columns = columns.map(_.map(bit => RegNext(bit)))
  }

  val (lower, upper) = MulUtils.toRows(columns)

  val finalAdd = Module(adder)
  require(
    finalAdd.io.src1.getWidth == outputWidth,
    "the final adder must be 2 * width + 1 bits wide",
  )
  finalAdd.io.src1  := lower
  finalAdd.io.src2  := upper
  finalAdd.io.carry := false.B
  io.output         := finalAdd.io.output(2 * width, 0)

  override def latency: Int = stages + finalAdd.latency
}

object UIntPipelinedTreeFma extends App {
  ExportForAnalysis(
    new UIntPipelinedTreeFma(
      32,
      ReductionStyle.Dadda,
      new UIntPrefixAdd(65, PrefixStyle.KoggeStone),
      2,
    ),
    args,
    Array(
      "--lowering-options=mitigateVivadoArrayIndexConstPropBug,disallowLocalVariables,disallowPackedArrays",
    ),
  )
}
