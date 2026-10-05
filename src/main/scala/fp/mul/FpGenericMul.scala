package harith.fp

import chisel3._
import chisel3.util._

/**
  * A floating-point multiplier built from an exact significand product and a single rounding step.
  *
  * The significand multiplier is supplied as a [[FpSigMul]] strategy, so the same floating-point
  * wrapping can be paired with different `harith.uint` multiplier datapaths. NaN is canonical, per
  * RISC-V.
  *
  * @param aFmt   The format of the first operand
  * @param bFmt   The format of the second operand
  * @param outFmt The format of the result
  * @param policy The numeric policy
  * @param sigMul The significand multiplier strategy
  */
class FpMulImpl(
    val aFmt:   FpFormat,
    val bFmt:   FpFormat,
    val outFmt: FpFormat,
    policy:     FpPolicy,
    sigMul:     FpSigMul,
) extends FpMul {
  val io = IO(new FpMulIO(aFmt, bFmt, outFmt))

  val a = FpUtils.decode(io.src1, aFmt)
  val b = FpUtils.decode(io.src2, bFmt)

  val aZero = if (policy.daz) a.isZero || a.isSubnormal else a.isZero
  val bZero = if (policy.daz) b.isZero || b.isSubnormal else b.isZero

  val sign  = a.sign ^ b.sign
  val prod  = sigMul(a.sig, b.sig)
  val eProd = a.exp + b.exp - (aFmt.manWidth + bFmt.manWidth).S(FpUtils.EW.W)

  val rounded = FpUtils.roundPack(sign, prod, eProd, outFmt, io.rm, policy)

  val nanCase  = a.isNaN || b.isNaN || (a.isInf && bZero) || (aZero && b.isInf)
  val infCase  = !nanCase && (a.isInf || b.isInf)
  val zeroCase = !nanCase && !infCase && (aZero || bZero)
  val special  = nanCase || infCase || zeroCase
  val invalid  = (a.isInf && bZero) || (aZero && b.isInf)

  io.output := Mux(
    nanCase,
    outFmt.canonicalNaN,
    Mux(
      infCase,
      Cat(sign, outFmt.infinityMag),
      Mux(zeroCase, outFmt.zero(sign), rounded.bits),
    ),
  )

  io.fflags.nx := Mux(special, false.B, rounded.nx)
  io.fflags.uf := Mux(special, false.B, rounded.uf)
  io.fflags.of := Mux(special, false.B, rounded.of)
  io.fflags.dz := false.B
  io.fflags.nv := invalid
}

/**
  * A floating-point multiplier with an inferred significand multiplier.
  *
  * delay = 28.951, area = 1065 @32bit@fpga
  * delay = 12.0162, area = 11671.24 @32bit@55nm
  */
class FpGenericMul(aFmt: FpFormat, bFmt: FpFormat, outFmt: FpFormat, policy: FpPolicy = FpPolicy())
    extends FpMulImpl(aFmt, bFmt, outFmt, policy, FpSigMulGeneric)

/**
  * A floating-point multiplier with an array significand multiplier.
  *
  * delay = 36.003, area = 2319 @32bit@fpga
  * delay = 13.2657, area = 11438.56 @32bit@55nm
  */
class FpArrayMul(aFmt: FpFormat, bFmt: FpFormat, outFmt: FpFormat, policy: FpPolicy = FpPolicy())
    extends FpMulImpl(aFmt, bFmt, outFmt, policy, FpSigMulArray)

/**
  * A floating-point multiplier with a Booth tree significand multiplier.
  *
  * delay = 30.310, area = 2378 @32bit@fpga
  * delay = 12.1188, area = 12647.88 @32bit@55nm
  */
class FpBoothMul(aFmt: FpFormat, bFmt: FpFormat, outFmt: FpFormat, policy: FpPolicy = FpPolicy())
    extends FpMulImpl(aFmt, bFmt, outFmt, policy, FpSigMulBooth)

/**
  * A floating-point multiplier with an AND partial product tree significand multiplier.
  *
  * delay = 29.481, area = 1841 @32bit@fpga
  * delay = 11.0304, area = 8116.64 @32bit@55nm
  */
class FpTreeMul(aFmt: FpFormat, bFmt: FpFormat, outFmt: FpFormat, policy: FpPolicy = FpPolicy())
    extends FpMulImpl(aFmt, bFmt, outFmt, policy, FpSigMulTree)

/**
  * A bfloat16 by bfloat16 to float32 multiplier.
  */
class FpBf16Fp32Mul extends FpGenericMul(FpFormat.Bf16, FpFormat.Bf16, FpFormat.Fp32)

/**
  * A float16 by float16 to float32 multiplier.
  */
class Fp16Fp32Mul extends FpGenericMul(FpFormat.Fp16, FpFormat.Fp16, FpFormat.Fp32)

/**
  * A float32 multiplier.
  */
class Fp32Mul extends FpGenericMul(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32)

/**
  * A float64 multiplier.
  */
class Fp64Mul extends FpGenericMul(FpFormat.Fp64, FpFormat.Fp64, FpFormat.Fp64)
