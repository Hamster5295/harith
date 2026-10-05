package harith.fp

import chisel3._
import chisel3.util._

/**
  * Shared floating-point datapath helpers.
  *
  * The helpers are format agnostic: operands are decoded into a `(sign, unbiased exponent,
  * significand)` form and results are produced by rounding an exact integer significand with an
  * exact exponent into the requested output format.
  */
private[fp] object FpUtils {

  /** The internal width of unbiased exponents. */
  val EW = 16

  /**
    * An operand decoded into sign, unbiased exponent and integer significand.
    *
    * The magnitude is `sig * 2^(exp - manWidth)`. For a normal operand `sig` carries the implicit
    * leading one; for a subnormal operand `exp` is `format.minExp` and `sig` has a leading zero.
    *
    * @param sign        The sign
    * @param exp         The unbiased exponent
    * @param sig         The integer significand, `manWidth + 1` bits wide
    * @param isZero      Whether the operand is zero
    * @param isSubnormal Whether the operand is subnormal
    * @param isNormal    Whether the operand is normal
    * @param isInf       Whether the operand is infinity
    * @param isNaN       Whether the operand is a NaN
    */
  final case class Unpacked(
      sign:        Bool,
      exp:         SInt,
      sig:         UInt,
      isZero:      Bool,
      isSubnormal: Bool,
      isNormal:    Bool,
      isInf:       Bool,
      isNaN:       Bool,
  )

  /**
    * Decode a bit pattern into the unpacked form.
    *
    * @param bits   The operand bits
    * @param format The operand format
    * @return the unpacked operand
    */
  def decode(bits: UInt, format: FpFormat): Unpacked = {
    val sign    = bits(format.signBit)
    val expF    = bits(format.width - 2, format.manWidth)
    val man     = bits(format.manWidth - 1, 0)
    val expZero = expF === 0.U
    val expAll  = expF === format.expMask.U

    val isZero      = expZero && man === 0.U
    val isSubnormal = expZero && man =/= 0.U
    val isInf       = expAll && man === 0.U
    val isNaN       = expAll && man =/= 0.U
    val isNormal    = !isZero && !isSubnormal && !isInf && !isNaN

    val expField = Mux(expZero, 1.U(format.expWidth.W), expF)
    val exp      = expField.pad(EW).asSInt - format.bias.S(EW.W)
    val sig      = Mux(expZero, man, Cat(1.B, man))

    Unpacked(sign, exp, sig, isZero, isSubnormal, isNormal, isInf, isNaN)
  }

  /**
    * A rounded and packed result together with the exceptions it raised.
    *
    * @param bits The formatted result
    * @param nx   Inexact
    * @param uf   Underflow
    * @param of   Overflow
    */
  final case class Packed(bits: UInt, nx: Bool, uf: Bool, of: Bool)

  /**
    * Round the exact value `(-1)^sign * mant * 2^exp` to `out` and pack it.
    *
    * The mantissa is an arbitrary width unsigned integer and the rounding is performed once,
    * directly at the final quantum, so subnormal results do not suffer a double rounding.
    *
    * @param sign   The sign of the result
    * @param mant   The exact unsigned significand
    * @param exp    The exact exponent of the value `mant * 2^exp`
    * @param out    The output format
    * @param rm     The RISC-V rounding mode
    * @param policy The numeric policy
    * @return the packed result
    */
  def roundPack(
      sign:      Bool,
      mant:      UInt,
      exp:       SInt,
      out:       FpFormat,
      rm:        UInt,
      policy:    FpPolicy,
      extSticky: Bool = false.B,
  ): Packed = {
    val W      = mant.getWidth
    val manOut = out.manWidth
    val minExp = out.minExp.S(EW.W)
    val maxExp = out.maxExp.S(EW.W)

    // Index of the most significant set bit (PriorityEncoder reports the least significant one).
    val p         = (W - 1).U - PriorityEncoder(Reverse(mant))
    val eNorm     = exp + p.pad(EW).asSInt
    val subBranch = eNorm < minExp
    val eOut      = Mux(subBranch, minExp, eNorm)

    // shift = eOut - manOut - exp. Positive shifts discard low bits (with guard/sticky).
    val shift  = eOut - manOut.S(EW.W) - exp
    val tooFar = shift > W.S(EW.W)

    // left path
    val leftAmt   = Mux(shift < 0.S, -shift, 0.S(EW.W))
    val leftClamp = Mux(leftAmt > manOut.S, manOut.S(EW.W), leftAmt)
    val lw        = log2Ceil(manOut + 2)
    val lAmt      = leftClamp.asUInt.apply(lw - 1, 0)
    val qL        = mant << lAmt

    // right path
    val rightS = Mux(tooFar, (W + 1).S(EW.W), Mux(shift > 0.S, shift, 0.S(EW.W)))
    val rw     = log2Ceil(W + 3)
    val sAmt   = rightS.asUInt.apply(rw - 1, 0)
    val qR     = mant >> sAmt
    val lower  = mant & ((1.U << sAmt) - 1.U)

    val guardBit  = (lower >> (sAmt - 1.U))(0)
    val belowMask = (1.U << (sAmt - 1.U)) - 1.U
    val stickyBit = (lower & belowMask) =/= 0.U

    val qRaw   = Mux(shift <= 0.S, qL, qR)
    val guard  = Mux(shift <= 0.S, false.B, Mux(tooFar, false.B, guardBit))
    val sticky = Mux(shift <= 0.S, false.B, Mux(tooFar, mant =/= 0.U, stickyBit))

    val stickyEff = sticky || extSticky
    val inexact   = guard || stickyEff
    val lsb       = qRaw(0)
    val roundUp   = MuxLookup(rm, false.B)(
      Seq(
        0.U -> (guard && (stickyEff || lsb)),   // RNE
        1.U -> false.B,                         // RTZ
        2.U -> ((guard || stickyEff) && sign),  // RDN
        3.U -> ((guard || stickyEff) && !sign), // RUP
        4.U -> guard,                           // RMM
      ),
    )
    val qRounded = qRaw + roundUp
    val qPad     = qRounded.pad(manOut + 2)

    // normal result
    val carryN  = qPad(manOut + 1)
    val qNormal = Mux(carryN, qPad >> 1, qPad)
    val eNormal = eOut + Mux(carryN, 1.S(EW.W), 0.S(EW.W))
    val ovf     = !subBranch && (eNormal > maxExp)

    val normalBits = Cat(
      sign,
      (eNormal + out.bias.S(EW.W)).asUInt.apply(out.expWidth - 1, 0),
      qNormal(manOut - 1, 0),
    )

    // subnormal result, including the round-up to the smallest normal
    val subBits = Mux(
      qPad(manOut),
      Cat(sign, 1.U(out.expWidth.W), 0.U(out.manWidth.W)),
      Cat(sign, 0.U(out.expWidth.W), qPad(manOut - 1, 0)),
    )

    // overflow
    val infMag = Cat(out.expMask.U(out.expWidth.W), 0.U(out.manWidth.W))
    val maxMag = Cat((out.expMask - 1).U(out.expWidth.W), out.manMask.U(out.manWidth.W))
    val ovfMag = MuxLookup(rm, infMag)(
      Seq(
        1.U -> maxMag,                    // RTZ
        2.U -> Mux(sign, infMag, maxMag), // RDN
        3.U -> Mux(sign, maxMag, infMag), // RUP
      ),
    )
    val ovfBits = Cat(sign, ovfMag)

    val bits = Mux(
      qRounded === 0.U,
      out.zero(sign),
      Mux(ovf, ovfBits, Mux(subBranch, subBits, normalBits)),
    )

    val flush = policy.ftz.B && subBranch && !qPad(manOut)
    val uf    = subBranch && !qPad(manOut) && inexact

    Packed(Mux(flush, out.zero(sign), bits), inexact, uf, ovf)
  }

  /**
    * Register `x` through `stages` cycles, preserving its type. A non-positive stage count returns
    * `x` unchanged.
    *
    * @param x      The value to delay
    * @param stages The number of register layers
    * @return the delayed value
    */
  def pipe[T <: Data](x: T, stages: Int): T =
    if (stages <= 0) x else pipe(RegNext(x), stages - 1)

  /** The index of the most significant set bit of `value`. */
  def msbIndex(value: UInt): UInt =
    (value.getWidth - 1).U - PriorityEncoder(Reverse(value))

  /**
    * Shift the exact value `mant * 2^exp` to the working exponent `eW`.
    *
    * A positive difference is a left shift, a negative difference a right shift whose discarded low
    * bits are folded into a sticky bit so that a later rounding is still exact.
    *
    * @param mant The unsigned integer significand
    * @param exp  The exponent of the value `mant * 2^exp`
    * @param eW   The working exponent
    * @param keep The maximum left shift (the number of output bits kept)
    * @return the shifted signed value and the sticky bit
    */
  def alignToExp(mant: UInt, exp: SInt, eW: SInt, keep: Int): (SInt, Bool) = {
    val W     = mant.getWidth
    val s     = exp - eW
    val left  = Mux(s > 0.S, s, 0.S(EW.W))
    val right = Mux(s < 0.S, -s, 0.S(EW.W))
    val lw    = log2Ceil(keep + 2)
    val rw    = log2Ceil(W + 3)
    val lAmt  = Mux(left > keep.S, keep.S(EW.W), left).asUInt.apply(lw - 1, 0)
    val rAmt  = Mux(right > (W + 1).S, (W + 1).S(EW.W), right).asUInt.apply(rw - 1, 0)

    val shl = mant << lAmt
    val shr = mant >> rAmt
    // Zero extend before reinterpreting, so the magnitude stays non-negative.
    val magWidth = math.max(shl.getWidth, shr.getWidth) + 1
    val mag      = Mux(s > 0.S, shl.pad(magWidth), shr.pad(magWidth)).asSInt

    val lower  = mant & ((1.U << rAmt) - 1.U)
    val sticky = Mux(s >= 0.S, false.B, Mux(right > W.S, mant =/= 0.U, lower =/= 0.U))
    (mag, sticky)
  }
}
