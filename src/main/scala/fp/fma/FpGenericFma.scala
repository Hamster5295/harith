package harith.fp

import chisel3._
import chisel3.util._

/**
  * A floating-point fused multiply-adder built from one exact product, one alignment and one
  * rounding step.
  *
  * The significand multiplier and the wide alignment adder are supplied as strategies, so the same
  * floating-point wrapping can be paired with different `harith.uint` datapaths. NaN is canonical,
  * per RISC-V.
  *
  * @param aFmt   The format of the multiplicand
  * @param bFmt   The format of the multiplier
  * @param cFmt   The format of the addend
  * @param outFmt The format of the result
  * @param policy The numeric policy
  * @param sigMul The significand multiplier strategy
  * @param sigAdd The wide alignment adder strategy
  */
class FpFmaImpl(
    val aFmt:   FpFormat,
    val bFmt:   FpFormat,
    val cFmt:   FpFormat,
    val outFmt: FpFormat,
    policy:     FpPolicy,
    sigMul:     FpSigMul,
    sigAdd:     FpSigAdd,
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
  val prod  = sigMul(a.sig, b.sig)
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

  val w     = math.max(psh.getWidth, csh.getWidth)
  val pVal  = Mux(signP, -psh.pad(w), psh.pad(w))
  val cVal  = Mux(c.sign, -csh.pad(w), csh.pad(w))
  val total = sigAdd(pVal, cVal)
  val tSign = total(w)
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
  * A floating-point fused multiply-adder with inferred significand and alignment datapaths.
  *
  * delay = 53.886, area = 5122 @32bit@fpga
  * delay = 51.6054, area = 26082.28 @32bit@55nm
  */
class FpGenericFma(
    aFmt:   FpFormat,
    bFmt:   FpFormat,
    cFmt:   FpFormat,
    outFmt: FpFormat,
    policy: FpPolicy = FpPolicy(),
) extends FpFmaImpl(aFmt, bFmt, cFmt, outFmt, policy, FpSigMulGeneric, FpSigAddGeneric)

/**
  * A floating-point fused multiply-adder with an array significand multiplier.
  *
  * delay = 60.294, area = 6427 @32bit@fpga
  * delay = 37.9481, area = 27385.96 @32bit@55nm
  */
class FpArrayFma(
    aFmt:   FpFormat,
    bFmt:   FpFormat,
    cFmt:   FpFormat,
    outFmt: FpFormat,
    policy: FpPolicy = FpPolicy(),
) extends FpFmaImpl(aFmt, bFmt, cFmt, outFmt, policy, FpSigMulArray, FpSigAddGeneric)

/**
  * A floating-point fused multiply-adder with a Booth tree significand multiplier.
  *
  * delay = 54.714, area = 6460 @32bit@fpga
  * delay = 35.4359, area = 30400.16 @32bit@55nm
  */
class FpBoothFma(
    aFmt:   FpFormat,
    bFmt:   FpFormat,
    cFmt:   FpFormat,
    outFmt: FpFormat,
    policy: FpPolicy = FpPolicy(),
) extends FpFmaImpl(aFmt, bFmt, cFmt, outFmt, policy, FpSigMulBooth, FpSigAddGeneric)

/**
  * A floating-point fused multiply-adder with an AND partial product tree significand multiplier.
  *
  * delay = 54.715, area = 5948 @32bit@fpga
  * delay = 45.5888, area = 28058.80 @32bit@55nm
  */
class FpTreeFma(
    aFmt:   FpFormat,
    bFmt:   FpFormat,
    cFmt:   FpFormat,
    outFmt: FpFormat,
    policy: FpPolicy = FpPolicy(),
) extends FpFmaImpl(aFmt, bFmt, cFmt, outFmt, policy, FpSigMulTree, FpSigAddGeneric)

/**
  * A floating-point fused multiply-adder with a ripple carry alignment adder.
  *
  * delay = 67.529, area = 5380 @32bit@fpga
  * delay = 40.9942, area = 30536.52 @32bit@55nm
  */
class FpRippleFma(
    aFmt:   FpFormat,
    bFmt:   FpFormat,
    cFmt:   FpFormat,
    outFmt: FpFormat,
    policy: FpPolicy = FpPolicy(),
) extends FpFmaImpl(aFmt, bFmt, cFmt, outFmt, policy, FpSigMulGeneric, FpSigAddRipple)

/**
  * A floating-point fused multiply-adder with a parallel prefix alignment adder.
  *
  * delay = 54.672, area = 6155 @32bit@fpga
  * delay = 45.4307, area = 26786.48 @32bit@55nm
  */
class FpPrefixFma(
    aFmt:   FpFormat,
    bFmt:   FpFormat,
    cFmt:   FpFormat,
    outFmt: FpFormat,
    policy: FpPolicy = FpPolicy(),
) extends FpFmaImpl(aFmt, bFmt, cFmt, outFmt, policy, FpSigMulGeneric, FpSigAddPrefix)

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
