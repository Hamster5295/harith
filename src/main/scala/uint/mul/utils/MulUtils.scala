package harith.uint

import chisel3._
import chisel3.util._
import scala.collection.mutable

/**
  * Builds the partial product heaps and the carry save reduction shared by the UInt multipliers.
  *
  * A partial product matrix is represented as one heap of bits per output column. A reduction
  * level is a sequence of compressor counts per column; applying a level consumes three bits per
  * compressor and emits one sum in the same column and one carry in the next column. This mirrors
  * the underlying hardware so that levels can be registered for pipelining.
  */
private[uint] object MulUtils {

  /** The number of output columns of a `width x width` multiplier. */
  def columnCount(width: Int): Int = 2 * width

  /** A bit access that yields false outside the operand range. */
  private def bitOrFalse(value: UInt, index: Int, width: Int): Bool =
    if (index >= 0 && index < width) value(index) else false.B

  /**
    * The column bit heaps of the AND partial products.
    *
    * @param src1  The multiplicand
    * @param src2  The multiplier
    * @param width The width of the operands
    * @return one heap of bit expressions per output column
    */
  def andColumns(src1: UInt, src2: UInt, width: Int): Seq[Seq[Bool]] = {
    val columns = Array.fill(columnCount(width))(mutable.ArrayBuffer.empty[Bool])
    for (i <- 0 until width; j <- 0 until width) {
      columns(i + j) += (src1(j) & src2(i))
    }
    columns.map(_.toSeq).toSeq
  }

  /**
    * The column bit heaps of the modified Booth radix-4 partial products.
    *
    * The multiplier is grouped into overlapping triples and each digit selects `0`, `+-src1` or
    * `+-2 * src1`. Partially built heaps are sign extended to the full output width so the product
    * is recovered modulo `2^(2 * width)`. `extraColumns` adds sign extension columns on top of the
    * `2 * width` output, as required when the product feeds a wider fused multiply-adder.
    *
    * @param src1         The multiplicand
    * @param src2         The multiplier
    * @param width        The width of the operands
    * @param extraColumns The number of extra sign extension columns
    * @return one heap of bit expressions per output column
    */
  def boothColumns(src1: UInt, src2: UInt, width: Int, extraColumns: Int = 0): Seq[Seq[Bool]] = {
    val nCols   = columnCount(width) + extraColumns
    val columns = Array.fill(nCols)(mutable.ArrayBuffer.empty[Bool])
    val digits  = (width + 2) / 2
    for (i <- 0 until digits) {
      val lower  = bitOrFalse(src2, 2 * i - 1, width)
      val middle = bitOrFalse(src2, 2 * i, width)
      val upper  = bitOrFalse(src2, 2 * i + 1, width)

      val isZero   = (upper === middle) && (middle === lower)
      val isTwo    = !isZero && (middle === lower)
      val negative = upper && !(middle && lower)

      val magnitude = Mux(isZero, 0.U(nCols.W), Mux(isTwo, (src1 << 1).pad(nCols), src1.pad(nCols)))
      val product   = Mux(negative, ~magnitude + 1.U, magnitude)
      val shifted   = (product << (2 * i))(nCols - 1, 0)
      for (c <- 0 until nCols) columns(c) += shifted(c)
    }
    columns.map(_.toSeq).toSeq
  }

  /** The current bit count of every column. */
  def heights(columns: Seq[Seq[Bool]]): IndexedSeq[Int] = columns.map(_.length).toIndexedSeq

  /**
    * The compressor counts of every reduction level for the given style.
    *
    * The counts only depend on the column heights, so the schedule is independent of the actual
    * bits and can drive either a combinational or a pipelined reduction.
    *
    * @param initialHeights The starting column heights
    * @param style          The reduction style
    * @return one compressor count vector per level
    */
  def schedule(initialHeights: IndexedSeq[Int], style: ReductionStyle): Seq[Seq[Int]] = {
    val nCols = initialHeights.length

    val targets = style match {
      case ReductionStyle.Dadda =>
        val sequence = mutable.ArrayBuffer(2)
        while (sequence.last < initialHeights.max) sequence += (sequence.last * 3) / 2
        sequence.filter(_ < initialHeights.max).reverse.toSeq
      case ReductionStyle.Wallace => Seq.empty
    }

    val levels        = mutable.ArrayBuffer.empty[Seq[Int]]
    var currentHeight = initialHeights
    var targetIndex   = 0
    while (currentHeight.max > 2) {
      val target = style match {
        case ReductionStyle.Wallace => 2
        case ReductionStyle.Dadda   =>
          val value = if (targetIndex < targets.length) targets(targetIndex) else 2
          targetIndex += 1
          value
      }
      val compressors = Array.fill(nCols)(0)
      val next        = currentHeight.toArray
      for (c <- 0 until nCols) {
        val height = currentHeight(c)
        val count  = style match {
          case ReductionStyle.Wallace => height / 3
          case ReductionStyle.Dadda   =>
            if (height > target) math.min((height - target + 1) / 2, height / 3) else 0
        }
        compressors(c) = count
        next(c) -= 2 * count
        if (c + 1 < nCols) next(c + 1) += count
      }
      levels += compressors.toSeq
      currentHeight = next.toIndexedSeq
    }
    levels.toSeq
  }

  /**
    * Apply one reduction level to the column heaps.
    *
    * @param columns     The current column heaps
    * @param compressors The compressor count of every column for this level
    * @return the next column heaps
    */
  def applyLevel(columns: Seq[Seq[Bool]], compressors: Seq[Int]): Seq[Seq[Bool]] = {
    val nCols = columns.length
    val next  = Array.fill(nCols)(mutable.ArrayBuffer.empty[Bool])
    for (c <- 0 until nCols) {
      val bits  = columns(c)
      val count = compressors(c)
      var index = 0
      for (_ <- 0 until count) {
        val (sum, carry) = AddUtils.fullAdd(bits(index), bits(index + 1), bits(index + 2))
        index += 3
        next(c) += sum
        if (c + 1 < nCols) next(c + 1) += carry
      }
      while (index < bits.length) {
        next(c) += bits(index)
        index += 1
      }
    }
    next.map(_.toSeq).toSeq
  }

  /**
    * Reduce a column heap matrix until every column holds at most two bits.
    *
    * @param columns The column heaps
    * @param style   The reduction style
    * @return the fully reduced column heaps
    */
  def reduce(columns: Seq[Seq[Bool]], style: ReductionStyle): Seq[Seq[Bool]] = {
    var result = columns
    schedule(heights(result), style).foreach(level => result = applyLevel(result, level))
    result
  }

  /**
    * Pack a fully reduced matrix into the two rows whose sum is the product.
    *
    * @param columns The reduced column heaps
    * @return the two addend rows
    */
  def toRows(columns: Seq[Seq[Bool]]): (UInt, UInt) = {
    require(columns.forall(_.length <= 2), "the reduction must leave at most two bits per column")
    val lower = columns.indices.map(c => if (columns(c).nonEmpty) columns(c)(0) else false.B)
    val upper = columns.indices.map(c => if (columns(c).length > 1) columns(c)(1) else false.B)
    (VecInit(lower).asUInt, VecInit(upper).asUInt)
  }

  /**
    * The bits of one row of the AND partial product matrix.
    *
    * @param src1  The multiplicand
    * @param src2  The multiplier
    * @param row   The multiplier bit selecting this row
    * @param width The width of the operands
    * @return the row bits, aligned to the output columns
    */
  def andRow(src1: UInt, src2: UInt, row: Int, width: Int): Seq[Bool] = {
    val bits          = Array.fill(columnCount(width))(false.B)
    val multiplierBit = src2(row)
    for (j <- 0 until width) bits(row + j) = src1(j) & multiplierBit
    bits.toSeq
  }

  /**
    * Add one partial product row to a carry save accumulator.
    *
    * @param acc   The accumulator sum bits
    * @param carry The accumulator carry bits
    * @param row   The partial product row bits
    * @return the updated sum and carry bits
    */
  def compressRow(acc: Seq[Bool], carry: Seq[Bool], row: Seq[Bool]): (Seq[Bool], Seq[Bool]) = {
    val nCols     = acc.length
    val nextSum   = Array.fill(nCols)(false.B)
    val nextCarry = Array.fill(nCols)(false.B)
    for (c <- 0 until nCols) {
      val (sum, carryOut) = AddUtils.fullAdd(acc(c), carry(c), row(c))
      nextSum(c) = sum
      if (c + 1 < nCols) nextCarry(c + 1) = carryOut
    }
    (nextSum.toSeq, nextCarry.toSeq)
  }

  /**
    * Add the two carry save rows with a ripple carry chain.
    *
    * @param acc   The accumulator sum bits
    * @param carry The accumulator carry bits
    * @return the sum, truncated to the output width
    */
  def finalAdd(acc: Seq[Bool], carry: Seq[Bool]): UInt = {
    val nCols      = acc.length
    val sum        = Wire(Vec(nCols, Bool()))
    val carryChain = Wire(Vec(nCols + 1, Bool()))
    carryChain(0) := false.B
    for (c <- 0 until nCols) {
      val (bit, carryOut) = AddUtils.fullAdd(acc(c), carry(c), carryChain(c))
      sum(c)            := bit
      carryChain(c + 1) := carryOut
    }
    sum.asUInt
  }

  /**
    * Split `values` into `groups` consecutive, as evenly sized chunks as possible.
    *
    * @param values The sequence to split
    * @param groups The number of chunks to produce, which must be positive
    * @return the consecutive chunks
    */
  def partition[A](values: Seq[A], groups: Int): Seq[Seq[A]] = {
    require(groups > 0, "groups must be positive")
    val base    = values.length / groups
    val extra   = values.length % groups
    var current = 0
    (0 until groups).map { group =>
      val size  = base + (if (group < extra) 1 else 0)
      val chunk = values.slice(current, current + size)
      current += size
      chunk
    }
  }
}
