package harith.fp

import chisel3._
import chisel3.util._

/**
  * The request of a unary floating-point operation.
  *
  * @param inFmt The format of the operand
  */
class FpUnaryReq(inFmt: FpFormat) extends Bundle {
  val src = UInt(inFmt.width.W)
  val rm  = UInt(3.W)
}

/**
  * The response of a unary floating-point operation.
  *
  * @param outFmt The format of the result
  */
class FpUnaryResp(outFmt: FpFormat) extends Bundle {
  val output = UInt(outFmt.width.W)
  val fflags = new FpFlags
}

/**
  * The ports of a unary floating-point operation.
  *
  * The request channel is decoupled, while the response channel is valid only, so a result is held
  * until the next request is accepted. `flush` aborts an in-flight operation with the highest
  * priority, dropping any pending result.
  *
  * @param inFmt  The format of the operand
  * @param outFmt The format of the result
  */
class FpUnaryIO(inFmt: FpFormat, outFmt: FpFormat) extends Bundle {
  val in    = Flipped(Decoupled(new FpUnaryReq(inFmt)))
  val out   = Output(Valid(new FpUnaryResp(outFmt)))
  val flush = Input(Bool())
}

/**
  * The common interface of the unary floating-point operations.
  */
trait FpUnary extends Module {
  val io: FpUnaryIO

  /**
    * The fixed number of cycles from an accepted request to a valid response.
    */
  def latency: Int
}

/** The common interface of the floating-point reciprocal units. */
trait FpReciprocal extends FpUnary

/** The common interface of the floating-point square root units. */
trait FpSqrt extends FpUnary

/** The common interface of the floating-point reciprocal square root units. */
trait FpRsqrt extends FpUnary
