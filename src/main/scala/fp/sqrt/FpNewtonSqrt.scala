package harith.fp

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis
import harith.uint.UIntMacroMul
import harith.uint.UIntMul

/**
  * A Newton-Raphson square root with a piecewise-constant seed.
  *
  * The reciprocal square root is computed by fixed-point Newton-Raphson iterations and multiplied
  * by the operand. The result is faithfully rounded (within one unit in the last place), not
  * correctly rounded.
  *
  * fpga@fp64: delay[i/o/max] = 134.717ns/0.912ns/0.881ns  area = 3438luts + 68ff
  *
  * 55nm@fp64: delay[i/o/max] = 73.8453ns/0.4557ns/73.8453ns  area = 367311.00um²
  *
  * fpga@fp32: delay[i/o/max] = 95.495ns/0.912ns/0.881ns  area = 1463luts + 36ff
  *
  * 55nm@fp32: delay[i/o/max] = 42.9883ns/0.4557ns/42.9883ns  area = 112291.20um²
  *
  * fpga@fp16: delay[i/o/max] = 60.256ns/0.912ns/0.881ns  area = 973luts + 20ff
  *
  * 55nm@fp16: delay[i/o/max] = 25.5042ns/0.4560ns/25.5042ns  area = 29868.44um²
  *
  * fpga@bf16: delay[i/o/max] = 48.873ns/0.912ns/0.881ns  area = 519luts + 20ff
  *
  * 55nm@bf16: delay[i/o/max] = 14.7767ns/0.4560ns/14.7767ns  area = 13866.44um²
  *
  * @param inFmt  The format of the operand
  * @param outFmt The format of the result
  * @param mul    The combinational unsigned multiplier factory
  * @param policy The numeric policy
  */
class FpNewtonSqrt(
    val inFmt:  FpFormat,
    val outFmt: FpFormat,
    mul:        Int => UIntMul,
    policy:     FpPolicy = FpPolicy(),
) extends FpSqrt {
  val io = IO(new FpUnaryIO(inFmt, outFmt))

  private val P        = outFmt.manWidth + 8
  private val W        = P + 2
  private val k        = math.min(7, inFmt.manWidth)
  private val seedFrac = math.min(16, P - 1)
  private val table: Seq[BigInt] = Seq.tabulate(1 << (k + 2)) { idx =>
    if (idx < (1 << k)) BigInt(0)
    else {
      val m = (idx + 0.5) / (1 << k)
      BigInt(math.round((1.0 / math.sqrt(m)) * (1 << seedFrac)))
    }
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

  val a        = FpUtils.decode(io.in.bits.src, inFmt)
  val (m2, e2) = FpUnaryUtil.normalizeEven(a, inFmt)
  val x        = (m2 << (P - inFmt.manWidth)).pad(W)
  val index    = m2(inFmt.manWidth + 1, inFmt.manWidth - k)

  val core = Module(new FpNewtonRsqrtCore(P, k, seedFrac, table, iters, mul))
  core.io.x     := x
  core.io.index := index
  val z = core.io.z

  val m1 = Module(mul(W))
  m1.io.src1 := x
  m1.io.src2 := z
  val s = (m1.io.output >> P)(W - 1, 0)

  io.in.ready        := !io.flush
  io.out.valid       := valid
  io.out.bits.output := outReg
  io.out.bits.fflags := flagsReg

  when(io.flush) {
    valid := false.B
  }.elsewhen(io.in.fire) {
    valid := true.B

    val neg    = a.sign && !a.isZero && !a.isNaN
    val packed =
      FpUtils.roundPack(false.B, s, (e2 >> 1) - P.S(FpUtils.EW.W), outFmt, io.in.bits.rm, policy)

    outReg := Mux(
      neg || a.isNaN,
      outFmt.canonicalNaN,
      Mux(
        a.isInf,
        Mux(a.sign, outFmt.canonicalNaN, Cat(false.B, outFmt.infinityMag)),
        Mux(a.isZero, outFmt.zero(a.sign), packed.bits),
      ),
    )
    flagsReg.nx := Mux(neg || a.isNaN || a.isInf || a.isZero, false.B, packed.nx)
    flagsReg.uf := Mux(neg || a.isNaN || a.isInf || a.isZero, false.B, packed.uf)
    flagsReg.of := Mux(neg || a.isNaN || a.isInf || a.isZero, false.B, packed.of)
    flagsReg.dz := false.B
    flagsReg.nv := neg
  }
}

object FpNewtonSqrtFp64 extends App {
  ExportForAnalysis(
    new FpNewtonSqrt(FpFormat.Fp64, FpFormat.Fp64, w => new UIntMacroMul(w)),
    args,
    FpExport.opts,
  )
}

object FpNewtonSqrtFp32 extends App {
  ExportForAnalysis(
    new FpNewtonSqrt(FpFormat.Fp32, FpFormat.Fp32, w => new UIntMacroMul(w)),
    args,
    FpExport.opts,
  )
}

object FpNewtonSqrtFp16 extends App {
  ExportForAnalysis(
    new FpNewtonSqrt(FpFormat.Fp16, FpFormat.Fp16, w => new UIntMacroMul(w)),
    args,
    FpExport.opts,
  )
}

object FpNewtonSqrtBf16 extends App {
  ExportForAnalysis(
    new FpNewtonSqrt(FpFormat.Bf16, FpFormat.Bf16, w => new UIntMacroMul(w)),
    args,
    FpExport.opts,
  )
}
