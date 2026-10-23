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
  * A pipelined signed fused multiply-adder using signed modified Booth partial products with the
  * addend merged into the reduction.
  *
  * The reduction levels are distributed over `stages` register layers; the sign correction is
  * delayed to the final adder. The latency is `stages` plus the final adder latency.
  *
  * fpga@32bit-2cyc: delay[i/o/max] = 5.754ns/4.436ns/3.865ns  area = 3142luts + 459ff
  *
  * 55nm@32bit-2cyc: delay[i/o/max] = 3.4471ns/2.3025ns/3.4471ns  area = 18142.60um²
  *
  * @param width          The width of the multiplicands
  * @param reductionStyle The reduction style of the partial product tree
  * @param adder          The final carry propagate adder factory, given the `2 * width` bit width
  * @param stages         The number of pipeline register layers in the reduction tree
  */
class SIntPipelinedBoothFma(
    val width:          Int,
    val reductionStyle: ReductionStyle,
    adder:              Int => UIntAdd,
    val stages:         Int,
) extends SIntFma {
  val io = IO(new SIntFmaIO(width))
  require(width > 0, "width must be positive")
  require(stages >= 0, "stages must be non-negative")

  private val nCols = 2 * width

  var columns =
    SIntMulUtils.withAddend(SIntMulUtils.boothColumns(io.mul1, io.mul2, width), io.add, width)
  val levels = MulUtils.schedule(MulUtils.heights(columns), reductionStyle)
  val groups = if (stages == 0) Seq(levels) else MulUtils.partition(levels, stages)

  for (group <- groups) {
    for (level <- group) columns = MulUtils.applyLevel(columns, level)
    if (stages > 0) columns = columns.map(_.map(bit => RegNext(bit)))
  }

  val (lower, upper) = MulUtils.toRows(columns)

  val finalAdd = Module(adder(nCols))
  finalAdd.io.src1  := lower
  finalAdd.io.src2  := upper
  finalAdd.io.carry := false.B

  val total = stages + finalAdd.latency
  val pSign = SIntMulUtils.pipe(SIntMulUtils.productSign(io.mul1, io.mul2, width), total)
  val cSign = SIntMulUtils.pipe(io.add(nCols - 1), total)
  val sum   = finalAdd.io.output(nCols - 1, 0)
  val sMsb  = sum(nCols - 1)
  val ovf   = (pSign === cSign) && (sMsb =/= pSign)
  val top   = sMsb ^ ovf
  io.output := Cat(top, sum).asSInt

  override def latency: Int = total
}

object SIntPipelinedBoothFma extends App {
  ExportForAnalysis(
    new SIntPipelinedBoothFma(
      32,
      ReductionStyle.Dadda,
      w => new UIntPrefixAdd(w, PrefixStyle.KoggeStone),
      2,
    ),
    args,
    SIntExport.opts,
  )
}
