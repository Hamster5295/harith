package harith.uint

import chisel3._
import chisel3.util._

private[uint] object AdderUtils {

  /** A single full adder.
    *
    * @return the sum bit and the carry out
    */
  def fullAdder(a: Bool, b: Bool, carryIn: Bool): (Bool, Bool) = {
    val propagate = a ^ b
    (propagate ^ carryIn, (a & b) | (propagate & carryIn))
  }
}
