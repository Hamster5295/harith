package harith.uint

import chisel3._
import chisel3.util._

/**
  * The unsigned multiplier implemented with the `*` operator.
  *
  * FPGA will likely implement it as an inner DSP.
  *
  * @param width The width of the operands
  */
class UIntMacroMultiplier(width: Int) extends Module with UIntMultiplier {
  val io = IO(new UIntMultiplierIO(width))

  io.output := io.src1 * io.src2
}
