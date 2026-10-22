package harith.fp

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis

/**
  * A radix-2 restoring square root.
  *
  * The operand is normalized to `[1, 4)` with an even exponent, and the root is produced one bit
  * per cycle by the restoring recurrence, with the final remainder providing the sticky bit. The
  * result is correctly rounded.
  *
  * fpga@fp64-55cyc: delay[i/o/max] = 5.915ns/1.416ns/20.888ns  area = 1596luts + 264ff
  *
  * 55nm@fp64-55cyc: delay[i/o/max] = 5.2639ns/0.8858ns/8.2514ns  area = 8034.04um²
  *
  * fpga@fp32-26cyc: delay[i/o/max] = 5.011ns/1.505ns/18.073ns  area = 810luts + 141ff
  *
  * 55nm@fp32-26cyc: delay[i/o/max] = 1.8587ns/0.8798ns/5.1646ns  area = 4199.44um²
  *
  * fpga@fp16-13cyc: delay[i/o/max] = 3.789ns/1.389ns/16.265ns  area = 446luts + 80ff
  *
  * 55nm@fp16-13cyc: delay[i/o/max] = 1.8282ns/0.8856ns/3.9789ns  area = 2201.64um²
  *
  * fpga@bf16-10cyc: delay[i/o/max] = 4.195ns/1.493ns/14.461ns  area = 359luts + 74ff
  *
  * 55nm@bf16-10cyc: delay[i/o/max] = 1.7902ns/0.8855ns/4.0429ns  area = 2177.84um²
  *
  * @param inFmt  The format of the operand
  * @param outFmt The format of the result
  * @param policy The numeric policy
  */
class FpRestoringSqrt(
    val inFmt:  FpFormat,
    val outFmt: FpFormat,
    policy:     FpPolicy = FpPolicy(),
) extends FpSqrt {
  val io = IO(new FpUnaryIO(inFmt, outFmt))

  private val N    = outFmt.manWidth + 2
  private val radW = 2 * (N + 1)
  private val remW = N + 5

  require(2 * N >= inFmt.manWidth, "the output format is too narrow for the radicand")

  override def latency: Int = N + 1

  val busy  = RegInit(false.B)
  val valid = RegInit(false.B)
  val count = RegInit(0.U(log2Ceil(N + 2).W))

  val signReg  = Reg(Bool())
  val expReg   = Reg(SInt(FpUtils.EW.W))
  val rmReg    = Reg(UInt(3.W))
  val shiftReg = Reg(UInt(radW.W))
  val remReg   = Reg(UInt(remW.W))
  val rootReg  = Reg(UInt((N + 1).W))

  val nanReg  = Reg(Bool())
  val infReg  = Reg(Bool())
  val zeroReg = Reg(Bool())
  val negReg  = Reg(Bool())

  val outReg   = Reg(UInt(outFmt.width.W))
  val flagsReg = Reg(new FpFlags)

  val a        = FpUtils.decode(io.in.bits.src, inFmt)
  val (m2, e2) = FpUnaryUtil.normalizeEven(a, inFmt)
  val radicand = m2 << (2 * N - inFmt.manWidth)

  val pair     = shiftReg(radW - 1, radW - 2)
  val shifted  = (remReg << 2) | pair
  val trial    = (rootReg << 2) | 1.U
  val ge       = shifted >= trial.pad(shifted.getWidth)
  val remNext  = Mux(ge, shifted - trial.pad(shifted.getWidth), shifted)
  val rootNext = Cat(rootReg(N - 1, 0), ge)
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
      count    := (N + 1).U
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
    shiftReg := (shiftReg << 2)(radW - 1, 0)
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

object FpRestoringSqrtFp64 extends App {
  ExportForAnalysis(new FpRestoringSqrt(FpFormat.Fp64, FpFormat.Fp64), args, FpExport.opts)
}

object FpRestoringSqrtFp32 extends App {
  ExportForAnalysis(new FpRestoringSqrt(FpFormat.Fp32, FpFormat.Fp32), args, FpExport.opts)
}

object FpRestoringSqrtFp16 extends App {
  ExportForAnalysis(new FpRestoringSqrt(FpFormat.Fp16, FpFormat.Fp16), args, FpExport.opts)
}

object FpRestoringSqrtBf16 extends App {
  ExportForAnalysis(new FpRestoringSqrt(FpFormat.Bf16, FpFormat.Bf16), args, FpExport.opts)
}
