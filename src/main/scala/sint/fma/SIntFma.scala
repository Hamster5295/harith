package harith.sint

import chisel3._

/**
  * The ports of a [[SIntFma]].
  *
  * @param width The width of the multiplicands
  */
class SIntFmaIO(width: Int) extends Bundle {
  val mul1   = Input(SInt(width.W))
  val mul2   = Input(SInt(width.W))
  val add    = Input(SInt((2 * width).W))
  val output = Output(SInt((2 * width + 1).W))
}

/**
  * The common interface of the signed fused multiply-adders.
  *
  * Every implementation computes `mul1 * mul2 + add` losslessly as a `2 * width + 1` bit two's
  * complement result through [[SIntFmaIO]].
  */
trait SIntFma extends Module {
  val io: SIntFmaIO

  /**
    * Result latency in clock cycles, where 0 marks a purely combinational FMA.
    */
  def latency: Int = 0
}
