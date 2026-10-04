package harith.uint

import chisel3._
import chisel3.util._

/**
  * A structural ripple carry adder.
  *
  * This is the most resource efficient combinational adder, at the cost of an O(width) critical
  * path. On FPGAs the inferred [[UIntMacroAdder]] is usually preferable.
  *
  * @param width The width of the operands
  */
class UIntRippleAdder(val width: Int) extends Module with UIntAdder {
  val io = IO(new UIntAdderIO(width))

  val sums    = Wire(Vec(width, Bool()))
  val carries = Wire(Vec(width + 1, Bool()))
  carries(0) := io.carry

  for (i <- 0 until width) {
    val (sum, carryOut) = AdderUtils.fullAdder(io.src1(i), io.src2(i), carries(i))
    sums(i)        := sum
    carries(i + 1) := carryOut
  }

  io.output := Cat(carries(width), sums.asUInt)
}
