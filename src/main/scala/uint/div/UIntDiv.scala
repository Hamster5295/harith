package harith.uint

import chisel3._
import chisel3.util._

/**
  * The request of a [[UIntDiv]].
  *
  * @param width The width of the operands
  */
class UIntDivReq(width: Int) extends Bundle {
  val dividend = UInt(width.W)
  val divisor  = UInt(width.W)
}

/**
  * The response of a [[UIntDiv]].
  *
  * @param width The width of the operands
  */
class UIntDivResp(width: Int) extends Bundle {
  val quotient     = UInt(width.W)
  val remainder    = UInt(width.W)
  val divideByZero = Bool()
}

/**
  * The ports of a [[UIntDiv]].
  *
  * The request channel is decoupled, while the response channel is valid only, so a result is held
  * until the next request is accepted. `flush` aborts an in-flight division with the highest
  * priority, dropping any pending result.
  *
  * @param width The width of the operands
  */
class UIntDivIO(width: Int) extends Bundle {
  val in    = Flipped(Decoupled(new UIntDivReq(width)))
  val out   = Output(Valid(new UIntDivResp(width)))
  val flush = Input(Bool())
}

/**
  * The common interface of the unsigned dividers.
  *
  * Every implementation computes `dividend / divisor` and `dividend % divisor` for unsigned
  * operands with the RISC-V divide-by-zero semantics: an all ones quotient, the dividend as the
  * remainder and the [[UIntDivResp.divideByZero]] flag set.
  */
trait UIntDiv extends Module {
  val io: UIntDivIO

  /**
    * The fixed number of cycles from an accepted request to a valid response.
    */
  def latency: Int
}
