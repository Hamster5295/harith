package harith.uint

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis

/**
  * An iterative radix-4 divider.
  *
  * Two dividend bits are consumed per iteration and each iteration produces one quotient digit
  * from `{0, 1, 2, 3}` by comparing the shifted partial remainder against the divisor and its
  * multiples `2 * divisor` and `3 * divisor`. The digit is selected exactly, so the partial
  * remainder always stays below the divisor and no restore or final correction step is needed.
  * This halves the number of iterations of a radix-2 divider.
  *
  * fpga@32bit-16cyc: delay[i/o/max] = 2.467ns/1.528ns/4.338ns  area = 328luts + 202ff
  *
  * 55nm@32bit-16cyc: delay[i/o/max] = 5.8867ns/0.8927ns/5.8867ns  area = 3832.64um²
  *
  * @param width The width of the operands
  */
class UIntSrt4Div(val width: Int) extends UIntDiv {
  val io = IO(new UIntDivIO(width))
  require(width > 0, "width must be positive")

  private val steps    = (width + 1) / 2
  private val total    = steps * 2
  private val remWidth = width + 2

  override def latency: Int = steps

  val busy  = RegInit(false.B)
  val valid = RegInit(false.B)
  val count = RegInit(0.U(DivUtils.counterWidth(steps).W))

  val divisorReg  = Reg(UInt(width.W))
  val dividendReg = Reg(UInt(width.W))
  val zeroDiv     = Reg(Bool())
  val quotient    = Reg(UInt(width.W))
  val remainder   = Reg(UInt(width.W))

  val shiftReg = Reg(UInt(total.W))
  val rem      = Reg(UInt(remWidth.W))

  val take     = shiftReg(total - 1, total - 2)
  val divisor1 = divisorReg.pad(remWidth + 2)
  val divisor2 = divisor1 << 1
  val divisor3 = divisor2 + divisor1

  val shifted = (rem << 2) + take.pad(remWidth)
  val digit   = Mux(
    shifted >= divisor3,
    3.U(2.W),
    Mux(shifted >= divisor2, 2.U(2.W), Mux(shifted >= divisor1, 1.U(2.W), 0.U(2.W))),
  )
  val remNext = Mux(
    shifted >= divisor3,
    shifted - divisor3,
    Mux(
      shifted >= divisor2,
      shifted - divisor2,
      Mux(shifted >= divisor1, shifted - divisor1, shifted),
    ),
  )
  val quotientNext = (quotient << 2)(width - 1, 0) | digit
  val last         = count === 1.U

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
      shiftReg    := io.in.bits.dividend.pad(total)
      rem         := 0.U
      quotient    := 0.U
      remainder   := 0.U
    }
  }.otherwise {
    rem      := remNext(remWidth - 1, 0)
    shiftReg := (shiftReg << 2)(total - 1, 0)
    quotient := quotientNext
    when(last) {
      busy      := false.B
      valid     := true.B
      quotient  := Mux(zeroDiv, DivUtils.allOnes(width), quotientNext)
      remainder := Mux(zeroDiv, dividendReg, remNext(width - 1, 0))
    }.otherwise {
      count := count - 1.U
    }
  }
}

object UIntSrt4Div extends App {
  ExportForAnalysis(
    new UIntSrt4Div(32),
    args,
    Array(
      "--lowering-options=mitigateVivadoArrayIndexConstPropBug,disallowLocalVariables,disallowPackedArrays",
    ),
  )
}
