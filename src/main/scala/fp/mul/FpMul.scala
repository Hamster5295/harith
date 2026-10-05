package harith.fp

import chisel3._
import chisel3.util._
import harith.uint._

/**
  * The ports of a [[FpMul]].
  *
  * @param a   The format of the first operand
  * @param b   The format of the second operand
  * @param out The format of the result
  */
class FpMulIO(a: FpFormat, b: FpFormat, out: FpFormat) extends Bundle {
  val src1   = Input(UInt(a.width.W))
  val src2   = Input(UInt(b.width.W))
  val rm     = Input(UInt(3.W))
  val output = Output(UInt(out.width.W))
  val fflags = Output(new FpFlags)
}

/**
  * The common interface of the floating-point multipliers.
  *
  * Every implementation computes the product of `src1` and `src2`, rounds it to `output` using the
  * RISC-V rounding mode `rm`, and reports the IEEE-754 exceptions. The operand and result formats
  * are independent parameters, so mixed-precision products are expressed without extra code.
  */
trait FpMul extends Module {
  val io: FpMulIO

  /**
    * Result latency in clock cycles, where 0 marks a purely combinational multiplier.
    */
  def latency: Int = 0
}

/**
  * The shared combinational floating-point wrapping around a significand multiplier.
  *
  * The significand multiplier is built by `mulFactory`, which always returns an existing
  * [[harith.uint.UIntMul]] trait. Subclasses only choose the significand datapath. NaN is canonical,
  * per RISC-V.
  *
  * @param aFmt       The format of the first operand
  * @param bFmt       The format of the second operand
  * @param outFmt     The format of the result
  * @param policy     The numeric policy
  * @param mulFactory The significand multiplier factory
  */
abstract class FpMulBase(
    val aFmt:   FpFormat,
    val bFmt:   FpFormat,
    val outFmt: FpFormat,
    policy:     FpPolicy,
    mulFactory: Int => UIntMul,
) extends FpMul {
  val io = IO(new FpMulIO(aFmt, bFmt, outFmt))

  val a = FpUtils.decode(io.src1, aFmt)
  val b = FpUtils.decode(io.src2, bFmt)

  val aZero = if (policy.daz) a.isZero || a.isSubnormal else a.isZero
  val bZero = if (policy.daz) b.isZero || b.isSubnormal else b.isZero

  val sign  = a.sign ^ b.sign
  val w     = math.max(aFmt.manWidth, bFmt.manWidth) + 1
  val mul   = mulFactory(w)
  val prod  = mul.io.output
  val eProd = a.exp + b.exp - (aFmt.manWidth + bFmt.manWidth).S(FpUtils.EW.W)

  mul.io.src1 := a.sig.pad(w)
  mul.io.src2 := b.sig.pad(w)

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
