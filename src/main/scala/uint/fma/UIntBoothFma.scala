package harith.uint

import chisel3._
import chisel3.util._
import hammer.Export

/**
  * A fused multiply-adder using modified Booth radix-4 partial products.
  *
  * The addend is merged into the Booth partial product heap, so fewer partial products than the AND
  * based [[UIntTreeFma]] are reduced by a single carry propagate adder.
  *
  * @param width          The width of the operands
  * @param reductionStyle The reduction style of the partial product tree
  * @param adder          The final carry propagate adder, which must be `2 * width + 1` bits wide
  *
  * delay = 13.533, area = 2642 @32bit@fpga
  * delay = 5.5765, area = 18796.68 @32bit@55nm
  */
class UIntBoothFma(val width: Int, val reductionStyle: ReductionStyle, adder: => UIntAdder)
    extends UIntFma {
  val io = IO(new UIntFmaIO(width))
  require(width > 0, "width must be positive")

  val outputWidth = 2 * width + 1

  val partial        = MultiplierUtils.boothColumns(io.mul1, io.mul2, width, extraColumns = 1)
  val columns        = FmaUtils.withAddend(partial, io.add, outputWidth)
  val (lower, upper) = MultiplierUtils.toRows(MultiplierUtils.reduce(columns, reductionStyle))

  val finalAdder = Module(adder)
  require(
    finalAdder.io.src1.getWidth == outputWidth,
    "the final adder must be 2 * width + 1 bits wide",
  )
  finalAdder.io.src1  := lower
  finalAdder.io.src2  := upper
  finalAdder.io.carry := false.B
  io.output           := finalAdder.io.output(2 * width, 0)

  override def latency: Int = finalAdder.latency
}

object UIntBoothFma extends App {
  Export(
    new UIntBoothFma(
      32,
      ReductionStyle.Dadda,
      new UIntPrefixAdder(65, PrefixStyle.KoggeStone),
    ),
    args,
    Array(
      "--lowering-options=mitigateVivadoArrayIndexConstPropBug,disallowLocalVariables,disallowPackedArrays",
    ),
  )
}
