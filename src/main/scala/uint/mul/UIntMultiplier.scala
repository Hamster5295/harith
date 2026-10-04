package harith.uint

import chisel3._
import chisel3.util._

class UIntMultiplierIO(width: Int) extends Bundle {
  val src1   = Input(UInt(width.W))
  val src2   = Input(UInt(width.W))
  val output = Output(UInt((width * 2).W))
}

trait UIntMultiplier {
  val io: UIntMultiplierIO

  /** Result latency in clock cycles, where 0 marks a purely combinational adder.
    *
    * Downstream logic and testbenches use this to align the pipeline. Pipelined adders are fully
    * pipelined with a fixed latency and accept a new operand pair every cycle.  
    * 
    */
  def latency: Int = 0
}
