package harith.sint

import chisel3._
import harith.ExportForAnalysis
import harith.uint.MulUtils
import harith.uint.PrefixStyle
import harith.uint.ReductionStyle
import harith.uint.UIntAdd
import harith.uint.UIntPrefixAdd

/**
  * A signed modified Booth radix-4 tree multiplier.
  *
  * Both operands are sign extended; the multiplier's overlapping triples select `0`, `+-a` or
  * `+-2a`, and the resulting partial products are reduced by the requested
  * [[harith.uint.ReductionStyle]] and added by the supplied [[harith.uint.UIntAdd]]. The product is
  * two's complement in `2 * width` bits.
  *
  * fpga@32bit: delay[i/o/max] = 10.235ns/10.807ns/10.235ns  area = 3030luts + 0ff
  *
  * 55nm@32bit: delay[i/o/max] = 5.0887ns/5.0887ns/5.0887ns  area = 15983.52um²
  *
  * @param width          The width of the operands
  * @param reductionStyle The reduction style of the partial product tree
  * @param adder          The final carry propagate adder factory, given the `2 * width` bit width
  */
class SIntBoothMul(
    val width:          Int,
    val reductionStyle: ReductionStyle,
    adder:              Int => UIntAdd,
) extends SIntMul {
  val io = IO(new SIntMulIO(width))
  require(width > 0, "width must be positive")

  val columns        = SIntMulUtils.boothColumns(io.src1, io.src2, width)
  val (lower, upper) = MulUtils.toRows(MulUtils.reduce(columns, reductionStyle))

  val finalAdd = Module(adder(2 * width))
  finalAdd.io.src1  := lower
  finalAdd.io.src2  := upper
  finalAdd.io.carry := false.B
  io.output         := finalAdd.io.output(2 * width - 1, 0).asSInt

  override def latency: Int = finalAdd.latency
}

object SIntBoothMul extends App {
  ExportForAnalysis(
    new SIntBoothMul(32, ReductionStyle.Dadda, w => new UIntPrefixAdd(w, PrefixStyle.KoggeStone)),
    args,
    SIntExport.opts,
  )
}
