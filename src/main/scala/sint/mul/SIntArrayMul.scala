package harith.sint

import chisel3._
import harith.ExportForAnalysis
import harith.uint.MulUtils

/**
  * A signed carry save array multiplier using Baugh-Wooley partial products.
  *
  * The sign row and column of the partial product matrix are inverted and the correction is folded
  * into the array, then the rows are accumulated by a carry save chain. The product is two's
  * complement in `2 * width` bits.
  *
  * fpga@32bit: delay[i/o/max] = 18.743ns/19.315ns/18.743ns  area = 2731luts + 0ff
  *
  * 55nm@32bit: delay[i/o/max] = 8.1454ns/8.1454ns/8.1454ns  area = 16828.56um²
  *
  * @param width The width of the operands
  */
class SIntArrayMul(val width: Int) extends SIntMul {
  val io = IO(new SIntMulIO(width))
  require(width > 0, "width must be positive")

  private val nCols = 2 * width
  private val rows  = SIntMulUtils.baughWooleyRows(io.src1, io.src2, width)

  var acc:   Seq[Bool] = rows.head
  var carry: Seq[Bool] = Seq.fill(nCols)(false.B)

  for (row <- rows.tail) {
    val (nextAcc, nextCarry) = MulUtils.compressRow(acc, carry, row)
    acc = nextAcc
    carry = nextCarry
  }

  io.output := MulUtils.finalAdd(acc, carry).asSInt
}

object SIntArrayMul extends App {
  ExportForAnalysis(new SIntArrayMul(32), args, SIntExport.opts)
}
