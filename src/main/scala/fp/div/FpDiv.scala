package harith.fp

import chisel3._
import chisel3.util._

/**
  * The ports of a [[FpDiv]].
  *
  * @param a   The format of the dividend
  * @param b   The format of the divisor
  * @param out The format of the result
  */
class FpDivIO(a: FpFormat, b: FpFormat, out: FpFormat) extends Bundle {
  val src1   = Input(UInt(a.width.W))
  val src2   = Input(UInt(b.width.W))
  val rm     = Input(UInt(3.W))
  val output = Output(UInt(out.width.W))
  val fflags = Output(new FpFlags)
}

/**
  * The common interface of the floating-point dividers.
  *
  * Every implementation computes `src1 / src2`, rounds it to `output` using the RISC-V rounding
  * mode `rm`, and reports the IEEE-754 exceptions. The operand and result formats are independent
  * parameters.
  */
trait FpDiv extends Module {
  val io: FpDivIO

  /**
    * Result latency in clock cycles, where 0 marks a purely combinational divider.
    */
  def latency: Int = 0
}
