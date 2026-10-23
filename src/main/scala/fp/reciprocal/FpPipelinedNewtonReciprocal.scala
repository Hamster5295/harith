package harith.fp

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis
import harith.uint.UIntMacroMul
import harith.uint.UIntMul

/**
  * A pipelined Newton-Raphson reciprocal with a fixed-depth pipeline.
  *
  * The combinational Newton-Raphson chain is split at every multiplier and every result stage, so a
  * new operand can be accepted every cycle. The seed, iterations and final rounding are the same as
  * [[FpNewtonReciprocal]]; the result is faithfully rounded (within one unit in the last place).
  *
  * fpga@fp64-8cyc: delay[i/o/max] = 7.502ns/0.912ns/24.383ns  area = 2744luts + 545ff
  *
  * 55nm@fp64-8cyc: delay[i/o/max] = 4.1115ns/0.4508ns/7.9334ns  area = 189950.04um²
  *
  * fpga@fp32-6cyc: delay[i/o/max] = 6.689ns/0.912ns/20.763ns  area = 1168luts + 148ff
  *
  * 55nm@fp32-6cyc: delay[i/o/max] = 3.3681ns/0.4508ns/5.5111ns  area = 37731.12um²
  *
  * fpga@fp16-6cyc: delay[i/o/max] = 5.365ns/0.912ns/17.044ns  area = 750luts + 156ff
  *
  * 55nm@fp16-6cyc: delay[i/o/max] = 3.1719ns/0.4508ns/4.2826ns  area = 15510.32um²
  *
  * fpga@bf16-6cyc: delay[i/o/max] = 4.588ns/0.912ns/16.527ns  area = 526luts + 54ff
  *
  * 55nm@bf16-6cyc: delay[i/o/max] = 1.7911ns/0.4508ns/4.1308ns  area = 11165.56um²
  *
  * @param inFmt  The format of the operand
  * @param outFmt The format of the result
  * @param mul    The combinational unsigned multiplier factory
  * @param policy The numeric policy
  */
class FpPipelinedNewtonReciprocal(
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

  override def latency: Int = 2 + 2 * iters

  io.in.ready := !io.flush
  val fire = io.in.valid && io.in.ready

  val a       = FpUtils.decode(io.in.bits.src, inFmt)
  val (m, e)  = FpUnaryUtil.normalize(a, inFmt)
  val seedVec = VecInit(table.map(_.U((seedFrac + 2).W)))
  val index   = m(inFmt.manWidth - 1, inFmt.manWidth - k)

  val in = Wire(new FpNewtonReciprocalPayload(W))
  in.x    := (m << (P - inFmt.manWidth)).pad(W)
  in.y    := (seedVec(index) << (P - seedFrac - 1)).pad(W)
  in.t    := 0.U
  in.sign := a.sign
  in.exp  := e
  in.rm   := io.in.bits.rm
  in.nan  := a.isNaN
  in.inf  := a.isInf
  in.zero := a.isZero

  private def pipe[T <: Data](gen: T, p: T, v: Bool): (T, Bool) = {
    val s = Module(new FpPipeStage(gen))
    s.io.in      := p
    s.io.inValid := v
    s.io.flush   := io.flush
    (s.io.out, s.io.outValid)
  }

  var p        = in
  var v        = fire
  val (p0, v0) = pipe(new FpNewtonReciprocalPayload(W), p, v)
  p = p0
  v = v0

  for (_ <- 0 until iters) {
    val pa = Wire(new FpNewtonReciprocalPayload(W))
    pa   := p
    pa.t := (p.x * p.y) >> P
    val (p1, v1) = pipe(new FpNewtonReciprocalPayload(W), pa, v)

    val pb = Wire(new FpNewtonReciprocalPayload(W))
    pb   := p1
    pb.y := (p1.y * ((BigInt(2) << P).U(W.W) - p1.t)) >> P
    val (p2, v2) = pipe(new FpNewtonReciprocalPayload(W), pb, v1)
    p = p2
    v = v2
  }

  val packed  = FpUtils.roundPack(p.sign, p.y, -P.S(FpUtils.EW.W) - p.exp, outFmt, p.rm, policy)
  val special = p.nan || p.inf || p.zero

  val out = Wire(new FpUnaryResp(outFmt))
  out.output := Mux(
    p.nan,
    outFmt.canonicalNaN,
    Mux(
      p.inf,
      outFmt.zero(p.sign),
      Mux(p.zero, Cat(p.sign, outFmt.infinityMag), packed.bits),
    ),
  )
  out.fflags.nx := Mux(special, false.B, packed.nx)
  out.fflags.uf := Mux(special, false.B, packed.uf)
  out.fflags.of := Mux(special, false.B, packed.of)
  out.fflags.dz := p.zero
  out.fflags.nv := false.B

  val (o, ov) = pipe(new FpUnaryResp(outFmt), out, v)
  io.out.valid := ov
  io.out.bits  := o
}

object FpPipelinedNewtonReciprocalFp64 extends App {
  ExportForAnalysis(
    new FpPipelinedNewtonReciprocal(FpFormat.Fp64, FpFormat.Fp64, w => new UIntMacroMul(w)),
    args,
    FpExport.opts,
  )
}

object FpPipelinedNewtonReciprocalFp32 extends App {
  ExportForAnalysis(
    new FpPipelinedNewtonReciprocal(FpFormat.Fp32, FpFormat.Fp32, w => new UIntMacroMul(w)),
    args,
    FpExport.opts,
  )
}

object FpPipelinedNewtonReciprocalFp16 extends App {
  ExportForAnalysis(
    new FpPipelinedNewtonReciprocal(FpFormat.Fp16, FpFormat.Fp16, w => new UIntMacroMul(w)),
    args,
    FpExport.opts,
  )
}

object FpPipelinedNewtonReciprocalBf16 extends App {
  ExportForAnalysis(
    new FpPipelinedNewtonReciprocal(FpFormat.Bf16, FpFormat.Bf16, w => new UIntMacroMul(w)),
    args,
    FpExport.opts,
  )
}
