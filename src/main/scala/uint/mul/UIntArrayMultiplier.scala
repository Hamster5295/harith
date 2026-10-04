package harith.uint

import chisel3._
import chisel3.util._

/**
  * A structural carry save array multiplier.
  *
  * Each partial product row is accumulated in carry save form with a row of full adders, and a
  * final ripple carry chain produces the product. The regular structure gives the lowest cost at
  * the price of an O(width) critical path. On FPGAs the inferred [[UIntMacroMultiplier]] is
  * usually preferable.
  *
  * @param width The width of the operands
  */
class UIntArrayMultiplier(val width: Int) extends UIntMultiplier {
  val io = IO(new UIntMultiplierIO(width))
  require(width > 0, "width must be positive")

  val nCols = MultiplierUtils.columnCount(width)

  var acc:   Seq[Bool] = Seq.fill(nCols)(false.B)
  var carry: Seq[Bool] = Seq.fill(nCols)(false.B)

  for (row <- 0 until width) {
    val bits = MultiplierUtils.andRow(io.src1, io.src2, row, width)
    if (row == 0) {
      acc = bits
      carry = Seq.fill(nCols)(false.B)
    } else {
      val (nextAcc, nextCarry) = MultiplierUtils.compressRow(acc, carry, bits)
      acc = nextAcc
      carry = nextCarry
    }
  }

  io.output := MultiplierUtils.finalAdd(acc, carry)
}
