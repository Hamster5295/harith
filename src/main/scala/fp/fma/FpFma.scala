package harith.fp

import chisel3._
import chisel3.util._

/**
  * The ports of a [[FpFma]].
  *
  * @param a   The format of the multiplicand
  * @param b   The format of the multiplier
  * @param c   The format of the addend
  * @param out The format of the result
  */
class FpFmaIO(a: FpFormat, b: FpFormat, c: FpFormat, out: FpFormat) extends Bundle {
  val src1   = Input(UInt(a.width.W))
  val src2   = Input(UInt(b.width.W))
  val add    = Input(UInt(c.width.W))
  val rm     = Input(UInt(3.W))
  val output = Output(UInt(out.width.W))
  val fflags = Output(new FpFlags)
}

/**
  * The common interface of the floating-point fused multiply-adders.
  *
  * Every implementation computes `src1 * src2 + add` with a single rounding to `output` using the
  * RISC-V rounding mode `rm`, and reports the IEEE-754 exceptions. The operand and result formats
  * are independent parameters, so mixed-precision FMAs are expressed without extra code.
  */
trait FpFma extends Module {
  val io: FpFmaIO

  /**
    * Result latency in clock cycles, where 0 marks a purely combinational FMA.
    */
  def latency: Int = 0
}
