package harith.fp

import chisel3._
import chisel3.util._
import hammer.Export

/**
  * An iterative restoring floating-point divider.
  *
  * The significands are normalized to `NW` bits and the quotient is built one bit per cycle,
  * restoring the remainder when the subtraction underflows. The quotient carries `manWidth + 3`
  * extra fractional bits and a sticky from the nonzero remainder, so the final rounding is exact.
  * NaN is canonical, per RISC-V.
  *
  * fpga@fp32-50cyc: delay = 16.596ns  area = 1268luts + 297ff
  *
  * 55nm@fp32-50cyc: delay = 6.2552ns  area = 5463.64um²
  *
  * @param aFmt   The format of the dividend
  * @param bFmt   The format of the divisor
  * @param outFmt The format of the result
  * @param policy The numeric policy
  */
class FpRestoringDiv(
    val aFmt:   FpFormat,
    val bFmt:   FpFormat,
    val outFmt: FpFormat,
    policy:     FpPolicy = FpPolicy(),
) extends FpDiv {
  private val NW    = math.max(math.max(aFmt.manWidth, bFmt.manWidth), outFmt.manWidth) + 1
  private val extra = outFmt.manWidth + 3
  private val steps = NW + extra

  override def latency: Int = steps

  val io = IO(new FpDivIO(aFmt, bFmt, outFmt))

  val busy  = RegInit(false.B)
  val valid = RegInit(false.B)
  val count = RegInit(0.U(log2Ceil(steps + 1).W))

  val signReg = Reg(Bool())
  val nanReg  = Reg(Bool())
  val dzReg   = Reg(Bool())
  val infAReg = Reg(Bool())
  val infBReg = Reg(Bool())
  val expReg  = Reg(SInt(FpUtils.EW.W))
  val rmReg   = Reg(UInt(3.W))

  val divisorReg = Reg(UInt(NW.W))
  val shiftReg   = Reg(UInt((NW + extra).W))
  val remReg     = Reg(UInt(NW.W))
  val quoReg     = Reg(UInt((NW + extra).W))

  val outReg   = Reg(UInt(outFmt.width.W))
  val flagsReg = Reg(new FpFlags)

  // request-time decode
  val a = FpUtils.decode(io.in.bits.src1, aFmt)
  val b = FpUtils.decode(io.in.bits.src2, bFmt)

  val aZero = if (policy.daz) a.isZero || a.isSubnormal else a.isZero
  val bZero = if (policy.daz) b.isZero || b.isSubnormal else b.isZero

  val sign   = a.sign ^ b.sign
  val shiftA = (NW - 1).U - FpUtils.msbIndex(a.sig)
  val shiftB = (NW - 1).U - FpUtils.msbIndex(b.sig)
  val sigAn  = (a.sig << shiftA)(NW - 1, 0)
  val sigBn  = (b.sig << shiftB)(NW - 1, 0)
  val expAn  = a.exp - aFmt.manWidth.S(FpUtils.EW.W) - shiftA.pad(FpUtils.EW).asSInt
  val expBn  = b.exp - bFmt.manWidth.S(FpUtils.EW.W) - shiftB.pad(FpUtils.EW).asSInt
  val expQ   = expAn - expBn - extra.S(FpUtils.EW.W)

  val nanCase = a.isNaN || b.isNaN || (a.isInf && b.isInf) || (aZero && bZero)
  val dzCase  = !nanCase && bZero && !aZero
  val infCase = !nanCase && !dzCase && (a.isInf || b.isInf)

  // iterative step
  val shifted   = Cat(remReg, shiftReg(NW + extra - 1))
  val denPad    = divisorReg.pad(NW + 1)
  val ge        = shifted >= denPad
  val remNext   = Mux(ge, shifted - denPad, shifted)(NW - 1, 0)
  val quoNext   = Cat(quoReg(NW + extra - 2, 0), ge)
  val shiftNext = (shiftReg << 1)(NW + extra - 1, 0)
  val last      = count === 1.U

  val sticky  = remNext =/= 0.U
  val rounded = FpUtils.roundPack(signReg, quoNext, expReg, outFmt, rmReg, policy, sticky)

  io.in.ready        := !busy && !io.flush
  io.out.valid       := valid
  io.out.bits.output := outReg
  io.out.bits.fflags := flagsReg

  when(io.flush) {
    busy  := false.B
    valid := false.B
  }.elsewhen(!busy) {
    when(io.in.fire) {
      busy       := true.B
      valid      := false.B
      count      := steps.U
      signReg    := sign
      nanReg     := nanCase
      dzReg      := dzCase
      infAReg    := infCase && a.isInf
      infBReg    := infCase && !a.isInf
      expReg     := expQ
      rmReg      := io.in.bits.rm
      divisorReg := sigBn
      shiftReg   := sigAn << extra
      remReg     := 0.U
      quoReg     := 0.U
    }
  }.otherwise {
    remReg   := remNext
    quoReg   := quoNext
    shiftReg := shiftNext
    when(last) {
      busy   := false.B
      valid  := true.B
      outReg := Mux(
        nanReg,
        outFmt.canonicalNaN,
        Mux(
          dzReg || infAReg,
          Cat(signReg, outFmt.infinityMag),
          Mux(infBReg, outFmt.zero(signReg), rounded.bits),
        ),
      )
      flagsReg.nx := Mux(nanReg || dzReg || infAReg || infBReg, false.B, rounded.nx)
      flagsReg.uf := Mux(nanReg || dzReg || infAReg || infBReg, false.B, rounded.uf)
      flagsReg.of := Mux(nanReg || dzReg || infAReg || infBReg, false.B, rounded.of)
      flagsReg.dz := dzReg
      flagsReg.nv := nanReg
    }.otherwise {
      count := count - 1.U
    }
  }
}

object FpRestoringDiv extends App {
  Export(new FpRestoringDiv(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32), args, FpExport.opts)
}

/**
  * A float32 restoring divider.
  */
class Fp32Div extends FpRestoringDiv(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32)

/**
  * A float64 restoring divider.
  */
class Fp64Div extends FpRestoringDiv(FpFormat.Fp64, FpFormat.Fp64, FpFormat.Fp64)

/**
  * A float16 by float16 to float32 restoring divider.
  */
class Fp16Fp32Div extends FpRestoringDiv(FpFormat.Fp16, FpFormat.Fp16, FpFormat.Fp32)

/**
  * A bfloat16 by bfloat16 to float32 restoring divider.
  */
class FpBf16Fp32Div extends FpRestoringDiv(FpFormat.Bf16, FpFormat.Bf16, FpFormat.Fp32)
