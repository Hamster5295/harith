package harith.uint

import chisel3._
import chisel3.util._

/**
  * The low level helpers shared by the structural adders.
  */
private[uint] object AddUtils {

  /**
    * A single full adder.
    *
    * @param a       The first operand bit
    * @param b       The second operand bit
    * @param carryIn The carry into this bit
    * @return the sum bit and the carry out
    */
  def fullAdd(a: Bool, b: Bool, carryIn: Bool): (Bool, Bool) = {
    val propagate = a ^ b
    (propagate ^ carryIn, (a & b) | (propagate & carryIn))
  }
}
