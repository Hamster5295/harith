package harith.fp

import chisel3._
import chisel3.util._

/**
  * A floating-point fused multiply-adder built from one exact product, one alignment and one
  * rounding step.
  *
  * The addend is aligned to the product with a sticky bit so the single rounding is exact. NaN is
  * canonical, per RISC-V.
  *
  * @param aFmt   The format of the multiplicand
  * @param bFmt   The format of the multiplier
  * @param cFmt   The format of the addend
  * @param outFmt The format of the result
  * @param policy The numeric policy
  */
class FpGenericFma(
    val aFmt:   FpFormat,
    val bFmt:   FpFormat,
    val cFmt:   FpFormat,
    val outFmt: FpFormat,
    policy:     FpPolicy = FpPolicy(),
) extends FpFma {
  val io = IO(new FpFmaIO(aFmt, bFmt, cFmt, outFmt))

  val a = FpUtils.decode(io.src1, aFmt)
  val b = FpUtils.decode(io.src2, bFmt)
  val c = FpUtils.decode(io.add, cFmt)

  val aZero = if (policy.daz) a.isZero || a.isSubnormal else a.isZero
  val bZero = if (policy.daz) b.isZero || b.isSubnormal else b.isZero
  val cZero = if (policy.daz) c.isZero || c.isSubnormal else c.isZero

  // exact product and addend
  val signP = a.sign ^ b.sign
  val prod  = a.sig * b.sig
  val x     = a.exp + b.exp - (aFmt.manWidth + bFmt.manWidth).S(FpUtils.EW.W)
  val csig  = c.sig
  val y     = c.exp - cFmt.manWidth.S(FpUtils.EW.W)

  // align the product and the addend to a common working exponent
  val keep = outFmt.manWidth + 12
  val pP   = FpUtils.msbIndex(prod)
  val pC   = FpUtils.msbIndex(csig)
  val eP   = x + pP.pad(FpUtils.EW).asSInt
  val eC   = y + pC.pad(FpUtils.EW).asSInt
  val eW   = Mux(eP > eC, eP, eC) - keep.S(FpUtils.EW.W)

  val (psh, pst) = FpUtils.alignToExp(prod, x, eW, keep)
  val (csh, cst) = FpUtils.alignToExp(csig, y, eW, keep)

  val aw    = math.max(psh.getWidth, csh.getWidth) + 1
  val total = Mux(signP, -psh.pad(aw), psh.pad(aw)) + Mux(c.sign, -csh.pad(aw), csh.pad(aw))
  val tSign = total(aw - 1)
  val tMag  = Mux(tSign, (-total).asUInt, total.asUInt)

  val general = FpUtils.roundPack(tSign, tMag, eW, outFmt, io.rm, policy, pst || cst)
  val cRound  = FpUtils.roundPack(c.sign, csig, y, outFmt, io.rm, policy)
  val pRound  = FpUtils.roundPack(signP, prod, x, outFmt, io.rm, policy)

  // special cases
  val prodInf  = !aZero && !bZero && (a.isInf || b.isInf)
  val invalid  = (a.isInf && bZero) || (aZero && b.isInf)
  val infMinus = prodInf && c.isInf && (signP =/= c.sign)
  val nanCase  = a.isNaN || b.isNaN || c.isNaN || invalid || infMinus
  val infCase  = !nanCase && (prodInf || c.isInf)
  val infSign  = Mux(prodInf, signP, c.sign)

  val prodZero = !nanCase && !infCase && (aZero || bZero)
  val bothZero = prodZero && cZero
  val mulZero  = prodZero && !cZero
  val addZero  = !nanCase && !infCase && !prodZero && cZero

  val zeroSign = Mux(signP === c.sign, signP, io.rm === 2.U)

  val isSpecial = nanCase || infCase || bothZero
  val selNx = Mux(isSpecial, false.B, Mux(mulZero, cRound.nx, Mux(addZero, pRound.nx, general.nx)))
  val selUf = Mux(isSpecial, false.B, Mux(mulZero, cRound.uf, Mux(addZero, pRound.uf, general.uf)))
  val selOf = Mux(isSpecial, false.B, Mux(mulZero, cRound.of, Mux(addZero, pRound.of, general.of)))

  io.output := Mux(
    nanCase,
    outFmt.canonicalNaN,
    Mux(
      infCase,
      Cat(infSign, outFmt.infinityMag),
      Mux(
        bothZero,
        outFmt.zero(zeroSign),
        Mux(mulZero, cRound.bits, Mux(addZero, pRound.bits, general.bits)),
      ),
    ),
  )

  io.fflags.nx := selNx
  io.fflags.uf := selUf
  io.fflags.of := selOf
  io.fflags.dz := false.B
  io.fflags.nv := invalid || infMinus
}

/**
  * A bfloat16 by bfloat16 plus float32 to float32 fused multiply-adder.
  */
class FpBf16Fp32Fma extends FpGenericFma(FpFormat.Bf16, FpFormat.Bf16, FpFormat.Fp32, FpFormat.Fp32)

/**
  * A float16 by float16 plus float32 to float32 fused multiply-adder.
  */
class Fp16Fp32Fma extends FpGenericFma(FpFormat.Fp16, FpFormat.Fp16, FpFormat.Fp32, FpFormat.Fp32)

/**
  * A float32 fused multiply-adder.
  */
class Fp32Fma extends FpGenericFma(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32)

/**
  * A float64 fused multiply-adder.
  */
class Fp64Fma extends FpGenericFma(FpFormat.Fp64, FpFormat.Fp64, FpFormat.Fp64, FpFormat.Fp64)
