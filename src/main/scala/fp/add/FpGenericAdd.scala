package harith.fp

import chisel3._
import chisel3.util._

/**
  * A floating-point adder built from one alignment and one rounding step.
  *
  * The exponents are aligned with a sticky bit so the single rounding is exact. NaN is canonical,
  * per RISC-V.
  *
  * @param aFmt   The format of the first operand
  * @param bFmt   The format of the second operand
  * @param outFmt The format of the result
  * @param policy The numeric policy
  */
class FpGenericAdd(
    val aFmt:   FpFormat,
    val bFmt:   FpFormat,
    val outFmt: FpFormat,
    policy:     FpPolicy = FpPolicy(),
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

  val aw    = math.max(ash.getWidth, bsh.getWidth) + 1
  val total = Mux(a.sign, -ash.pad(aw), ash.pad(aw)) + Mux(b.sign, -bsh.pad(aw), bsh.pad(aw))
  val tSign = total(aw - 1)
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
