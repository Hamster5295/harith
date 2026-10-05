package harith.fp

import chisel3._
import chisel3.util._

/**
  * A floating-point multiplier built from the exact product and a single rounding step.
  *
  * The significands are multiplied exactly and the exact exponent is passed to
  * [[FpUtils.roundPack]]. NaN is canonical, per RISC-V.
  *
  * @param aFmt   The format of the first operand
  * @param bFmt   The format of the second operand
  * @param outFmt The format of the result
  * @param policy The numeric policy
  */
class FpGenericMul(
    val aFmt:   FpFormat,
    val bFmt:   FpFormat,
    val outFmt: FpFormat,
    policy:     FpPolicy = FpPolicy(),
) extends FpMul {
  val io = IO(new FpMulIO(aFmt, bFmt, outFmt))

  val a = FpUtils.decode(io.src1, aFmt)
  val b = FpUtils.decode(io.src2, bFmt)

  val aZero = if (policy.daz) a.isZero || a.isSubnormal else a.isZero
  val bZero = if (policy.daz) b.isZero || b.isSubnormal else b.isZero

  val sign  = a.sign ^ b.sign
  val prod  = a.sig * b.sig
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
