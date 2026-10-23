package harith.fp

import chisel3._
import chisel3.util._

/**
  * A single pipeline register layer with a valid bit.
  *
  * `flush` clears the valid bit with the highest priority, dropping any in-flight value.
  *
  * @param gen The payload type
  */
private[fp] class FpPipeStage[T <: Data](gen: T) extends Module {
  val io = IO(new Bundle {
    val in       = Input(gen)
    val out      = Output(gen)
    val inValid  = Input(Bool())
    val outValid = Output(Bool())
    val flush    = Input(Bool())
  })

  val data  = Reg(gen)
  val valid = RegInit(false.B)

  data        := io.in
  valid       := Mux(io.flush, false.B, io.inValid)
  io.out      := data
  io.outValid := valid
}

/** The pipeline payload of the Newton-Raphson reciprocal. */
private[fp] class FpNewtonReciprocalPayload(W: Int) extends Bundle {
  val x    = UInt(W.W)
  val y    = UInt(W.W)
  val t    = UInt(W.W)
  val sign = Bool()
  val exp  = SInt(FpUtils.EW.W)
  val rm   = UInt(3.W)
  val nan  = Bool()
  val inf  = Bool()
  val zero = Bool()
}

/** The pipeline payload of the Newton-Raphson reciprocal square root. */
private[fp] class FpNewtonRsqrtPayload(W: Int) extends Bundle {
  val x    = UInt(W.W)
  val z    = UInt(W.W)
  val t1   = UInt(W.W)
  val t2   = UInt(W.W)
  val sign = Bool()
  val exp  = SInt(FpUtils.EW.W)
  val rm   = UInt(3.W)
  val nan  = Bool()
  val inf  = Bool()
  val zero = Bool()
  val neg  = Bool()
}
