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
  * fpga@32bit: delay = 13.533ns  area = 2642luts + 0ff
  *
  * 55nm@32bit: delay = 5.5765ns  area = 18796.68um²
  *
  * @param width          The width of the operands
  * @param reductionStyle The reduction style of the partial product tree
  * @param adder          The final carry propagate adder, which must be `2 * width + 1` bits wide
  */
class UIntBoothFma(val width: Int, val reductionStyle: ReductionStyle, adder: => UIntAdd)
    extends UIntFma {
  val io = IO(new UIntFmaIO(width))
  require(width > 0, "width must be positive")

  val outputWidth = 2 * width + 1

  val partial        = MulUtils.boothColumns(io.mul1, io.mul2, width, extraColumns = 1)
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

object UIntBoothFma extends App {
  Export(
    new UIntBoothFma(
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
