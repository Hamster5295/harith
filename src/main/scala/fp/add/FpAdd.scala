package harith.fp

import chisel3._
import chisel3.util._
import harith.uint._

/**
  * The ports of a [[FpAdd]].
  *
  * @param a   The format of the first operand
  * @param b   The format of the second operand
  * @param out The format of the result
  */
class FpAddIO(a: FpFormat, b: FpFormat, out: FpFormat) extends Bundle {
  val src1   = Input(UInt(a.width.W))
  val src2   = Input(UInt(b.width.W))
  val rm     = Input(UInt(3.W))
  val output = Output(UInt(out.width.W))
  val fflags = Output(new FpFlags)
}

/**
  * The common interface of the floating-point adders.
  *
  * Every implementation computes `src1 + src2`, rounds it to `output` using the RISC-V rounding
  * mode `rm`, and reports the IEEE-754 exceptions. The operand and result formats are independent
  * parameters, so mixed-precision additions are expressed without extra code.
  */
trait FpAdd extends Module {
  val io: FpAddIO

  /**
    * Result latency in clock cycles, where 0 marks a purely combinational adder.
    */
  def latency: Int = 0
}

/**
  * Adapts a signed two-operand sum to an existing [[harith.uint.UIntAdd]].
  */
private[fp] object FpAddUtil {
  def sum(adder: UIntAdd)(x: SInt, y: SInt): SInt = {
    val w = x.getWidth
    adder.io.src1  := x.pad(w + 1).asUInt
    adder.io.src2  := y.pad(w + 1).asUInt
    adder.io.carry := false.B
    adder.io.output(w, 0).asSInt
  }
}

/**
  * The shared combinational floating-point wrapping around a wide alignment adder.
  *
  * The alignment adder is built by `addFactory`, which always returns an existing
  * [[harith.uint.UIntAdd]] trait. The signed sum is adapted to it inside. NaN is canonical, per
  * RISC-V.
  *
  * @param aFmt       The format of the first operand
  * @param bFmt       The format of the second operand
  * @param outFmt     The format of the result
  * @param policy     The numeric policy
  * @param addFactory The wide alignment adder factory
  */
abstract class FpAddBase(
    val aFmt:   FpFormat,
    val bFmt:   FpFormat,
    val outFmt: FpFormat,
    policy:     FpPolicy,
    addFactory: Int => UIntAdd,
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
  val adder = addFactory(w + 1)
  val total = FpAddUtil.sum(adder)(aVal, bVal)
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
