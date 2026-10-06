package harith.uint

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis

/**
  * A modified Booth radix-4 carry save tree multiplier.
  *
  * The multiplier is recoded so that only about half as many partial products as the AND based
  * [[UIntTreeMul]] are generated. The partial products are reduced to two rows by a Wallace
  * or Dadda network and the two rows are added by the supplied [[UIntAdd]].
  *
  * fpga@32bit: delay[i/o/max] = 10.234ns/10.806ns/10.234ns  area = 2269luts + 0ff
  *
  * 55nm@32bit: delay[i/o/max] = 5.3502ns/5.3502ns/5.3502ns  area = 16288.16um²
  *
  * @param width          The width of the operands
  * @param reductionStyle The reduction style of the partial product tree
  * @param adder          The final carry propagate adder, which must be `2 * width` bits wide
  */
class UIntBoothMul(
    val width:          Int,
    val reductionStyle: ReductionStyle,
    adder:              => UIntAdd,
) extends Module
    with UIntMul {
  val io = IO(new UIntMulIO(width))
  require(width > 0, "width must be positive")

  val columns =
    MulUtils.reduce(MulUtils.boothColumns(io.src1, io.src2, width), reductionStyle)
  val (lower, upper) = MulUtils.toRows(columns)

  val finalAdd = Module(adder)
  require(finalAdd.io.src1.getWidth == 2 * width, "the final adder must be 2 * width bits wide")
  finalAdd.io.src1  := lower
  finalAdd.io.src2  := upper
  finalAdd.io.carry := false.B
  io.output         := finalAdd.io.output(2 * width - 1, 0)

  override def latency: Int = finalAdd.latency
}

object UIntBoothMul extends App {
  ExportForAnalysis(
    new UIntBoothMul(
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
