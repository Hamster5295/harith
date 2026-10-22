package harith.fp

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis

/**
  * A radix-2 restoring reciprocal.
  *
  * The operand is normalized to `[1, 2)` and the quotient bits of `1 / significand` are produced
  * one per cycle by the restoring recurrence, with the final remainder providing the sticky bit.
  * The result is correctly rounded.
  *
  * fpga@fp64-56cyc: delay[i/o/max] = 5.642ns/1.362ns/20.281ns  area = 1553luts + 259ff
  *
  * 55nm@fp64-56cyc: delay[i/o/max] = 4.2967ns/0.8856ns/8.7567ns  area = 7825.16um²
  *
  * fpga@fp32-27cyc: delay[i/o/max] = 4.334ns/1.477ns/18.282ns  area = 746luts + 136ff
  *
  * 55nm@fp32-27cyc: delay[i/o/max] = 2.3376ns/0.8798ns/5.5987ns  area = 3828.72um²
  *
  * fpga@fp16-14cyc: delay[i/o/max] = 3.590ns/1.482ns/16.867ns  area = 425luts + 75ff
  *
  * 55nm@fp16-14cyc: delay[i/o/max] = 1.9488ns/0.8917ns/3.7728ns  area = 2119.04um²
  *
  * fpga@bf16-11cyc: delay[i/o/max] = 3.643ns/1.373ns/14.442ns  area = 348luts + 69ff
  *
  * 55nm@bf16-11cyc: delay[i/o/max] = 2.1707ns/0.8858ns/3.4835ns  area = 1924.72um²
  *
  * @param inFmt  The format of the operand
  * @param outFmt The format of the result
  * @param policy The numeric policy
  */
class FpRestoringReciprocal(
    val inFmt:  FpFormat,
    val outFmt: FpFormat,
    policy:     FpPolicy = FpPolicy(),
) extends FpReciprocal {
  val io = IO(new FpUnaryIO(inFmt, outFmt))

  private val W    = inFmt.manWidth + 1
  private val N    = outFmt.manWidth + 4
  private val remW = W + 2

  override def latency: Int = N

  val busy  = RegInit(false.B)
  val valid = RegInit(false.B)
  val count = RegInit(0.U(log2Ceil(N + 1).W))

  val signReg = Reg(Bool())
  val expReg  = Reg(SInt(FpUtils.EW.W))
  val rmReg   = Reg(UInt(3.W))
  val divReg  = Reg(UInt(W.W))
  val remReg  = Reg(UInt(remW.W))
  val quoReg  = Reg(UInt(N.W))

  val nanReg  = Reg(Bool())
  val infReg  = Reg(Bool())
  val zeroReg = Reg(Bool())

  val outReg   = Reg(UInt(outFmt.width.W))
  val flagsReg = Reg(new FpFlags)

  val a       = FpUtils.decode(io.in.bits.src, inFmt)
  val (m, e)  = FpUnaryUtil.normalize(a, inFmt)
  val doubled = remReg << 1
  val divPad  = divReg.pad(doubled.getWidth)
  val ge      = doubled >= divPad
  val remNext = Mux(ge, doubled - divPad, doubled)
  val quoNext = Cat(quoReg(N - 2, 0), ge)
  val last    = count === 1.U

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
      expReg  := e
      rmReg   := io.in.bits.rm
      divReg  := m
      remReg  := (BigInt(1) << (W - 1)).U(remW.W)
      quoReg  := 0.U
      nanReg  := a.isNaN
      infReg  := a.isInf
      zeroReg := a.isZero
    }
  }.otherwise {
    remReg := remNext(remW - 1, 0)
    quoReg := quoNext
    when(last) {
      busy  := false.B
      valid := true.B

      val sticky = remNext =/= 0.U
      val packed = FpUtils.roundPack(
        signReg,
        quoNext,
        -N.S(FpUtils.EW.W) - expReg,
        outFmt,
        rmReg,
        policy,
        sticky,
      )

      val special = nanReg || infReg || zeroReg
      outReg := Mux(
        nanReg,
        outFmt.canonicalNaN,
        Mux(
          infReg,
          outFmt.zero(signReg),
          Mux(zeroReg, Cat(signReg, outFmt.infinityMag), packed.bits),
        ),
      )
      flagsReg.nx := Mux(special, false.B, packed.nx)
      flagsReg.uf := Mux(special, false.B, packed.uf)
      flagsReg.of := Mux(special, false.B, packed.of)
      flagsReg.dz := zeroReg
      flagsReg.nv := false.B
    }.otherwise {
      count := count - 1.U
    }
  }
}

object FpRestoringReciprocalFp64 extends App {
  ExportForAnalysis(new FpRestoringReciprocal(FpFormat.Fp64, FpFormat.Fp64), args, FpExport.opts)
}

object FpRestoringReciprocalFp32 extends App {
  ExportForAnalysis(new FpRestoringReciprocal(FpFormat.Fp32, FpFormat.Fp32), args, FpExport.opts)
}

object FpRestoringReciprocalFp16 extends App {
  ExportForAnalysis(new FpRestoringReciprocal(FpFormat.Fp16, FpFormat.Fp16), args, FpExport.opts)
}

object FpRestoringReciprocalBf16 extends App {
  ExportForAnalysis(new FpRestoringReciprocal(FpFormat.Bf16, FpFormat.Bf16), args, FpExport.opts)
}
