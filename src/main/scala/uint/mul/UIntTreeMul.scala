package harith.uint

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis

/**
  * A carry save tree multiplier using AND partial products.
  *
  * The partial product matrix is reduced to two rows by a Wallace or Dadda network and the two
  * rows are added by the supplied [[UIntAdd]]. The reduction is combinational, so the latency is
  * the latency of the final adder.
  *
  * fpga@32bit: delay[i/o/max] = 10.316ns/10.888ns/10.316ns  area = 1616luts + 0ff
  *
  * 55nm@32bit: delay[i/o/max] = 4.8125ns/4.8125ns/4.8125ns  area = 10564.68um²
  *
  * @param width          The width of the operands
  * @param reductionStyle The reduction style of the partial product tree
  * @param adder          The final carry propagate adder, which must be `2 * width` bits wide
  */
class UIntTreeMul(
    val width:          Int,
    val reductionStyle: ReductionStyle,
    adder:              => UIntAdd,
) extends Module
    with UIntMul {
  val io = IO(new UIntMulIO(width))
  require(width > 0, "width must be positive")

  val columns =
    MulUtils.reduce(MulUtils.andColumns(io.src1, io.src2, width), reductionStyle)
  val (lower, upper) = MulUtils.toRows(columns)

  val finalAdd = Module(adder)
  require(finalAdd.io.src1.getWidth == 2 * width, "the final adder must be 2 * width bits wide")
  finalAdd.io.src1  := lower
  finalAdd.io.src2  := upper
  finalAdd.io.carry := false.B
  io.output         := finalAdd.io.output(2 * width - 1, 0)

  override def latency: Int = finalAdd.latency
}

object UIntTreeMul extends App {
  ExportForAnalysis(
    new UIntTreeMul(
      32,
      ReductionStyle.Dadda,
      new UIntPrefixAdd(64, PrefixStyle.KoggeStone),
    ),
    args,
    Array(
      "--lowering-options=mitigateVivadoArrayIndexConstPropBug,disallowLocalVariables,disallowPackedArrays",
    ),
  )
}
