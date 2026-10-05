package harith.fp

import chisel3._
import chisel3.util._

/**
  * The ports of a [[FpMul]].
  *
  * @param a   The format of the first operand
  * @param b   The format of the second operand
  * @param out The format of the result
  */
class FpMulIO(a: FpFormat, b: FpFormat, out: FpFormat) extends Bundle {
  val src1   = Input(UInt(a.width.W))
  val src2   = Input(UInt(b.width.W))
  val rm     = Input(UInt(3.W))
  val output = Output(UInt(out.width.W))
  val fflags = Output(new FpFlags)
}

/**
  * The common interface of the floating-point multipliers.
  *
  * Every implementation computes the product of `src1` and `src2`, rounds it to `output` using the
  * RISC-V rounding mode `rm`, and reports the IEEE-754 exceptions. The operand and result formats
  * are independent parameters, so mixed-precision products are expressed without extra code.
  */
trait FpMul extends Module {
  val io: FpMulIO

  /**
    * Result latency in clock cycles, where 0 marks a purely combinational multiplier.
    */
  def latency: Int = 0
}
