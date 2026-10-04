package harith.uint

import chisel3._
import chisel3.util._

/**
  * An iterative radix-2 SRT divider.
  *
  * Each iteration produces one redundant signed quotient digit from `{-1, 0, 1}` by comparing twice
  * the partial remainder against `+-divisor`, and the digits are accumulated into a signed quotient
  * that is corrected once at the end. It needs no restore step and finishes in one cycle per operand
  * bit.
  *
  * @param width The width of the operands
  */
class UIntSrt2Divider(val width: Int) extends UIntDivider {
  val io = IO(new UIntDividerIO(width))
  require(width > 0, "width must be positive")

  private val steps     = width
  private val remWidth  = width + 4
  private val quotWidth = width + 2
  private val cmpWidth  = remWidth + 2

  override def latency: Int = steps

  val busy  = RegInit(false.B)
  val valid = RegInit(false.B)
  val count = RegInit(0.U(DividerUtils.counterWidth(steps).W))

  val divisorReg  = Reg(UInt(width.W))
  val dividendReg = Reg(UInt(width.W))
  val zeroDiv     = Reg(Bool())
  val quotient    = Reg(UInt(width.W))
  val remainder   = Reg(UInt(width.W))

  val shiftReg    = Reg(UInt(width.W))
  val rem         = Reg(SInt(remWidth.W))
  val quotientAcc = Reg(SInt(quotWidth.W))

  val take     = shiftReg(width - 1).asUInt.pad(remWidth + 1).asSInt
  val w        = (rem << 1) + take
  val w2       = (w << 1)(cmpWidth - 1, 0).asSInt
  val divisorS = divisorReg.pad(cmpWidth).asSInt

  val digit = Mux(
    zeroDiv,
    0.S(2.W),
    Mux(w2 >= divisorS, 1.S(2.W), Mux(w2 <= -divisorS, -1.S(2.W), 0.S(2.W))),
  )

  val remNext      = (w - digit * divisorS)(remWidth - 1, 0).asSInt
  val quotientNext = ((quotientAcc << 1) + digit)(quotWidth - 1, 0).asSInt
  val last         = count === 1.U

  val remCorrected      = Mux(remNext(remWidth - 1), remNext + divisorS, remNext)
  val quotientCorrected = Mux(remNext(remWidth - 1), quotientNext - 1.S, quotientNext)
  val quotientValue     = quotientCorrected.asUInt.apply(width - 1, 0)
  val remainderValue    = remCorrected.asUInt.apply(width - 1, 0)

  io.in.ready              := !busy && !io.flush
  io.out.valid             := valid
  io.out.bits.quotient     := quotient
  io.out.bits.remainder    := remainder
  io.out.bits.divideByZero := zeroDiv

  when(io.flush) {
    busy  := false.B
    valid := false.B
  }.elsewhen(!busy) {
    when(io.in.fire) {
      busy        := true.B
      valid       := false.B
      count       := steps.U
      divisorReg  := io.in.bits.divisor
      dividendReg := io.in.bits.dividend
      zeroDiv     := io.in.bits.divisor === 0.U
      shiftReg    := io.in.bits.dividend
      rem         := 0.S
      quotientAcc := 0.S
      quotient    := 0.U
      remainder   := 0.U
    }
  }.otherwise {
    rem         := remNext
    quotientAcc := quotientNext
    shiftReg    := shiftReg << 1
    when(last) {
      busy      := false.B
      valid     := true.B
      quotient  := Mux(zeroDiv, DividerUtils.allOnes(width), quotientValue)
      remainder := Mux(zeroDiv, dividendReg, remainderValue)
    }.otherwise {
      count := count - 1.U
    }
  }
}
