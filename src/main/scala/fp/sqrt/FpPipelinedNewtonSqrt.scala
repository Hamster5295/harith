package harith.fp

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis
import harith.uint.UIntMacroMul
import harith.uint.UIntMul

/**
  * A pipelined Newton-Raphson square root with a fixed-depth pipeline.
  *
  * The combinational Newton-Raphson chain is split at every multiplier and every result stage, so a
  * new operand can be accepted every cycle. The reciprocal square root and the final multiply are
  * the same as [[FpNewtonSqrt]]; the result is faithfully rounded (within one unit in the last
  * place).
  *
  * fpga@fp64-12cyc: delay[i/o/max] = 8.445ns/0.912ns/25.022ns  area = 3554luts + 733ff
  *
  * 55nm@fp64-12cyc: delay[i/o/max] = 5.6980ns/0.4508ns/8.0459ns  area = 305500.72um²
  *
  * fpga@fp32-12cyc: delay[i/o/max] = 7.048ns/0.912ns/21.793ns  area = 1568luts + 320ff
  *
  * 55nm@fp32-12cyc: delay[i/o/max] = 3.8242ns/0.4508ns/5.9647ns  area = 94503.08um²
  *
  * fpga@fp16-9cyc: delay[i/o/max] = 5.260ns/0.912ns/16.906ns  area = 1049luts + 250ff
  *
  * 55nm@fp16-9cyc: delay[i/o/max] = 2.9117ns/0.4508ns/4.1158ns  area = 25272.24um²
  *
  * fpga@bf16-9cyc: delay[i/o/max] = 6.139ns/0.912ns/16.529ns  area = 601luts + 56ff
  *
  * 55nm@bf16-9cyc: delay[i/o/max] = 2.5437ns/0.4508ns/4.5610ns  area = 18198.60um²
  *
  * @param inFmt  The format of the operand
  * @param outFmt The format of the result
  * @param mul    The combinational unsigned multiplier factory
  * @param policy The numeric policy
  */
class FpPipelinedNewtonSqrt(
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

  override def latency: Int = 3 + 3 * iters

  io.in.ready := !io.flush
  val fire = io.in.valid && io.in.ready

  val a        = FpUtils.decode(io.in.bits.src, inFmt)
  val (m2, e2) = FpUnaryUtil.normalizeEven(a, inFmt)
  val seedVec  = VecInit(table.map(_.U((seedFrac + 1).W)))
  val index    = m2(inFmt.manWidth + 1, inFmt.manWidth - k)

  val in = Wire(new FpNewtonRsqrtPayload(W))
  in.x    := (m2 << (P - inFmt.manWidth)).pad(W)
  in.z    := (seedVec(index) << (P - seedFrac)).pad(W)
  in.t1   := 0.U
  in.t2   := 0.U
  in.sign := a.sign
  in.exp  := e2
  in.rm   := io.in.bits.rm
  in.nan  := a.isNaN
  in.inf  := a.isInf
  in.zero := a.isZero
  in.neg  := a.sign && !a.isZero && !a.isNaN

  private def pipe[T <: Data](gen: T, p: T, v: Bool): (T, Bool) = {
    val s = Module(new FpPipeStage(gen))
    s.io.in      := p
    s.io.inValid := v
    s.io.flush   := io.flush
    (s.io.out, s.io.outValid)
  }

  var p        = in
  var v        = fire
  val (p0, v0) = pipe(new FpNewtonRsqrtPayload(W), p, v)
  p = p0
  v = v0

  for (_ <- 0 until iters) {
    val pa = Wire(new FpNewtonRsqrtPayload(W))
    pa    := p
    pa.t1 := (p.z * p.z) >> P
    val (p1, v1) = pipe(new FpNewtonRsqrtPayload(W), pa, v)

    val pb = Wire(new FpNewtonRsqrtPayload(W))
    pb    := p1
    pb.t2 := (p1.x * p1.t1) >> P
    val (p2, v2) = pipe(new FpNewtonRsqrtPayload(W), pb, v1)

    val pc = Wire(new FpNewtonRsqrtPayload(W))
    pc   := p2
    pc.z := (p2.z * ((BigInt(3) << P).U(W.W) - p2.t2)) >> (P + 1)
    val (p3, v3) = pipe(new FpNewtonRsqrtPayload(W), pc, v2)
    p = p3
    v = v3
  }

  val ps = Wire(new FpNewtonRsqrtPayload(W))
  ps    := p
  ps.t1 := (p.x * p.z) >> P
  val (pRoot, vRoot) = pipe(new FpNewtonRsqrtPayload(W), ps, v)

  val packed = FpUtils.roundPack(
    false.B,
    pRoot.t1,
    (pRoot.exp >> 1) - P.S(FpUtils.EW.W),
    outFmt,
    pRoot.rm,
    policy,
  )

  val out = Wire(new FpUnaryResp(outFmt))
  out.output := Mux(
    pRoot.neg || pRoot.nan,
    outFmt.canonicalNaN,
    Mux(
      pRoot.inf,
      Mux(pRoot.sign, outFmt.canonicalNaN, Cat(false.B, outFmt.infinityMag)),
      Mux(pRoot.zero, outFmt.zero(pRoot.sign), packed.bits),
    ),
  )
  out.fflags.nx := Mux(pRoot.neg || pRoot.nan || pRoot.inf || pRoot.zero, false.B, packed.nx)
  out.fflags.uf := Mux(pRoot.neg || pRoot.nan || pRoot.inf || pRoot.zero, false.B, packed.uf)
  out.fflags.of := Mux(pRoot.neg || pRoot.nan || pRoot.inf || pRoot.zero, false.B, packed.of)
  out.fflags.dz := false.B
  out.fflags.nv := pRoot.neg

  val (o, ov) = pipe(new FpUnaryResp(outFmt), out, vRoot)
  io.out.valid := ov
  io.out.bits  := o
}

object FpPipelinedNewtonSqrtFp64 extends App {
  ExportForAnalysis(
    new FpPipelinedNewtonSqrt(FpFormat.Fp64, FpFormat.Fp64, w => new UIntMacroMul(w)),
    args,
    FpExport.opts,
  )
}

object FpPipelinedNewtonSqrtFp32 extends App {
  ExportForAnalysis(
    new FpPipelinedNewtonSqrt(FpFormat.Fp32, FpFormat.Fp32, w => new UIntMacroMul(w)),
    args,
    FpExport.opts,
  )
}

object FpPipelinedNewtonSqrtFp16 extends App {
  ExportForAnalysis(
    new FpPipelinedNewtonSqrt(FpFormat.Fp16, FpFormat.Fp16, w => new UIntMacroMul(w)),
    args,
    FpExport.opts,
  )
}

object FpPipelinedNewtonSqrtBf16 extends App {
  ExportForAnalysis(
    new FpPipelinedNewtonSqrt(FpFormat.Bf16, FpFormat.Bf16, w => new UIntMacroMul(w)),
    args,
    FpExport.opts,
  )
}
