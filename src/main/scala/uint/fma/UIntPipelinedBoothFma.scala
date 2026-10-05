package harith.uint

import chisel3._
import chisel3.util._
import hammer.Export

/**
  * A pipelined fused multiply-adder using modified Booth radix-4 partial products.
  *
  * The addend is merged into the Booth partial product heap, the reduction levels are distributed
  * over `stages` register layers and the two reduced rows are added by the supplied [[UIntAdd]].
  * The throughput is one FMA per cycle and the latency is `stages` plus the adder latency.
  *
  * fpga@32bit-2cyc: delay = 2.360ns  area = 2441luts + 454ff
  *
  * 55nm@32bit-2cyc: delay = 3.8220ns  area = 18393.76um²
  *
  * @param width          The width of the operands
  * @param reductionStyle The reduction style of the partial product tree
  * @param adder          The final carry propagate adder, which must be `2 * width + 1` bits wide
  * @param stages         The number of pipeline register layers in the reduction tree
  */
class UIntPipelinedBoothFma(
    val width:          Int,
    val reductionStyle: ReductionStyle,
    adder:              => UIntAdd,
    val stages:         Int,
) extends UIntFma {
  val io = IO(new UIntFmaIO(width))
  require(width > 0, "width must be positive")
  require(stages >= 0, "stages must be non-negative")

  val outputWidth = 2 * width + 1

  var columns = FmaUtils.withAddend(
    MulUtils.boothColumns(io.mul1, io.mul2, width, extraColumns = 1),
    io.add,
    outputWidth,
  )
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

object UIntPipelinedBoothFma extends App {
  Export(
    new UIntPipelinedBoothFma(
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
