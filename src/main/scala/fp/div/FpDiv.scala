package harith.fp

import chisel3._
import chisel3.util._

/**
  * The request of a [[FpDiv]].
  *
  * @param a The format of the dividend
  * @param b The format of the divisor
  */
class FpDivReq(a: FpFormat, b: FpFormat) extends Bundle {
  val src1 = UInt(a.width.W)
  val src2 = UInt(b.width.W)
  val rm   = UInt(3.W)
}

/**
  * The response of a [[FpDiv]].
  *
  * @param out The format of the result
  */
class FpDivResp(out: FpFormat) extends Bundle {
  val output = UInt(out.width.W)
  val fflags = new FpFlags
}

/**
  * The ports of a [[FpDiv]].
  *
  * The request channel is decoupled, while the response channel is valid only, so a result is held
  * until the next request is accepted. `flush` aborts an in-flight division with the highest
  * priority, dropping any pending result.
  *
  * @param a   The format of the dividend
  * @param b   The format of the divisor
  * @param out The format of the result
  */
class FpDivIO(a: FpFormat, b: FpFormat, res: FpFormat) extends Bundle {
  val in    = Flipped(Decoupled(new FpDivReq(a, b)))
  val out   = Output(Valid(new FpDivResp(res)))
  val flush = Input(Bool())
}

/**
  * The common interface of the floating-point dividers.
  *
  * Every implementation computes `src1 / src2` with a single rounding to `output` using the RISC-V
  * rounding mode carried by the request, and reports the IEEE-754 exceptions.
  */
trait FpDiv extends Module {
  val io: FpDivIO

  /**
    * The fixed number of cycles from an accepted request to a valid response.
    */
  def latency: Int
}
