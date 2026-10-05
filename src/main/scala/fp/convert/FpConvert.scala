package harith.fp

import chisel3._
import chisel3.util._

/**
  * The ports of a [[FpConvert]].
  *
  * @param in  The input format
  * @param out The output format
  */
class FpConvertIO(in: FpFormat, out: FpFormat) extends Bundle {
  val src    = Input(UInt(in.width.W))
  val rm     = Input(UInt(3.W))
  val output = Output(UInt(out.width.W))
  val fflags = Output(new FpFlags)
}

/**
  * The common interface of the floating-point format converters.
  *
  * Every implementation re-encodes a value from one format to another, rounding with the RISC-V
  * rounding mode `rm` when the destination is narrower, and reports the IEEE-754 exceptions.
  */
trait FpConvert extends Module {
  val io: FpConvertIO

  /**
    * Result latency in clock cycles, where 0 marks a purely combinational converter.
    */
  def latency: Int = 0
}
