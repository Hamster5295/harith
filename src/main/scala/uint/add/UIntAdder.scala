package harith.uint

import chisel3._
import chisel3.util._

/**
  * The ports of a [[UIntAdder]].
  *
  * @param width The width of the operands
  */
class UIntAdderIO(width: Int) extends Bundle {
  val src1   = Input(UInt(width.W))
  val src2   = Input(UInt(width.W))
  val carry  = Input(Bool())
  val output = Output(UInt((width + 1).W))
}

/**
  * The common interface of the unsigned adders.
  *
  * Every implementation computes `src1 + src2 + carry` as a `width + 1` bit result through
  * [[UIntAdderIO]].
  */
trait UIntAdder {
  val io: UIntAdderIO

  /**
    * Result latency in clock cycles, where 0 marks a purely combinational adder.
    *
    * Downstream logic and testbenches use this to align the pipeline. Pipelined adders are fully
    * pipelined with a fixed latency and accept a new operand pair every cycle.
    */
  def latency: Int = 0
}
