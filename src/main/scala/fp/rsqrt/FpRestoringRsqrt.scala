package harith.fp

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis

/**
  * A radix-2 restoring reciprocal square root.
  *
  * The operand is normalized to `[1, 4)` with an even exponent. The unit keeps the residual
  * `x * y^2` and the product `x * y` in fixed point and appends one result bit per cycle, choosing
  * the digit that keeps `x * y^2 <= 1`. The final residual provides the sticky bit, so the result
  * is correctly rounded without a separate square root and reciprocal step.
  *
  * fpga@fp64-55cyc: delay[i/o/max] = 5.968ns/1.453ns/21.189ns  area = 3044luts + 443ff
  *
  * 55nm@fp64-55cyc: delay[i/o/max] = 8.1365ns/0.8862ns/15.6430ns  area = 19876.08um²
  *
  * fpga@fp32-26cyc: delay[i/o/max] = 5.059ns/1.436ns/19.099ns  area = 1419luts + 234ff
  *
  * 55nm@fp32-26cyc: delay[i/o/max] = 4.3361ns/0.8862ns/10.8285ns  area = 10578.40um²
  *
  * fpga@fp16-13cyc: delay[i/o/max] = 3.558ns/1.354ns/16.442ns  area = 746luts + 128ff
  *
  * 55nm@fp16-13cyc: delay[i/o/max] = 2.1229ns/0.8855ns/6.5856ns  area = 5530.84um²
  *
  * fpga@bf16-10cyc: delay[i/o/max] = 4.577ns/1.351ns/15.732ns  area = 589luts + 115ff
  *
  * 55nm@bf16-10cyc: delay[i/o/max] = 2.7075ns/0.8858ns/5.6220ns  area = 4684.12um²
  *
  * @param inFmt  The format of the operand
  * @param outFmt The format of the result
  * @param policy The numeric policy
  */
class FpRestoringRsqrt(
    val inFmt:  FpFormat,
    val outFmt: FpFormat,
    policy:     FpPolicy = FpPolicy(),
) extends FpRsqrt {
  val io = IO(new FpUnaryIO(inFmt, outFmt))

  private val N     = outFmt.manWidth + 3
  private val S     = 2 * N + 4
  private val W     = S + 4
  private val shift = S - inFmt.manWidth

  require(shift >= 0, "the output format is too narrow for the residual")

  override def latency: Int = N

  val busy  = RegInit(false.B)
  val valid = RegInit(false.B)
  val count = RegInit(0.U(log2Ceil(N + 1).W))

  val signReg = Reg(Bool())
  val expReg  = Reg(SInt(FpUtils.EW.W))
  val rmReg   = Reg(UInt(3.W))
  val mulReg  = Reg(UInt(W.W))
  val prodReg = Reg(UInt(W.W))
  val resReg  = Reg(UInt(W.W))
  val rootReg = Reg(UInt(N.W))

  val nanReg  = Reg(Bool())
  val infReg  = Reg(Bool())
  val zeroReg = Reg(Bool())
  val negReg  = Reg(Bool())

  val outReg   = Reg(UInt(outFmt.width.W))
  val flagsReg = Reg(new FpFlags)

  val a        = FpUtils.decode(io.in.bits.src, inFmt)
  val (m2, e2) = FpUnaryUtil.normalizeEven(a, inFmt)
  val scale    = (BigInt(1) << S).U(W.W)
  val k        = N.U - count + 1.U

  val pInc  = mulReg >> k
  val qInc  = (prodReg >> (k - 1.U)) + (mulReg >> (k << 1))
  val digit = (resReg + qInc) <= scale

  val prodNext = prodReg + Mux(digit, pInc, 0.U)
  val resNext  = resReg + Mux(digit, qInc, 0.U)
  val rootNext = Cat(rootReg(N - 2, 0), digit)
  val last     = count === 1.U

  io.in.ready        := !busy && !io.flush
  io.out.valid       := valid
  io.out.bits.output := outReg
  io.out.bits.fflags := flagsReg

  when(io.flush) {
    busy  := false.B
    valid := false.B
  }.elsewhen(!busy) {
    when(io.in.fire) {
      busy    := true.B
      valid   := false.B
      count   := N.U
      signReg := a.sign
      expReg  := e2
      rmReg   := io.in.bits.rm
      mulReg  := (m2 << shift).pad(W)
      prodReg := 0.U
      resReg  := 0.U
      rootReg := 0.U
      nanReg  := a.isNaN
      infReg  := a.isInf
      zeroReg := a.isZero
      negReg  := a.sign && !a.isZero && !a.isNaN
    }
  }.otherwise {
    prodReg := prodNext
    resReg  := resNext
    rootReg := rootNext
    when(last) {
      busy  := false.B
      valid := true.B

      val sticky = resNext =/= scale
      val packed = FpUtils.roundPack(
        false.B,
        rootNext,
        -N.S(FpUtils.EW.W) - (expReg >> 1),
        outFmt,
        rmReg,
        policy,
        sticky,
      )

      outReg := Mux(
        negReg || nanReg,
        outFmt.canonicalNaN,
        Mux(
          zeroReg,
          Cat(signReg, outFmt.infinityMag),
          Mux(infReg, outFmt.zero(false.B), packed.bits),
        ),
      )
      flagsReg.nx := Mux(negReg || nanReg || infReg || zeroReg, false.B, packed.nx)
      flagsReg.uf := Mux(negReg || nanReg || infReg || zeroReg, false.B, packed.uf)
      flagsReg.of := Mux(negReg || nanReg || infReg || zeroReg, false.B, packed.of)
      flagsReg.dz := zeroReg
      flagsReg.nv := negReg
    }.otherwise {
      count := count - 1.U
    }
  }
}

object FpRestoringRsqrtFp64 extends App {
  ExportForAnalysis(new FpRestoringRsqrt(FpFormat.Fp64, FpFormat.Fp64), args, FpExport.opts)
}

object FpRestoringRsqrtFp32 extends App {
  ExportForAnalysis(new FpRestoringRsqrt(FpFormat.Fp32, FpFormat.Fp32), args, FpExport.opts)
}

object FpRestoringRsqrtFp16 extends App {
  ExportForAnalysis(new FpRestoringRsqrt(FpFormat.Fp16, FpFormat.Fp16), args, FpExport.opts)
}

object FpRestoringRsqrtBf16 extends App {
  ExportForAnalysis(new FpRestoringRsqrt(FpFormat.Bf16, FpFormat.Bf16), args, FpExport.opts)
}
