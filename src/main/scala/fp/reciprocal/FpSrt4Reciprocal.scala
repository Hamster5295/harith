package harith.fp

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis

/**
  * A radix-4 reciprocal.
  *
  * The operand is normalized to `[1, 2)` and two quotient bits are produced per cycle by an exact
  * radix-4 digit recurrence (digits `0..3`). The final remainder provides the sticky bit, so the
  * result is correctly rounded.
  *
  * fpga@fp64-28cyc: delay[i/o/max] = 5.642ns/1.365ns/22.867ns  area = 1844luts + 259ff
  *
  * 55nm@fp64-28cyc: delay[i/o/max] = 3.0128ns/0.8798ns/9.5054ns  area = 9808.96um²
  *
  * fpga@fp32-14cyc: delay[i/o/max] = 4.334ns/1.362ns/19.950ns  area = 912luts + 135ff
  *
  * 55nm@fp32-14cyc: delay[i/o/max] = 2.3968ns/0.8913ns/6.0630ns  area = 4629.80um²
  *
  * fpga@fp16-7cyc: delay[i/o/max] = 3.590ns/1.373ns/19.557ns  area = 514luts + 75ff
  *
  * 55nm@fp16-7cyc: delay[i/o/max] = 1.9617ns/0.8856ns/4.0160ns  area = 2563.96um²
  *
  * fpga@bf16-6cyc: delay[i/o/max] = 3.643ns/1.485ns/17.876ns  area = 455luts + 70ff
  *
  * 55nm@bf16-6cyc: delay[i/o/max] = 1.8763ns/0.8856ns/4.2271ns  area = 2255.68um²
  *
  * @param inFmt  The format of the operand
  * @param outFmt The format of the result
  * @param policy The numeric policy
  */
class FpSrt4Reciprocal(
    val inFmt:  FpFormat,
    val outFmt: FpFormat,
    policy:     FpPolicy = FpPolicy(),
) extends FpReciprocal {
  val io = IO(new FpUnaryIO(inFmt, outFmt))

  private val W     = inFmt.manWidth + 1
  private val N     = outFmt.manWidth + 4
  private val steps = (N + 1) / 2
  private val bits  = 2 * steps
  private val remW  = W + 4

  override def latency: Int = steps

  val busy  = RegInit(false.B)
  val valid = RegInit(false.B)
  val count = RegInit(0.U(log2Ceil(steps + 1).W))

  val signReg = Reg(Bool())
  val expReg  = Reg(SInt(FpUtils.EW.W))
  val rmReg   = Reg(UInt(3.W))
  val divReg  = Reg(UInt(W.W))
  val remReg  = Reg(UInt(remW.W))
  val quoReg  = Reg(UInt(bits.W))

  val nanReg  = Reg(Bool())
  val infReg  = Reg(Bool())
  val zeroReg = Reg(Bool())

  val outReg   = Reg(UInt(outFmt.width.W))
  val flagsReg = Reg(new FpFlags)

  val a       = FpUtils.decode(io.in.bits.src, inFmt)
  val (m, e)  = FpUnaryUtil.normalize(a, inFmt)
  val quad    = remReg << 2
  val m1      = divReg.pad(quad.getWidth)
  val m2      = m1 << 1
  val m3      = m1 + m2
  val d3      = quad >= m3
  val d2      = quad >= m2
  val d1      = quad >= m1
  val digit   = Mux(d3, 3.U(2.W), Mux(d2, 2.U(2.W), Mux(d1, 1.U(2.W), 0.U(2.W))))
  val remNext = Mux(d3, quad - m3, Mux(d2, quad - m2, Mux(d1, quad - m1, quad)))
  val quoNext = Cat(quoReg(bits - 3, 0), digit)
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
      count   := steps.U
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
        -bits.S(FpUtils.EW.W) - expReg,
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

object FpSrt4ReciprocalFp64 extends App {
  ExportForAnalysis(new FpSrt4Reciprocal(FpFormat.Fp64, FpFormat.Fp64), args, FpExport.opts)
}

object FpSrt4ReciprocalFp32 extends App {
  ExportForAnalysis(new FpSrt4Reciprocal(FpFormat.Fp32, FpFormat.Fp32), args, FpExport.opts)
}

object FpSrt4ReciprocalFp16 extends App {
  ExportForAnalysis(new FpSrt4Reciprocal(FpFormat.Fp16, FpFormat.Fp16), args, FpExport.opts)
}

object FpSrt4ReciprocalBf16 extends App {
  ExportForAnalysis(new FpSrt4Reciprocal(FpFormat.Bf16, FpFormat.Bf16), args, FpExport.opts)
}
