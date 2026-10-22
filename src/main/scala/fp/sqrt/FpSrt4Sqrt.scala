package harith.fp

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis

/**
  * A radix-4 square root.
  *
  * The operand is normalized to `[1, 4)` with an even exponent, and two root bits are produced per
  * cycle by an exact radix-4 digit recurrence (digits `0..3`). The final remainder provides the
  * sticky bit, so the result is correctly rounded.
  *
  * fpga@fp64-28cyc: delay[i/o/max] = 6.175ns/1.415ns/22.011ns  area = 1773luts + 271ff
  *
  * 55nm@fp64-28cyc: delay[i/o/max] = 5.2306ns/0.8862ns/8.8452ns  area = 10170.72um²
  *
  * fpga@fp32-13cyc: delay[i/o/max] = 5.140ns/1.400ns/19.971ns  area = 883luts + 144ff
  *
  * 55nm@fp32-13cyc: delay[i/o/max] = 1.8460ns/0.8856ns/5.9918ns  area = 5274.36um²
  *
  * fpga@fp16-7cyc: delay[i/o/max] = 3.605ns/1.376ns/19.074ns  area = 518luts + 87ff
  *
  * 55nm@fp16-7cyc: delay[i/o/max] = 1.8003ns/0.8856ns/4.9940ns  area = 2850.12um²
  *
  * fpga@bf16-5cyc: delay[i/o/max] = 4.126ns/1.471ns/16.515ns  area = 409luts + 79ff
  *
  * 55nm@bf16-5cyc: delay[i/o/max] = 1.8209ns/0.8856ns/4.8894ns  area = 2471.00um²
  *
  * @param inFmt  The format of the operand
  * @param outFmt The format of the result
  * @param policy The numeric policy
  */
class FpSrt4Sqrt(
    val inFmt:  FpFormat,
    val outFmt: FpFormat,
    policy:     FpPolicy = FpPolicy(),
) extends FpSqrt {
  val io = IO(new FpUnaryIO(inFmt, outFmt))

  private val N        = outFmt.manWidth + 2 + ((outFmt.manWidth + 3) % 2)
  private val rootBits = N + 1
  private val steps    = rootBits / 2
  private val radW     = 2 * rootBits
  private val remW     = rootBits + 10

  require(2 * N >= inFmt.manWidth, "the output format is too narrow for the radicand")

  override def latency: Int = steps

  val busy  = RegInit(false.B)
  val valid = RegInit(false.B)
  val count = RegInit(0.U(log2Ceil(steps + 1).W))

  val signReg  = Reg(Bool())
  val expReg   = Reg(SInt(FpUtils.EW.W))
  val rmReg    = Reg(UInt(3.W))
  val shiftReg = Reg(UInt(radW.W))
  val remReg   = Reg(UInt(remW.W))
  val rootReg  = Reg(UInt(rootBits.W))

  val nanReg  = Reg(Bool())
  val infReg  = Reg(Bool())
  val zeroReg = Reg(Bool())
  val negReg  = Reg(Bool())

  val outReg   = Reg(UInt(outFmt.width.W))
  val flagsReg = Reg(new FpFlags)

  val a        = FpUtils.decode(io.in.bits.src, inFmt)
  val (m2, e2) = FpUnaryUtil.normalizeEven(a, inFmt)
  val radicand = m2 << (2 * N - inFmt.manWidth)

  val take     = shiftReg(radW - 1, radW - 4)
  val shifted  = (remReg << 4) | take
  val t1       = (rootReg << 3) | 1.U
  val t2       = (rootReg << 4) | 4.U
  val t3       = (rootReg << 4) + (rootReg << 3) + 9.U
  val d3       = shifted >= t3.pad(shifted.getWidth)
  val d2       = shifted >= t2.pad(shifted.getWidth)
  val d1       = shifted >= t1.pad(shifted.getWidth)
  val digit    = Mux(d3, 3.U(2.W), Mux(d2, 2.U(2.W), Mux(d1, 1.U(2.W), 0.U(2.W))))
  val trial    = Mux(d3, t3, Mux(d2, t2, Mux(d1, t1, 0.U)))
  val remNext  = shifted - trial.pad(shifted.getWidth)
  val rootNext = Cat(rootReg(rootBits - 3, 0), digit)
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
      busy     := true.B
      valid    := false.B
      count    := steps.U
      signReg  := a.sign
      expReg   := e2
      rmReg    := io.in.bits.rm
      shiftReg := radicand
      remReg   := 0.U
      rootReg  := 0.U
      nanReg   := a.isNaN
      infReg   := a.isInf
      zeroReg  := a.isZero
      negReg   := a.sign && !a.isZero && !a.isNaN
    }
  }.otherwise {
    shiftReg := (shiftReg << 4)(radW - 1, 0)
    remReg   := remNext(remW - 1, 0)
    rootReg  := rootNext
    when(last) {
      busy  := false.B
      valid := true.B

      val sticky = remNext =/= 0.U
      val packed = FpUtils.roundPack(
        false.B,
        rootNext,
        (expReg >> 1) - N.S(FpUtils.EW.W),
        outFmt,
        rmReg,
        policy,
        sticky,
      )

      outReg := Mux(
        negReg,
        outFmt.canonicalNaN,
        Mux(
          nanReg,
          outFmt.canonicalNaN,
          Mux(
            infReg,
            Cat(false.B, outFmt.infinityMag),
            Mux(zeroReg, outFmt.zero(signReg), packed.bits),
          ),
        ),
      )
      flagsReg.nx := Mux(negReg || nanReg || infReg || zeroReg, false.B, packed.nx)
      flagsReg.uf := Mux(negReg || nanReg || infReg || zeroReg, false.B, packed.uf)
      flagsReg.of := Mux(negReg || nanReg || infReg || zeroReg, false.B, packed.of)
      flagsReg.dz := false.B
      flagsReg.nv := negReg
    }.otherwise {
      count := count - 1.U
    }
  }
}

object FpSrt4SqrtFp64 extends App {
  ExportForAnalysis(new FpSrt4Sqrt(FpFormat.Fp64, FpFormat.Fp64), args, FpExport.opts)
}

object FpSrt4SqrtFp32 extends App {
  ExportForAnalysis(new FpSrt4Sqrt(FpFormat.Fp32, FpFormat.Fp32), args, FpExport.opts)
}

object FpSrt4SqrtFp16 extends App {
  ExportForAnalysis(new FpSrt4Sqrt(FpFormat.Fp16, FpFormat.Fp16), args, FpExport.opts)
}

object FpSrt4SqrtBf16 extends App {
  ExportForAnalysis(new FpSrt4Sqrt(FpFormat.Bf16, FpFormat.Bf16), args, FpExport.opts)
}
