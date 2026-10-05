package harith.fp

import chisel3._
import chisel3.util._
import harith.uint._

/**
  * The ports of a [[FpFma]].
  *
  * @param a   The format of the multiplicand
  * @param b   The format of the multiplier
  * @param c   The format of the addend
  * @param out The format of the result
  */
class FpFmaIO(a: FpFormat, b: FpFormat, c: FpFormat, out: FpFormat) extends Bundle {
  val src1   = Input(UInt(a.width.W))
  val src2   = Input(UInt(b.width.W))
  val add    = Input(UInt(c.width.W))
  val rm     = Input(UInt(3.W))
  val output = Output(UInt(out.width.W))
  val fflags = Output(new FpFlags)
}

/**
  * The common interface of the floating-point fused multiply-adders.
  *
  * Every implementation computes `src1 * src2 + add` with a single rounding to `output` using the
  * RISC-V rounding mode `rm`, and reports the IEEE-754 exceptions. The operand and result formats
  * are independent parameters, so mixed-precision FMAs are expressed without extra code.
  */
trait FpFma extends Module {
  val io: FpFmaIO

  /**
    * Result latency in clock cycles, where 0 marks a purely combinational FMA.
    */
  def latency: Int = 0
}

/**
  * The shared combinational floating-point wrapping around a significand multiplier and a wide
  * alignment adder.
  *
  * The significand multiplier and the alignment adder are built by `mulFactory` and `addFactory`,
  * which always return the existing [[harith.uint.UIntMul]] and [[harith.uint.UIntAdd]] traits. NaN
  * is canonical, per RISC-V.
  *
  * @param aFmt       The format of the multiplicand
  * @param bFmt       The format of the multiplier
  * @param cFmt       The format of the addend
  * @param outFmt     The format of the result
  * @param policy     The numeric policy
  * @param mulFactory The significand multiplier factory
  * @param addFactory The wide alignment adder factory
  */
abstract class FpFmaBase(
    val aFmt:   FpFormat,
    val bFmt:   FpFormat,
    val cFmt:   FpFormat,
    val outFmt: FpFormat,
    policy:     FpPolicy,
    mulFactory: Int => UIntMul,
    addFactory: Int => UIntAdd,
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
  val mw    = math.max(aFmt.manWidth, bFmt.manWidth) + 1
  val mul   = mulFactory(mw)
  val prod  = mul.io.output
  val x     = a.exp + b.exp - (aFmt.manWidth + bFmt.manWidth).S(FpUtils.EW.W)
  val csig  = c.sig
  val y     = c.exp - cFmt.manWidth.S(FpUtils.EW.W)

  mul.io.src1 := a.sig.pad(mw)
  mul.io.src2 := b.sig.pad(mw)

  // align the product and the addend to a common working exponent
  val keep = outFmt.manWidth + 12
  val pP   = FpUtils.msbIndex(prod)
  val pC   = FpUtils.msbIndex(csig)
  val eP   = x + pP.pad(FpUtils.EW).asSInt
  val eC   = y + pC.pad(FpUtils.EW).asSInt
  val eW   = Mux(eP > eC, eP, eC) - keep.S(FpUtils.EW.W)

  val (psh, pst) = FpUtils.alignToExp(prod, x, eW, keep)
  val (csh, cst) = FpUtils.alignToExp(csig, y, eW, keep)

  val aw    = math.max(psh.getWidth, csh.getWidth)
  val pVal  = Mux(signP, -psh.pad(aw), psh.pad(aw))
  val cVal  = Mux(c.sign, -csh.pad(aw), csh.pad(aw))
  val adder = addFactory(aw + 1)
  val total = FpAddUtil.sum(adder)(pVal, cVal)
  val tSign = total(aw)
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
