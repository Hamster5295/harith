package harith.fp

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis
import harith.uint.UIntMacroMul
import harith.uint.UIntMul

/**
  * A Newton-Raphson reciprocal square root with a piecewise-constant seed.
  *
  * The seed is a table indexed by the top mantissa bits; fixed-point Newton-Raphson iterations
  * converge quadratically to `1 / sqrt(significand)`. The result is faithfully rounded (within one
  * unit in the last place), not correctly rounded.
  *
  * fpga@fp64: delay[i/o/max] = 124.845ns/0.912ns/0.881ns  area = 3276luts + 70ff
  *
  * 55nm@fp64: delay[i/o/max] = 64.1853ns/0.4560ns/64.1853ns  area = 318050.60um²
  *
  * fpga@fp32: delay[i/o/max] = 90.303ns/0.912ns/0.881ns  area = 1452luts + 38ff
  *
  * 55nm@fp32: delay[i/o/max] = 39.7161ns/0.4560ns/39.7161ns  area = 101573.36um²
  *
  * fpga@fp16: delay[i/o/max] = 55.913ns/0.912ns/0.881ns  area = 919luts + 22ff
  *
  * 55nm@fp16: delay[i/o/max] = 23.8437ns/0.4560ns/23.8437ns  area = 26401.48um²
  *
  * fpga@bf16: delay[i/o/max] = 45.327ns/0.912ns/0.881ns  area = 510luts + 22ff
  *
  * 55nm@bf16: delay[i/o/max] = 13.1586ns/0.4560ns/13.1586ns  area = 12145.28um²
  *
  * @param inFmt  The format of the operand
  * @param outFmt The format of the result
  * @param mul    The combinational unsigned multiplier factory
  * @param policy The numeric policy
  */
class FpNewtonRsqrt(
    val inFmt:  FpFormat,
    val outFmt: FpFormat,
    mul:        Int => UIntMul,
    policy:     FpPolicy = FpPolicy(),
) extends FpRsqrt {
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
      FpUtils.roundPack(false.B, z, -P.S(FpUtils.EW.W) - (e2 >> 1), outFmt, io.in.bits.rm, policy)

    outReg := Mux(
      neg || a.isNaN,
      outFmt.canonicalNaN,
      Mux(
        a.isZero,
        Cat(a.sign, outFmt.infinityMag),
        Mux(a.isInf, outFmt.zero(false.B), packed.bits),
      ),
    )
    flagsReg.nx := Mux(neg || a.isNaN || a.isInf || a.isZero, false.B, packed.nx)
    flagsReg.uf := Mux(neg || a.isNaN || a.isInf || a.isZero, false.B, packed.uf)
    flagsReg.of := Mux(neg || a.isNaN || a.isInf || a.isZero, false.B, packed.of)
    flagsReg.dz := a.isZero
    flagsReg.nv := neg
  }
}

object FpNewtonRsqrtFp64 extends App {
  ExportForAnalysis(
    new FpNewtonRsqrt(FpFormat.Fp64, FpFormat.Fp64, w => new UIntMacroMul(w)),
    args,
    FpExport.opts,
  )
}

object FpNewtonRsqrtFp32 extends App {
  ExportForAnalysis(
    new FpNewtonRsqrt(FpFormat.Fp32, FpFormat.Fp32, w => new UIntMacroMul(w)),
    args,
    FpExport.opts,
  )
}

object FpNewtonRsqrtFp16 extends App {
  ExportForAnalysis(
    new FpNewtonRsqrt(FpFormat.Fp16, FpFormat.Fp16, w => new UIntMacroMul(w)),
    args,
    FpExport.opts,
  )
}

object FpNewtonRsqrtBf16 extends App {
  ExportForAnalysis(
    new FpNewtonRsqrt(FpFormat.Bf16, FpFormat.Bf16, w => new UIntMacroMul(w)),
    args,
    FpExport.opts,
  )
}
