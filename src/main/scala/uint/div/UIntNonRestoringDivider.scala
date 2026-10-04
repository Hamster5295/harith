package harith.uint

import chisel3._
import chisel3.util._

/**
  * An iterative non-restoring divider.
  *
  * The partial remainder is kept in signed form and the divisor is added or subtracted according
  * to its sign, so no restore step is needed. A final correction handles a negative remainder.
  *
  * @param width The width of the operands
  */
class UIntNonRestoringDivider(val width: Int) extends UIntDivider {
  val io = IO(new UIntDividerIO(width))
  require(width > 0, "width must be positive")

  override def latency: Int = width

  val remWidth = width + 4

  val busy  = RegInit(false.B)
  val valid = RegInit(false.B)
  val count = RegInit(0.U(DividerUtils.counterWidth(width).W))

  val divisorReg  = Reg(UInt(width.W))
  val dividendReg = Reg(UInt(width.W))
  val zeroDiv     = Reg(Bool())
  val quotient    = Reg(UInt(width.W))
  val remainder   = Reg(UInt(width.W))

  val shiftReg = Reg(UInt(width.W))
  val rem      = Reg(SInt(remWidth.W))

  val divisorS     = divisorReg.pad(remWidth).asSInt
  val bit          = shiftReg(width - 1).asUInt.pad(remWidth).asSInt
  val w            = (rem << 1) + bit
  val partialNext  = Mux(w(remWidth), w + divisorS, w - divisorS)
  val remNext      = partialNext(remWidth - 1, 0).asSInt
  val quotientNext = (quotient << 1)(width - 1, 0) | !partialNext(remWidth)
  val last         = count === 1.U

  val remainderCorrected = Mux(remNext(remWidth - 1), remNext + divisorS, remNext)
  val quotientCorrected  = Mux(remNext(remWidth - 1), quotientNext - 1.U, quotientNext)

  io.in.ready              := !busy && !io.flush
  io.out.valid             := valid
  io.out.bits.quotient     := Mux(zeroDiv, DividerUtils.allOnes(width), quotient)
  io.out.bits.remainder    := Mux(zeroDiv, dividendReg, remainder)
  io.out.bits.divideByZero := zeroDiv

  when(io.flush) {
    busy  := false.B
    valid := false.B
  }.elsewhen(!busy) {
    when(io.in.fire) {
      busy        := true.B
      valid       := false.B
      count       := width.U
      divisorReg  := io.in.bits.divisor
      dividendReg := io.in.bits.dividend
      zeroDiv     := io.in.bits.divisor === 0.U
      shiftReg    := io.in.bits.dividend
      rem         := 0.S
      quotient    := 0.U
      remainder   := 0.U
    }
  }.otherwise {
    rem      := remNext
    shiftReg := shiftReg << 1
    quotient := quotientNext
    when(last) {
      busy      := false.B
      valid     := true.B
      quotient  := quotientCorrected
      remainder := remainderCorrected(width - 1, 0).asUInt
    }.otherwise {
      count := count - 1.U
    }
  }
}
