package harith.fp

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis

/**
  * A radix-4 reciprocal square root.
  *
  * The operand is normalized to `[1, 4)` with an even exponent. The residual `x * y^2` and the
  * product `x * y` are kept in fixed point and two result bits are appended per cycle, choosing the
  * radix-4 digit that keeps `x * y^2 <= 1`. The final residual provides the sticky bit, so the
  * result is correctly rounded.
  *
  * fpga@fp64-28cyc: delay[i/o/max] = 6.722ns/1.442ns/30.759ns  area = 4341luts + 457ff
  *
  * 55nm@fp64-28cyc: delay[i/o/max] = 7.6004ns/0.8862ns/28.8763ns  area = 32988.76um²
  *
  * fpga@fp32-13cyc: delay[i/o/max] = 4.848ns/1.456ns/24.933ns  area = 1987luts + 233ff
  *
  * 55nm@fp32-13cyc: delay[i/o/max] = 2.8003ns/0.8798ns/16.6951ns  area = 15535.52um²
  *
  * fpga@fp16-7cyc: delay[i/o/max] = 3.561ns/1.456ns/23.851ns  area = 990luts + 133ff
  *
  * 55nm@fp16-7cyc: delay[i/o/max] = 2.0890ns/0.8864ns/10.7366ns  area = 8750.84um²
  *
  * fpga@bf16-5cyc: delay[i/o/max] = 4.125ns/1.453ns/21.229ns  area = 831luts + 118ff
  *
  * 55nm@bf16-5cyc: delay[i/o/max] = 1.9298ns/0.8855ns/8.1891ns  area = 6666.24um²
  *
  * @param inFmt  The format of the operand
  * @param outFmt The format of the result
  * @param policy The numeric policy
  */
class FpSrt4Rsqrt(
    val inFmt:  FpFormat,
    val outFmt: FpFormat,
    policy:     FpPolicy = FpPolicy(),
) extends FpRsqrt {
  val io = IO(new FpUnaryIO(inFmt, outFmt))

  private val N     = outFmt.manWidth + 3 + ((outFmt.manWidth + 3) % 2)
  private val steps = N / 2
  private val S     = 2 * N + 4
  private val W     = S + 6
  private val shift = S - inFmt.manWidth

  require(shift >= 0, "the output format is too narrow for the residual")

  override def latency: Int = steps

  val busy  = RegInit(false.B)
  val valid = RegInit(false.B)
  val count = RegInit(0.U(log2Ceil(steps + 1).W))

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
  val stepNo   = steps.U - count + 1.U
  val k        = stepNo << 1

  val pShift = prodReg >> (k - 1.U)
  val mShift = mulReg >> (k << 1)
  val q1     = pShift + mShift
  val q2     = (pShift << 1) + (mShift << 2)
  val q3     = (pShift << 1) + pShift + (mShift << 3) + mShift
  val d3     = (resReg + q3) <= scale
  val d2     = (resReg + q2) <= scale
  val d1     = (resReg + q1) <= scale
  val digit  = Mux(d3, 3.U(2.W), Mux(d2, 2.U(2.W), Mux(d1, 1.U(2.W), 0.U(2.W))))
  val qInc   = Mux(d3, q3, Mux(d2, q2, Mux(d1, q1, 0.U)))

  val pBase = mulReg >> k
  val pInc  = Mux(d3, (pBase << 1) + pBase, Mux(d2, pBase << 1, Mux(d1, pBase, 0.U)))

  val prodNext = prodReg + pInc
  val resNext  = resReg + qInc
  val rootNext = Cat(rootReg(N - 3, 0), digit)
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
      count   := steps.U
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

object FpSrt4RsqrtFp64 extends App {
  ExportForAnalysis(new FpSrt4Rsqrt(FpFormat.Fp64, FpFormat.Fp64), args, FpExport.opts)
}

object FpSrt4RsqrtFp32 extends App {
  ExportForAnalysis(new FpSrt4Rsqrt(FpFormat.Fp32, FpFormat.Fp32), args, FpExport.opts)
}

object FpSrt4RsqrtFp16 extends App {
  ExportForAnalysis(new FpSrt4Rsqrt(FpFormat.Fp16, FpFormat.Fp16), args, FpExport.opts)
}

object FpSrt4RsqrtBf16 extends App {
  ExportForAnalysis(new FpSrt4Rsqrt(FpFormat.Bf16, FpFormat.Bf16), args, FpExport.opts)
}
