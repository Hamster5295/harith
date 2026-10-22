package harith.sint

import chisel3._
import chisel3.util._

/**
  * The request of a [[SIntDiv]].
  *
  * @param width The width of the operands
  */
class SIntDivReq(width: Int) extends Bundle {
  val dividend = SInt(width.W)
  val divisor  = SInt(width.W)
}

/**
  * The response of a [[SIntDiv]].
  *
  * @param width The width of the operands
  */
class SIntDivResp(width: Int) extends Bundle {
  val quotient     = SInt(width.W)
  val remainder    = SInt(width.W)
  val divideByZero = Bool()
}

/**
  * The ports of a [[SIntDiv]].
  *
  * The request channel is decoupled, while the response channel is valid only, so a result is held
  * until the next request is accepted. `flush` aborts an in-flight division with the highest
  * priority, dropping any pending result.
  *
  * @param width The width of the operands
  */
class SIntDivIO(width: Int) extends Bundle {
  val in    = Flipped(Decoupled(new SIntDivReq(width)))
  val out   = Output(Valid(new SIntDivResp(width)))
  val flush = Input(Bool())
}

/**
  * The common interface of the signed dividers.
  *
  * Every implementation computes `dividend / divisor` and `dividend % divisor` for two's complement
  * operands, truncating the quotient toward zero, with the RISC-V divide-by-zero semantics: a
  * quotient of `-1`, the dividend as the remainder and the [[SIntDivResp.divideByZero]] flag set.
  */
trait SIntDiv extends Module {
  val io: SIntDivIO

  /**
    * The fixed number of cycles from an accepted request to a valid response.
    */
  def latency: Int
}
