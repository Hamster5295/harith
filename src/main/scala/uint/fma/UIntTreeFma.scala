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
  * fpga@32bit-dadda: delay[i/o/max] = 10.448ns/11.020ns/10.448ns  area = 1704luts + 0ff
  *
  * 55nm@32bit-dadda: delay[i/o/max] = 4.8911ns/4.8911ns/4.8911ns  area = 11172.00um²
  *
  * fpga@32bit-wallace: delay[i/o/max] = 10.359ns/10.931ns/10.359ns  area = 1662luts + 0ff
  *
  * 55nm@32bit-wallace: delay[i/o/max] = 4.7340ns/4.7340ns/4.7340ns  area = 11436.8800um²
  *
  * @param width          The width of the operands
  * @param reductionStyle The reduction style of the partial product tree
  * @param adder          The factory of the final carry propagate adder, given the `2 * width + 1`
  *                       bit width
  */
class UIntTreeFma(val width: Int, val reductionStyle: ReductionStyle, adder: Int => UIntAdd)
    extends UIntFma {
  val io = IO(new UIntFmaIO(width))
  require(width > 0, "width must be positive")

  val outputWidth = 2 * width + 1

  val partial        = MulUtils.andColumns(io.mul1, io.mul2, width)
  val columns        = FmaUtils.withAddend(partial, io.add, outputWidth)
  val (lower, upper) = MulUtils.toRows(MulUtils.reduce(columns, reductionStyle))

  val finalAdd = Module(adder(outputWidth))
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
      width => new UIntPrefixAdd(width, PrefixStyle.KoggeStone),
    ),
    args,
    Array(
      "--lowering-options=mitigateVivadoArrayIndexConstPropBug,disallowLocalVariables,disallowPackedArrays",
    ),
  )
}

object UIntTreeFmaWallace extends App {
  ExportForAnalysis(
    new UIntTreeFma(
      32,
      ReductionStyle.Wallace,
      width => new UIntPrefixAdd(width, PrefixStyle.KoggeStone),
    ),
    args,
    Array(
      "--lowering-options=mitigateVivadoArrayIndexConstPropBug,disallowLocalVariables,disallowPackedArrays",
    ),
  )
}
