package harith.uint

import chisel3._
import chisel3.util._
import hammer.Export

/**
  * An iterative non-restoring divider.
  *
  * The partial remainder is kept in signed form and the divisor is added or subtracted according
  * to its sign, so no restore step is needed. A final correction handles a negative remainder.
  *
  * @param width The width of the operands
  *
  * delay = 4.340, area = 140 @32bit@32cycles@fpga
  * delay = 6.4711, area = 3397.52 @32bit@32cycles@55nm
  */
class UIntNonRestoringDiv(val width: Int) extends UIntDiv {
  val io = IO(new UIntDivIO(width))
  require(width > 0, "width must be positive")

  override def latency: Int = width

  val remWidth = width + 4

  val busy  = RegInit(false.B)
  val valid = RegInit(false.B)
  val count = RegInit(0.U(DivUtils.counterWidth(width).W))

  val divisorReg  = Reg(UInt(width.W))
  val dividendReg = Reg(UInt(width.W))
  val zeroDiv     = Reg(Bool())
  val quotient    = Reg(UInt(width.W))
  val remainder   = Reg(UInt(width.W))

  val shiftReg = Reg(UInt(width.W))
  val rem      = Reg(SInt(remWidth.W))

  val divisorS     = divisorReg.pad(remWidth).asSInt
  val bit          = Cat(0.U((remWidth - 1).W), shiftReg(width - 1)).asSInt
  val w            = (rem << 1) + bit
  val partialNext  = Mux(rem < 0.S, w + divisorS, w - divisorS)
  val remNext      = partialNext(remWidth - 1, 0).asSInt
  val quotientNext = (quotient << 1)(width - 1, 0) | !partialNext(remWidth)
  val last         = count === 1.U

  val remainderCorrected = Mux(remNext(remWidth - 1), remNext + divisorS, remNext)

  io.in.ready              := !busy && !io.flush
  io.out.valid             := valid
  io.out.bits.quotient     := Mux(zeroDiv, DivUtils.allOnes(width), quotient)
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
      quotient  := quotientNext
      remainder := remainderCorrected(width - 1, 0).asUInt
    }.otherwise {
      count := count - 1.U
    }
  }
}

object UIntNonRestoringDiv extends App {
  Export(
    new UIntNonRestoringDiv(32),
    args,
    Array(
      "--lowering-options=mitigateVivadoArrayIndexConstPropBug,disallowLocalVariables,disallowPackedArrays",
    ),
  )
}
