package harith.sint

import chisel3._
import harith.ExportForAnalysis
import harith.uint.MulUtils
import harith.uint.PrefixStyle
import harith.uint.ReductionStyle
import harith.uint.UIntAdd
import harith.uint.UIntPrefixAdd

/**
  * A signed carry save tree multiplier using Baugh-Wooley partial products.
  *
  * The sign row and column of the partial product matrix are inverted and the correction is folded
  * into the column heap, which is reduced by the requested [[harith.uint.ReductionStyle]] and added
  * by the supplied [[harith.uint.UIntAdd]]. The product is two's complement in `2 * width` bits.
  *
  * fpga@32bit: delay[i/o/max] = 10.016ns/10.588ns/10.016ns  area = 1622luts + 0ff
  *
  * 55nm@32bit: delay[i/o/max] = 4.6843ns/4.6843ns/4.6843ns  area = 10562.44um²
  *
  * @param width          The width of the operands
  * @param reductionStyle The reduction style of the partial product tree
  * @param adder          The final carry propagate adder factory, given the `2 * width` bit width
  */
class SIntTreeMul(
    val width:          Int,
    val reductionStyle: ReductionStyle,
    adder:              Int => UIntAdd,
) extends SIntMul {
  val io = IO(new SIntMulIO(width))
  require(width > 0, "width must be positive")

  val columns        = SIntMulUtils.baughWooleyColumns(io.src1, io.src2, width)
  val (lower, upper) = MulUtils.toRows(MulUtils.reduce(columns, reductionStyle))

  val finalAdd = Module(adder(2 * width))
  finalAdd.io.src1  := lower
  finalAdd.io.src2  := upper
  finalAdd.io.carry := false.B
  io.output         := finalAdd.io.output(2 * width - 1, 0).asSInt

  override def latency: Int = finalAdd.latency
}

object SIntTreeMul extends App {
  ExportForAnalysis(
    new SIntTreeMul(32, ReductionStyle.Dadda, w => new UIntPrefixAdd(w, PrefixStyle.KoggeStone)),
    args,
    SIntExport.opts,
  )
}
