package harith.uint

import chisel3._
import chisel3.util._

/**
  * An iterative restoring divider.
  *
  * The remainder is shifted in one dividend bit per cycle and the divisor is subtracted; when the
  * subtraction underflows the remainder is restored and the quotient bit is zero. It is the
  * smallest divider at the cost of one cycle per operand bit.
  *
  * @param width The width of the operands
  */
class UIntRestoringDivider(val width: Int) extends UIntDivider {
  val io = IO(new UIntDividerIO(width))
  require(width > 0, "width must be positive")

  override def latency: Int = width

  val busy  = RegInit(false.B)
  val valid = RegInit(false.B)
  val count = RegInit(0.U(DividerUtils.counterWidth(width).W))

  val divisorReg  = Reg(UInt(width.W))
  val dividendReg = Reg(UInt(width.W))
  val zeroDiv     = Reg(Bool())
  val quotient    = Reg(UInt(width.W))
  val remainder   = Reg(UInt(width.W))

  val shiftReg = Reg(UInt(width.W))
  val partial  = Reg(UInt((width + 1).W))

  val shifted      = Cat(partial(width - 1, 0), shiftReg(width - 1))
  val diff         = shifted - divisorReg.pad(width + 1)
  val noBorrow     = !diff(width)
  val partialNext  = Mux(noBorrow, diff, shifted)
  val quotientNext = (quotient << 1)(width - 1, 0) | noBorrow
  val last         = count === 1.U

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
      partial     := 0.U
      quotient    := 0.U
      remainder   := 0.U
    }
  }.otherwise {
    partial  := partialNext
    shiftReg := shiftReg << 1
    quotient := quotientNext
    when(last) {
      busy      := false.B
      valid     := true.B
      quotient  := quotientNext
      remainder := partialNext(width - 1, 0)
    }.otherwise {
      count := count - 1.U
    }
  }
}
