package harith.uint

import chisel3._
import chisel3.util._

/**
  * An iterative SRT divider.
  *
  * Each iteration produces a redundant signed quotient digit so that no full remainder compare is
  * needed, and the digits are accumulated into a signed quotient that is corrected once at the end.
  * Radix 4 halves the number of iterations at the cost of a wider selection. The quotient and
  * remainder are still register normalized by the RISC-V divide-by-zero handling.
  *
  * @param width The width of the operands
  * @param radix The quotient digit radix, either 2 or 4
  */
class UIntSrtDivider(val width: Int, val radix: Int) extends UIntDivider {
  val io = IO(new UIntDividerIO(width))
  require(width > 0, "width must be positive")
  require(radix == 2 || radix == 4, "radix must be 2 or 4")

  private val bitsPer   = if (radix == 2) 1 else 2
  private val shiftBits = if (radix == 2) 1 else 2
  private val steps     = (width + bitsPer - 1) / bitsPer
  private val total     = steps * bitsPer
  private val remWidth  = width + 4
  private val quotWidth = width + 2
  private val cmpWidth  = remWidth + shiftBits + 2

  override def latency: Int = steps

  val busy  = RegInit(false.B)
  val valid = RegInit(false.B)
  val count = RegInit(0.U(DividerUtils.counterWidth(steps).W))

  val divisorReg  = Reg(UInt(width.W))
  val dividendReg = Reg(UInt(width.W))
  val zeroDiv     = Reg(Bool())
  val quotient    = Reg(UInt(width.W))
  val remainder   = Reg(UInt(width.W))

  val shiftReg    = Reg(UInt(total.W))
  val rem         = Reg(SInt(remWidth.W))
  val quotientAcc = Reg(SInt(quotWidth.W))

  val take     = shiftReg(total - 1, total - bitsPer)
  val w        = (rem << shiftBits) + take.pad(remWidth + shiftBits).asSInt
  val w2       = (w << 1)(cmpWidth - 1, 0).asSInt
  val divisorS = divisorReg.pad(cmpWidth).asSInt
  val divisor3 = ((divisorS << 1) + divisorS)(cmpWidth - 1, 0).asSInt

  val digit = Mux(
    zeroDiv,
    0.S(3.W),
    if (radix == 2) {
      Mux(w2 >= divisorS, 1.S(3.W), Mux(w2 <= -divisorS, -1.S(3.W), 0.S(3.W)))
    } else {
      Mux(
        w2 >= divisor3,
        2.S(3.W),
        Mux(
          w2 >= divisorS,
          1.S(3.W),
          Mux(w2 <= -divisor3, -2.S(3.W), Mux(w2 <= -divisorS, -1.S(3.W), 0.S(3.W))),
        ),
      )
    },
  )

  val remNext      = (w - digit * divisorS)(remWidth - 1, 0).asSInt
  val quotientNext = ((quotientAcc << shiftBits) + digit)(quotWidth - 1, 0).asSInt
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
      shiftReg    := io.in.bits.dividend.pad(total)
      rem         := 0.S
      quotientAcc := 0.S
      quotient    := 0.U
      remainder   := 0.U
    }
  }.otherwise {
    rem         := remNext
    quotientAcc := quotientNext
    shiftReg    := (shiftReg << bitsPer)(total - 1, 0)
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
