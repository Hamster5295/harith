package harith.sint

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis
import harith.uint.UIntRestoringDiv

/**
  * A signed divider built from the unsigned restoring divider and sign correction.
  *
  * The operands are converted to magnitudes and divided by [[harith.uint.UIntRestoringDiv]]; the
  * quotient and remainder signs are then restored. The quotient is truncated toward zero and the
  * remainder takes the dividend's sign. Divide-by-zero and the `min / -1` overflow follow the
  * RISC-V semantics.
  *
  * fpga@32bit-32cyc: delay[i/o/max] = 4.487ns/3.775ns/3.204ns  area = 345luts + 235ff
  *
  * 55nm@32bit-32cyc: delay[i/o/max] = 4.9881ns/3.6132ns/4.9881ns  area = 3597.16um²
  *
  * @param width The width of the operands
  */
class SIntRestoringDiv(val width: Int) extends SIntDiv {
  val io = IO(new SIntDivIO(width))
  require(width > 0, "width must be positive")

  val core = Module(new UIntRestoringDiv(width))

  val a = io.in.bits.dividend
  val b = io.in.bits.divisor

  core.io.in.valid         := io.in.valid
  core.io.in.bits.dividend := Mux(a < 0.S, -a, a).asUInt
  core.io.in.bits.divisor  := Mux(b < 0.S, -b, b).asUInt
  core.io.flush            := io.flush
  io.in.ready              := core.io.in.ready

  // The unsigned core keeps the result of the last accepted request stable, so the operand signs
  // only need to be captured once per request.
  val signQ    = Reg(Bool())
  val signR    = Reg(Bool())
  val dividend = Reg(SInt(width.W))

  when(io.in.fire) {
    signQ    := (a < 0.S) ^ (b < 0.S)
    signR    := a < 0.S
    dividend := a
  }

  val divideByZero = core.io.out.bits.divideByZero

  io.out.valid         := core.io.out.valid
  io.out.bits.quotient := Mux(
    divideByZero,
    Fill(width, true.B).asSInt,
    applySign(core.io.out.bits.quotient, signQ),
  )
  io.out.bits.remainder := Mux(
    divideByZero,
    dividend,
    applySign(core.io.out.bits.remainder, signR),
  )
  io.out.bits.divideByZero := divideByZero

  override def latency: Int = core.latency

  /**
    * Reinterpret a magnitude as a two's complement value with the requested sign.
    *
    * @param mag The non-negative magnitude
    * @param neg Whether the result should be negated
    * @return the signed value
    */
  private def applySign(mag: UInt, neg: Bool): SInt = {
    val wide = mag.pad(width + 1).asSInt
    Mux(neg, -wide, wide)(width - 1, 0).asSInt
  }
}

object SIntRestoringDiv extends App {
  ExportForAnalysis(new SIntRestoringDiv(32), args, SIntExport.opts)
}
