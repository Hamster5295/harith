package harith.sint

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis
import harith.uint.MulUtils
import harith.uint.PrefixStyle
import harith.uint.ReductionStyle
import harith.uint.UIntAdd
import harith.uint.UIntPrefixAdd

/**
  * A pipelined signed carry save tree multiplier using Baugh-Wooley partial products.
  *
  * The reduction levels are distributed over `stages` register layers; `stages = 0` is the
  * combinational tree. The latency is `stages` plus the final adder latency.
  *
  * fpga@32bit-2cyc: delay[i/o/max] = 6.310ns/4.255ns/4.534ns  area = 1611luts + 208ff
  *
  * 55nm@32bit-2cyc: delay[i/o/max] = 1.8000ns/2.4505ns/2.4505ns  area = 11618.60um²
  *
  * @param width          The width of the operands
  * @param reductionStyle The reduction style of the partial product tree
  * @param adder          The final carry propagate adder factory, given the `2 * width` bit width
  * @param stages         The number of pipeline register layers in the reduction tree
  */
class SIntPipelinedTreeMul(
    val width:          Int,
    val reductionStyle: ReductionStyle,
    adder:              Int => UIntAdd,
    val stages:         Int,
) extends SIntMul {
  val io = IO(new SIntMulIO(width))
  require(width > 0, "width must be positive")
  require(stages >= 0, "stages must be non-negative")

  var columns = SIntMulUtils.baughWooleyColumns(io.src1, io.src2, width)
  val levels  = MulUtils.schedule(MulUtils.heights(columns), reductionStyle)
  val groups  = if (stages == 0) Seq(levels) else MulUtils.partition(levels, stages)

  for (group <- groups) {
    for (level <- group) columns = MulUtils.applyLevel(columns, level)
    if (stages > 0) columns = columns.map(_.map(bit => RegNext(bit)))
  }

  val (lower, upper) = MulUtils.toRows(columns)

  val finalAdd = Module(adder(2 * width))
  finalAdd.io.src1  := lower
  finalAdd.io.src2  := upper
  finalAdd.io.carry := false.B
  io.output         := finalAdd.io.output(2 * width - 1, 0).asSInt

  override def latency: Int = stages + finalAdd.latency
}

object SIntPipelinedTreeMul extends App {
  ExportForAnalysis(
    new SIntPipelinedTreeMul(
      32,
      ReductionStyle.Dadda,
      w => new UIntPrefixAdd(w, PrefixStyle.KoggeStone),
      2,
    ),
    args,
    SIntExport.opts,
  )
}
