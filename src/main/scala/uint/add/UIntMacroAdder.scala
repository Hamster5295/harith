package harith.uint

import chisel3._
import chisel3.util._

/**
  * The unsigned adder implemented with the `+` operator.
  *
  * FPGA will likely implement it as an inner DSP or CARRY primitive.
  *
  * @param width The width of the operands
  */
class UIntMacroAdder(val width: Int) extends Module with UIntAdder {
  val io = IO(new UIntAdderIO(width))
  io.output := io.src1 +& io.src2 + io.carry
}
