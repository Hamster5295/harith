package harith.uint

import chisel3._
import chisel3.util._
import hammer.Export

/**
  * A fused multiply-adder using AND partial products.
  *
  * The addend is merged into the partial product heap, so the reduction tree produces two rows that
  * a single carry propagate adder resolves. It is one adder cheaper than [[UIntComposedFma]].
  *
  * @param width          The width of the operands
  * @param reductionStyle The reduction style of the partial product tree
  * @param adder          The final carry propagate adder, which must be `2 * width + 1` bits wide
  *
  * delay = 13.250, area = 1704 @32bit@fpga
  * delay = 5.3544, area = 11301.36 @32bit@55nm
  */
class UIntTreeFma(val width: Int, val reductionStyle: ReductionStyle, adder: => UIntAdd)
    extends UIntFma {
  val io = IO(new UIntFmaIO(width))
  require(width > 0, "width must be positive")

  val outputWidth = 2 * width + 1

  val partial        = MulUtils.andColumns(io.mul1, io.mul2, width)
  val columns        = FmaUtils.withAddend(partial, io.add, outputWidth)
  val (lower, upper) = MulUtils.toRows(MulUtils.reduce(columns, reductionStyle))

  val finalAdd = Module(adder)
  require(
    finalAdd.io.src1.getWidth == outputWidth,
    "the final adder must be 2 * width + 1 bits wide",
  )
  finalAdd.io.src1  := lower
  finalAdd.io.src2  := upper
  finalAdd.io.carry := false.B
  io.output         := finalAdd.io.output(2 * width, 0)

  override def latency: Int = finalAdd.latency
}

object UIntTreeFma extends App {
  Export(
    new UIntTreeFma(
      32,
      ReductionStyle.Dadda,
      new UIntPrefixAdd(65, PrefixStyle.KoggeStone),
    ),
    args,
    Array(
      "--lowering-options=mitigateVivadoArrayIndexConstPropBug,disallowLocalVariables,disallowPackedArrays",
    ),
  )
}
