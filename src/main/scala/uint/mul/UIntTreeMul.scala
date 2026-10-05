package harith.uint

import chisel3._
import chisel3.util._
import hammer.Export

/**
  * A carry save tree multiplier using AND partial products.
  *
  * The partial product matrix is reduced to two rows by a Wallace or Dadda network and the two
  * rows are added by the supplied [[UIntAdd]]. The reduction is combinational, so the latency is
  * the latency of the final adder.
  *
  * fpga@32bit: delay = 13.118ns  area = 1616luts + 0ff
  *
  * 55nm@32bit: delay = 5.1937ns  area = 10559.36um²
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
  Export(
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
