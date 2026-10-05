package harith.fp

import chisel3._
import chisel3.util._

/**
  * A floating-point adder built from one alignment and one rounding step.
  *
  * The wide alignment adder is supplied as a strategy, so the same floating-point wrapping can be
  * paired with different `harith.uint` adder datapaths. NaN is canonical, per RISC-V.
  *
  * @param aFmt   The format of the first operand
  * @param bFmt   The format of the second operand
  * @param outFmt The format of the result
  * @param policy The numeric policy
  * @param sigAdd The wide alignment adder strategy
  */
class FpAddImpl(
    val aFmt:   FpFormat,
    val bFmt:   FpFormat,
    val outFmt: FpFormat,
    policy:     FpPolicy,
    sigAdd:     FpSigAdd,
) extends FpAdd {
  val io = IO(new FpAddIO(aFmt, bFmt, outFmt))

  val a = FpUtils.decode(io.src1, aFmt)
  val b = FpUtils.decode(io.src2, bFmt)

  val aZero = if (policy.daz) a.isZero || a.isSubnormal else a.isZero
  val bZero = if (policy.daz) b.isZero || b.isSubnormal else b.isZero

  val expA = a.exp - aFmt.manWidth.S(FpUtils.EW.W)
  val expB = b.exp - bFmt.manWidth.S(FpUtils.EW.W)

  val keep = outFmt.manWidth + 12
  val eA   = expA + FpUtils.msbIndex(a.sig).pad(FpUtils.EW).asSInt
  val eB   = expB + FpUtils.msbIndex(b.sig).pad(FpUtils.EW).asSInt
  val eW   = Mux(eA > eB, eA, eB) - keep.S(FpUtils.EW.W)

  val (ash, ast) = FpUtils.alignToExp(a.sig, expA, eW, keep)
  val (bsh, bst) = FpUtils.alignToExp(b.sig, expB, eW, keep)

  val w     = math.max(ash.getWidth, bsh.getWidth)
  val aVal  = Mux(a.sign, -ash.pad(w), ash.pad(w))
  val bVal  = Mux(b.sign, -bsh.pad(w), bsh.pad(w))
  val total = sigAdd(aVal, bVal)
  val tSign = total(w)
  val tMag  = Mux(tSign, (-total).asUInt, total.asUInt)

  val general = FpUtils.roundPack(tSign, tMag, eW, outFmt, io.rm, policy, ast || bst)
  val aRound  = FpUtils.roundPack(a.sign, a.sig, expA, outFmt, io.rm, policy)
  val bRound  = FpUtils.roundPack(b.sign, b.sig, expB, outFmt, io.rm, policy)

  val oppositeInf = a.isInf && b.isInf && (a.sign =/= b.sign)
  val nanCase     = a.isNaN || b.isNaN || oppositeInf
  val infCase     = !nanCase && (a.isInf || b.isInf)
  val infSign     = Mux(a.isInf, a.sign, b.sign)

  val bothZero  = !nanCase && !infCase && aZero && bZero
  val aOnlyZero = !nanCase && !infCase && aZero && !bZero
  val bOnlyZero = !nanCase && !infCase && !aZero && bZero

  val zeroSign = Mux(a.sign === b.sign, a.sign, io.rm === 2.U)

  val isSpecial = nanCase || infCase || bothZero
  val selNx     =
    Mux(isSpecial, false.B, Mux(aOnlyZero, bRound.nx, Mux(bOnlyZero, aRound.nx, general.nx)))
  val selUf =
    Mux(isSpecial, false.B, Mux(aOnlyZero, bRound.uf, Mux(bOnlyZero, aRound.uf, general.uf)))
  val selOf =
    Mux(isSpecial, false.B, Mux(aOnlyZero, bRound.of, Mux(bOnlyZero, aRound.of, general.of)))

  io.output := Mux(
    nanCase,
    outFmt.canonicalNaN,
    Mux(
      infCase,
      Cat(infSign, outFmt.infinityMag),
      Mux(
        bothZero,
        outFmt.zero(zeroSign),
        Mux(aOnlyZero, bRound.bits, Mux(bOnlyZero, aRound.bits, general.bits)),
      ),
    ),
  )

  io.fflags.nx := selNx
  io.fflags.uf := selUf
  io.fflags.of := selOf
  io.fflags.dz := false.B
  io.fflags.nv := oppositeInf
}

/**
  * A floating-point adder with an inferred alignment adder.
  *
  * delay = 43.206, area = 3923 @32bit@fpga
  * delay = 14.3314, area = 7254.24 @32bit@55nm
  */
class FpGenericAdd(aFmt: FpFormat, bFmt: FpFormat, outFmt: FpFormat, policy: FpPolicy = FpPolicy())
    extends FpAddImpl(aFmt, bFmt, outFmt, policy, FpSigAddGeneric)

/**
  * A floating-point adder with a ripple carry alignment adder, the cheapest option.
  *
  * delay = 55.508, area = 4066 @32bit@fpga
  * delay = 17.0334, area = 7073.08 @32bit@55nm
  */
class FpRippleAdd(aFmt: FpFormat, bFmt: FpFormat, outFmt: FpFormat, policy: FpPolicy = FpPolicy())
    extends FpAddImpl(aFmt, bFmt, outFmt, policy, FpSigAddRipple)

/**
  * A floating-point adder with a parallel prefix alignment adder, the fast option.
  *
  * delay = 45.192, area = 4818 @32bit@fpga
  * delay = 15.6692, area = 6928.88 @32bit@55nm
  */
class FpPrefixAdd(aFmt: FpFormat, bFmt: FpFormat, outFmt: FpFormat, policy: FpPolicy = FpPolicy())
    extends FpAddImpl(aFmt, bFmt, outFmt, policy, FpSigAddPrefix)

/**
  * A floating-point adder with a block carry select alignment adder.
  *
  * delay = 48.919, area = 4194 @32bit@fpga
  * delay = 21.5805, area = 7995.12 @32bit@55nm
  */
class FpCarrySelectAdd(
    aFmt:   FpFormat,
    bFmt:   FpFormat,
    outFmt: FpFormat,
    policy: FpPolicy = FpPolicy(),
) extends FpAddImpl(aFmt, bFmt, outFmt, policy, FpSigAddCarrySelect)

/**
  * A floating-point adder with a hierarchical carry lookahead alignment adder.
  *
  * delay = 45.835, area = 4250 @32bit@fpga
  * delay = 14.0796, area = 7059.92 @32bit@55nm
  */
class FpCarryLookaheadAdd(
    aFmt:   FpFormat,
    bFmt:   FpFormat,
    outFmt: FpFormat,
    policy: FpPolicy = FpPolicy(),
) extends FpAddImpl(aFmt, bFmt, outFmt, policy, FpSigAddCarryLookahead)

/**
  * A bfloat16 plus float32 to float32 adder.
  */
class FpBf16Fp32Add extends FpGenericAdd(FpFormat.Bf16, FpFormat.Fp32, FpFormat.Fp32)

/**
  * A float16 plus float32 to float32 adder.
  */
class Fp16Fp32Add extends FpGenericAdd(FpFormat.Fp16, FpFormat.Fp32, FpFormat.Fp32)

/**
  * A float32 adder.
  */
class Fp32Add extends FpGenericAdd(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32)

/**
  * A float64 adder.
  */
class Fp64Add extends FpGenericAdd(FpFormat.Fp64, FpFormat.Fp64, FpFormat.Fp64)
