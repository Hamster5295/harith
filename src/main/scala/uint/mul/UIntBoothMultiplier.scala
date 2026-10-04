package harith.uint

import chisel3._
import chisel3.util._
import hammer.Export

/**
  * A modified Booth radix-4 carry save tree multiplier.
  *
  * The multiplier is recoded so that only about half as many partial products as the AND based
  * [[UIntTreeMultiplier]] are generated. The partial products are reduced to two rows by a Wallace
  * or Dadda network and the two rows are added by the supplied [[UIntAdder]].
  *
  * @param width          The width of the operands
  * @param reductionStyle The reduction style of the partial product tree
  * @param adder          The final carry propagate adder, which must be `2 * width` bits wide
  */
class UIntBoothMultiplier(
    val width:          Int,
    val reductionStyle: ReductionStyle,
    adder:              => UIntAdder,
) extends Module
    with UIntMultiplier {
  val io = IO(new UIntMultiplierIO(width))
  require(width > 0, "width must be positive")

  val columns =
    MultiplierUtils.reduce(MultiplierUtils.boothColumns(io.src1, io.src2, width), reductionStyle)
  val (lower, upper) = MultiplierUtils.toRows(columns)

  val finalAdder = Module(adder)
  require(finalAdder.io.src1.getWidth == 2 * width, "the final adder must be 2 * width bits wide")
  finalAdder.io.src1  := lower
  finalAdder.io.src2  := upper
  finalAdder.io.carry := false.B
  io.output           := finalAdder.io.output(2 * width - 1, 0)

  override def latency: Int = finalAdder.latency
}

object UIntBoothMultiplier extends App {
  Export(
    new UIntBoothMultiplier(
      32,
      ReductionStyle.Dadda,
      new UIntPrefixAdder(64, PrefixStyle.KoggeStone),
    ),
    args,
    Array(
      "--lowering-options=mitigateVivadoArrayIndexConstPropBug,disallowLocalVariables,disallowPackedArrays",
    ),
  )
}
