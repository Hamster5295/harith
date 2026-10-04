package harith.uint

import chisel3._
import chisel3.util._

/**
  * The unsigned FMA implemented with the `*` and `+` operators.
  *
  * The whole expression is left to the synthesis tool, which on FPGA will likely map it into an
  * inner DSP with a built-in multiply-accumulate.
  *
  * @param width The width of the operands
  */
class UIntMacroFma(val width: Int) extends UIntFma {
  val io = IO(new UIntFmaIO(width))

  io.output := (io.mul1 * io.mul2) +& io.add
}
