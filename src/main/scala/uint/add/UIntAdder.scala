package harith.uint

import chisel3._
import chisel3.util._

class UIntAdderIO(width: Int) extends Bundle {
  val src1   = Input(UInt(width.W))
  val src2   = Input(UInt(width.W))
  val carry  = Input(Bool())
  val output = Output(UInt((width + 1).W))
}

trait UIntAdder {
  val io: UIntAdderIO
}
