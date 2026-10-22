package harith.sint

import chisel3._

/**
  * The ports of a [[SIntMul]].
  *
  * @param width The width of the operands
  */
class SIntMulIO(width: Int) extends Bundle {
  val src1   = Input(SInt(width.W))
  val src2   = Input(SInt(width.W))
  val output = Output(SInt((2 * width).W))
}

/**
  * The common interface of the signed multipliers.
  *
  * Every implementation computes the two's complement product of `src1` and `src2` as a
  * `2 * width` bit result through [[SIntMulIO]].
  */
trait SIntMul extends Module {
  val io: SIntMulIO

  /**
    * Result latency in clock cycles, where 0 marks a purely combinational multiplier.
    */
  def latency: Int = 0
}
