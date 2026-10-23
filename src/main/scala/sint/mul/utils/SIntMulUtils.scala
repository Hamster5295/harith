package harith.sint

import chisel3._
import chisel3.util._
import harith.uint.MulUtils

/**
  * The signed partial product generators shared by the signed multipliers.
  *
  * The bits are produced as column heaps (one heap per output column) so the unsigned carry save
  * reduction helpers can be reused.
  */
private[sint] object SIntMulUtils {

  /**
    * The Baugh-Wooley partial product columns of a signed product.
    *
    * The sign row and column are inverted and the correction `2^width + 2^(2 * width - 1)` is
    * folded into the heap.
    *
    * @param a     The multiplicand
    * @param b     The multiplier
    * @param width The operand width
    * @return one heap of bit expressions per output column
    */
  def baughWooleyColumns(a: SInt, b: SInt, width: Int): Seq[Seq[Bool]] = {
    val nCols = MulUtils.columnCount(width)
    val cols  = Array.fill(nCols)(scala.collection.mutable.ArrayBuffer.empty[Bool])
    for (i <- 0 until width; j <- 0 until width) {
      val bit =
        if (i == width - 1 && j == width - 1) a(i) & b(j)
        else if (i == width - 1 || j == width - 1) ~(a(i) & b(j))
        else a(i) & b(j)
      cols(i + j) += bit
    }
    if (width > 1) {
      cols(width) += true.B
      cols(2 * width - 1) += true.B
    }
    cols.map(_.toSeq).toSeq
  }

  /**
    * The Baugh-Wooley partial product rows plus the correction row, for the carry save array.
    *
    * @param a     The multiplicand
    * @param b     The multiplier
    * @param width The operand width
    * @return one row of bit expressions per partial product, with the correction row last
    */
  def baughWooleyRows(a: SInt, b: SInt, width: Int): Seq[Seq[Bool]] = {
    val nCols = MulUtils.columnCount(width)
    val rows  = (0 until width).map { i =>
      val row = Array.fill(nCols)(false.B)
      for (j <- 0 until width) {
        val bit =
          if (i == width - 1 && j == width - 1) a(i) & b(j)
          else if (i == width - 1 || j == width - 1) ~(a(i) & b(j))
          else a(i) & b(j)
        row(i + j) = bit
      }
      row.toSeq
    }
    if (width == 1) rows
    else {
      val correction = Array.fill(nCols)(false.B)
      correction(width) = true.B
      correction(2 * width - 1) = true.B
      rows :+ correction.toSeq
    }
  }

  /**
    * The signed modified Booth radix-4 partial product columns.
    *
    * Both operands are sign extended, and the multiplier's overlapping triples are sign extended at
    * the top, so the partial products are two's complement slices of `+-a` and `+-2a`.
    *
    * @param a            The multiplicand
    * @param b            The multiplier
    * @param width        The operand width
    * @param extraColumns The number of extra sign extension columns
    * @return one heap of bit expressions per output column
    */
  def boothColumns(a: SInt, b: SInt, width: Int, extraColumns: Int = 0): Seq[Seq[Bool]] = {
    val nCols    = MulUtils.columnCount(width) + extraColumns
    val cols     = Array.fill(nCols)(scala.collection.mutable.ArrayBuffer.empty[Bool])
    val digits   = (width + 2) / 2
    val extended = a.pad(nCols).asUInt

    def bit(index: Int): Bool =
      if (index < 0) false.B
      else if (index < width) b(index)
      else b(width - 1)

    for (i <- 0 until digits) {
      val lower  = bit(2 * i - 1)
      val middle = bit(2 * i)
      val upper  = bit(2 * i + 1)

      val isZero   = (upper === middle) && (middle === lower)
      val isTwo    = !isZero && (middle === lower)
      val negative = upper && !(middle && lower)

      val magnitude = Mux(
        isZero,
        0.U(nCols.W),
        Mux(isTwo, (extended << 1)(nCols - 1, 0), extended),
      )
      val product = Mux(negative, ~magnitude + 1.U, magnitude)
      val shifted = (product << (2 * i))(nCols - 1, 0)
      for (c <- 0 until nCols) cols(c) += shifted(c)
    }
    cols.map(_.toSeq).toSeq
  }

  /** One row of the Baugh-Wooley partial product matrix. */
  def baughWooleyRow(a: SInt, b: SInt, row: Int, width: Int): Seq[Bool] = {
    val nCols = MulUtils.columnCount(width)
    val bits  = Array.fill(nCols)(false.B)
    for (j <- 0 until width) {
      val bit =
        if (row == width - 1 && j == width - 1) a(row) & b(j)
        else if (row == width - 1 || j == width - 1) ~(a(row) & b(j))
        else a(row) & b(j)
      bits(row + j) = bit
    }
    bits.toSeq
  }

  /** The Baugh-Wooley correction row. It is zero for a width of one. */
  def correctionRow(width: Int): Seq[Bool] = {
    val nCols = MulUtils.columnCount(width)
    val bits  = Array.fill(nCols)(false.B)
    if (width > 1) {
      bits(width) = true.B
      bits(2 * width - 1) = true.B
    }
    bits.toSeq
  }

  /** Merge a signed addend into a `2 * width` column heap. */
  def withAddend(columns: Seq[Seq[Bool]], addend: SInt, width: Int): Seq[Seq[Bool]] = {
    val out = Array.fill(columns.length)(scala.collection.mutable.ArrayBuffer.empty[Bool])
    for (c <- columns.indices) out(c) ++= columns(c)
    for (c <- columns.indices) out(c) += addend(c)
    out.map(_.toSeq).toSeq
  }

  /**
    * The sign bit of the signed product, used to build the extended FMA result.
    *
    * The sign of a nonzero product is the xor of the operand signs; a zero product is zero.
    */
  def productSign(a: SInt, b: SInt, width: Int): Bool =
    (a(width - 1) ^ b(width - 1)) && a.asUInt.orR && b.asUInt.orR

  /** Register `x` through `stages` cycles. */
  def pipe[T <: Data](x: T, stages: Int): T =
    if (stages <= 0) x else pipe(RegNext(x), stages - 1)
}
