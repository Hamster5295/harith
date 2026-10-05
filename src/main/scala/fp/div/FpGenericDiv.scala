package harith.fp

import chisel3._
import chisel3.util._

/**
  * A floating-point divider with a restoring significand divider.
  *
  * The significand quotient is computed with `manWidth + 3` extra fractional bits and a sticky from
  * the nonzero remainder, then rounded once with [[FpUtils.roundPack]]. NaN is canonical, per
  * RISC-V.
  *
  * @param aFmt   The format of the dividend
  * @param bFmt   The format of the divisor
  * @param outFmt The format of the result
  * @param policy The numeric policy
  */
class FpGenericDiv(
    val aFmt:   FpFormat,
    val bFmt:   FpFormat,
    val outFmt: FpFormat,
    policy:     FpPolicy = FpPolicy(),
) extends FpDiv {
  val io = IO(new FpDivIO(aFmt, bFmt, outFmt))

  val a = FpUtils.decode(io.src1, aFmt)
  val b = FpUtils.decode(io.src2, bFmt)

  val aZero = if (policy.daz) a.isZero || a.isSubnormal else a.isZero
  val bZero = if (policy.daz) b.isZero || b.isSubnormal else b.isZero

  val sign = a.sign ^ b.sign

  // Normalize both significands so their most significant bit is at position `NW - 1`. The ratio is
  // then in (0.5, 2), so the quotient never needs a large left shift that would amplify the
  // truncation error below the rounding position.
  val NW     = math.max(math.max(aFmt.manWidth, bFmt.manWidth), outFmt.manWidth) + 1
  val shiftA = (NW - 1).U - FpUtils.msbIndex(a.sig)
  val shiftB = (NW - 1).U - FpUtils.msbIndex(b.sig)
  val sigAn  = a.sig << shiftA
  val sigBn  = b.sig << shiftB

  val expAn = a.exp - aFmt.manWidth.S(FpUtils.EW.W) - shiftA.pad(FpUtils.EW).asSInt
  val expBn = b.exp - bFmt.manWidth.S(FpUtils.EW.W) - shiftB.pad(FpUtils.EW).asSInt

  // exact significand division: Q = floor(sigAn * 2^extra / sigBn)
  val extra    = outFmt.manWidth + 3
  val num      = sigAn << extra
  val numWidth = num.getWidth
  val den      = sigBn
  val remW     = sigBn.getWidth + 1

  var rem = 0.U(remW.W)
  var quo = 0.U(numWidth.W)
  for (i <- 0 until numWidth) {
    val idx     = numWidth - 1 - i
    val shifted = Cat(rem, num(idx))
    val denPad  = den.pad(remW + 1)
    val ge      = shifted >= denPad
    rem = Mux(ge, shifted - denPad, shifted)(remW - 1, 0)
    quo = Cat(quo(numWidth - 2, 0), ge)
  }

  val expQ    = expAn - expBn - extra.S(FpUtils.EW.W)
  val sticky  = rem =/= 0.U
  val rounded = FpUtils.roundPack(sign, quo, expQ, outFmt, io.rm, policy, sticky)

  val nanCase = a.isNaN || b.isNaN || (a.isInf && b.isInf) || (aZero && bZero)
  val dzCase  = !nanCase && bZero && !aZero
  val infCase = !nanCase && !dzCase && (a.isInf || b.isInf)
  val infRes  = Cat(sign, outFmt.infinityMag)
  val zeroRes = outFmt.zero(sign)

  val general = !nanCase && !dzCase && !infCase && !aZero && !bZero

  io.output := Mux(
    nanCase,
    outFmt.canonicalNaN,
    Mux(
      dzCase,
      infRes,
      Mux(infCase, Mux(a.isInf, infRes, zeroRes), Mux(aZero, zeroRes, rounded.bits)),
    ),
  )

  io.fflags.nx := Mux(general, rounded.nx, false.B)
  io.fflags.uf := Mux(general, rounded.uf, false.B)
  io.fflags.of := Mux(general, rounded.of, false.B)
  io.fflags.dz := dzCase
  io.fflags.nv := (a.isInf && b.isInf) || (aZero && bZero) || a.isNaN || b.isNaN
}

/**
  * A float32 divider.
  */
class Fp32Div extends FpGenericDiv(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32)

/**
  * A float64 divider.
  */
class Fp64Div extends FpGenericDiv(FpFormat.Fp64, FpFormat.Fp64, FpFormat.Fp64)

/**
  * A float16 by float16 to float32 divider.
  */
class Fp16Fp32Div extends FpGenericDiv(FpFormat.Fp16, FpFormat.Fp16, FpFormat.Fp32)

/**
  * A bfloat16 by bfloat16 to float32 divider.
  */
class FpBf16Fp32Div extends FpGenericDiv(FpFormat.Bf16, FpFormat.Bf16, FpFormat.Fp32)
