package harith.uint

import chisel3._
import chisel3.util._

/**
  * The ports of a [[UIntFma]].
  *
  * @param width The width of the operands
  */
class UIntFmaIO(width: Int) extends Bundle {
  val src1   = Input(UInt(width.W))
  val src2   = Input(UInt(width.W))
  val addend = Input(UInt((2 * width).W))
  val output = Output(UInt((2 * width + 1).W))
}

/**
  * The common interface of the unsigned fused multiply-adders.
  *
  * Every implementation computes `src1 * src2 + addend` losslessly as a `2 * width + 1` bit result
  * through [[UIntFmaIO]].
  */
trait UIntFma extends Module {
  val io: UIntFmaIO

  /**
    * Result latency in clock cycles, where 0 marks a purely combinational FMA.
    *
    * Downstream logic and testbenches use this to align the pipeline. Pipelined FMAs are fully
    * pipelined with a fixed latency and accept a new operand triple every cycle.
    */
  def latency: Int = 0
}
