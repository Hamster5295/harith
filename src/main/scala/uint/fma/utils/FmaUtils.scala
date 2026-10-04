package harith.uint

import chisel3._
import chisel3.util._
import scala.collection.mutable

/**
  * Builds the fused partial product heaps shared by the UInt fused multiply-adders.
  */
private[uint] object FmaUtils {

  /**
    * Extend a partial product heap to the FMA output width and merge the addend bits into it.
    *
    * The addend enters the carry save reduction as one extra operand row, so the final carry
    * propagate adder is the only carry propagate adder in the FMA.
    *
    * @param partial  The partial product column heaps, `2 * width` wide
    * @param addend   The addend to merge
    * @param outWidth The FMA output width, `2 * width + 1`
    * @return the merged column heaps
    */
  def withAddend(partial: Seq[Seq[Bool]], addend: UInt, outWidth: Int): Seq[Seq[Bool]] = {
    require(outWidth >= partial.length, "the FMA output width must cover the partial products")
    val columns = Array.fill(outWidth)(mutable.ArrayBuffer.empty[Bool])
    for (c <- partial.indices) columns(c) ++= partial(c)
    for (c <- 0 until addend.getWidth) columns(c) += addend(c)
    columns.map(_.toSeq).toSeq
  }
}
