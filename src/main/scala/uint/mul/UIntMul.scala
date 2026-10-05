package harith.uint

import chisel3._
import chisel3.util._

/**
  * The ports of a [[UIntMul]].
  *
  * @param width The width of the operands
  */
class UIntMulIO(width: Int) extends Bundle {
  val src1   = Input(UInt(width.W))
  val src2   = Input(UInt(width.W))
  val output = Output(UInt((width * 2).W))
}

/**
  * The common interface of the unsigned multipliers.
  *
  * Every implementation computes `src1 * src2` as a `width * 2` bit result through
  * [[UIntMulIO]].
  */
trait UIntMul extends Module {
  val io: UIntMulIO

  /**
    * Result latency in clock cycles, where 0 marks a purely combinational multiplier.
    *
    * Downstream logic and testbenches use this to align the pipeline. Pipelined multipliers are
    * fully pipelined with a fixed latency and accept a new operand pair every cycle.
    */
  def latency: Int = 0
}
