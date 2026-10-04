package harith.uint

import chisel3._
import chisel3.util._

/**
  * The low level helpers shared by the iterative dividers.
  */
private[uint] object DividerUtils {

  /**
    * A `width` bit value with every bit set, used as the RISC-V divide-by-zero quotient.
    *
    * @param width The number of bits
    * @return the all ones value
    */
  def allOnes(width: Int): UInt = Fill(width, true.B).asUInt

  /**
    * The register width needed to count from a fixed number of cycles down to one.
    *
    * @param cycles The number of iterations
    * @return the counter width
    */
  def counterWidth(cycles: Int): Int = log2Ceil(cycles + 1)
}
