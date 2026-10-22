package harith.fp

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis
import harith.uint.UIntMacroMul
import harith.uint.UIntMul

/**
  * A Newton-Raphson reciprocal with a piecewise-constant seed.
  *
  * The seed is a table indexed by the top mantissa bits; two fixed-point Newton-Raphson iterations
  * per stage converge quadratically to `1 / significand`. The result is faithfully rounded (within
  * one unit in the last place), not correctly rounded.
  *
  * fpga@fp64: delay[i/o/max] = 90.421ns/0.912ns/1.017ns  area = 2734luts + 68ff
  *
  * 55nm@fp64: delay[i/o/max] = 46.9906ns/0.4560ns/46.9906ns  area = 215430.88um²
  *
  * fpga@fp32: delay[i/o/max] = 53.401ns/0.912ns/1.017ns  area = 1109luts + 36ff
  *
  * 55nm@fp32: delay[i/o/max] = 24.2199ns/0.4560ns/24.2199ns  area = 41807.08um²
  *
  * fpga@fp16: delay[i/o/max] = 44.718ns/0.912ns/1.017ns  area = 722luts + 20ff
  *
  * 55nm@fp16: delay[i/o/max] = 17.1130ns/0.4560ns/17.1130ns  area = 16264.36um²
  *
  * fpga@bf16: delay[i/o/max] = 37.960ns/0.912ns/1.017ns  area = 454luts + 20ff
  *
  * 55nm@bf16: delay[i/o/max] = 7.5151ns/0.4560ns/7.5151ns  area = 4809.00um²
  *
  * @param inFmt  The format of the operand
  * @param outFmt The format of the result
  * @param mul    The combinational unsigned multiplier factory
  * @param policy The numeric policy
  */
class FpNewtonReciprocal(
    val inFmt:  FpFormat,
    val outFmt: FpFormat,
    mul:        Int => UIntMul,
    policy:     FpPolicy = FpPolicy(),
) extends FpReciprocal {
  val io = IO(new FpUnaryIO(inFmt, outFmt))

  private val P        = outFmt.manWidth + 8
  private val W        = P + 2
  private val k        = math.min(8, inFmt.manWidth)
  private val seedFrac = math.min(16, P - 1)
  private val table: Seq[BigInt] = Seq.tabulate(1 << k) { i =>
    val m = 1.0 + (i + 0.5) / (1 << k)
    BigInt(math.round((2.0 / m) * (1 << seedFrac)))
  }
  private val iters = {
    var e = k + 1
    var n = 0
    while (e < P + 2) {
      e *= 2
      n += 1
    }
    n
  }

  override def latency: Int = 1

  val valid    = RegInit(false.B)
  val outReg   = Reg(UInt(outFmt.width.W))
  val flagsReg = Reg(new FpFlags)

  val a       = FpUtils.decode(io.in.bits.src, inFmt)
  val (m, e)  = FpUnaryUtil.normalize(a, inFmt)
  val seedVec = VecInit(table.map(_.U((seedFrac + 2).W)))
  val index   = m(inFmt.manWidth - 1, inFmt.manWidth - k)
  val seed    = seedVec(index)
  val x       = (m << (P - inFmt.manWidth)).pad(W)
  val y0      = (seed << (P - seedFrac - 1)).pad(W)

  var y = y0
  for (_ <- 0 until iters) {
    val m1 = Module(mul(W))
    m1.io.src1 := x
    m1.io.src2 := y
    val mz = (m1.io.output >> P)(W - 1, 0)
    val t  = (BigInt(2) << P).U(W.W) - mz
    val m2 = Module(mul(W))
    m2.io.src1 := y
    m2.io.src2 := t
    y = (m2.io.output >> P)(W - 1, 0)
  }

  io.in.ready        := !io.flush
  io.out.valid       := valid
  io.out.bits.output := outReg
  io.out.bits.fflags := flagsReg

  when(io.flush) {
    valid := false.B
  }.elsewhen(io.in.fire) {
    valid := true.B

    val packed = FpUtils.roundPack(a.sign, y, -P.S(FpUtils.EW.W) - e, outFmt, io.in.bits.rm, policy)

    val special = a.isNaN || a.isInf || a.isZero
    outReg := Mux(
      a.isNaN,
      outFmt.canonicalNaN,
      Mux(
        a.isInf,
        outFmt.zero(a.sign),
        Mux(a.isZero, Cat(a.sign, outFmt.infinityMag), packed.bits),
      ),
    )
    flagsReg.nx := Mux(special, false.B, packed.nx)
    flagsReg.uf := Mux(special, false.B, packed.uf)
    flagsReg.of := Mux(special, false.B, packed.of)
    flagsReg.dz := a.isZero
    flagsReg.nv := false.B
  }
}

object FpNewtonReciprocalFp64 extends App {
  ExportForAnalysis(
    new FpNewtonReciprocal(FpFormat.Fp64, FpFormat.Fp64, w => new UIntMacroMul(w)),
    args,
    FpExport.opts,
  )
}

object FpNewtonReciprocalFp32 extends App {
  ExportForAnalysis(
    new FpNewtonReciprocal(FpFormat.Fp32, FpFormat.Fp32, w => new UIntMacroMul(w)),
    args,
    FpExport.opts,
  )
}

object FpNewtonReciprocalFp16 extends App {
  ExportForAnalysis(
    new FpNewtonReciprocal(FpFormat.Fp16, FpFormat.Fp16, w => new UIntMacroMul(w)),
    args,
    FpExport.opts,
  )
}

object FpNewtonReciprocalBf16 extends App {
  ExportForAnalysis(
    new FpNewtonReciprocal(FpFormat.Bf16, FpFormat.Bf16, w => new UIntMacroMul(w)),
    args,
    FpExport.opts,
  )
}
