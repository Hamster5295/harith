package harith.uint

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis

/**
  * A fused multiply-adder using AND partial products.
  *
  * The addend is merged into the partial product heap, so the reduction tree produces two rows that
  * a single carry propagate adder resolves. It is one adder cheaper than [[UIntComposedFma]].
  *
  * fpga@32bit: delay[i/o/max] = 10.448ns/11.020ns/10.448ns  area = 1704luts + 0ff
  *
  * 55nm@32bit: delay[i/o/max] = 4.8911ns/4.8911ns/4.8911ns  area = 11172.00um²
  *
  * @param width          The width of the operands
  * @param reductionStyle The reduction style of the partial product tree
  * @param adder          The final carry propagate adder, which must be `2 * width + 1` bits wide
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
  ExportForAnalysis(
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
