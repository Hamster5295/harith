package harith.fp

import chisel3._
import chisel3.util._

/**
  * Shared normalization helpers for the unary floating-point operations.
  */
private[fp] object FpUnaryUtil {

  /**
    * Normalize a decoded operand to `[1, 2)`.
    *
    * The returned integer significand `M` is `manWidth + 1` bits wide with the leading one at bit
    * `manWidth`, such that the operand is `(M / 2^manWidth) * 2^E`.
    *
    * @param u      The decoded operand
    * @param inFmt  The operand format
    * @return the normalized significand and the effective exponent
    */
  def normalize(u: FpUtils.Unpacked, inFmt: FpFormat): (UInt, SInt) = {
    val shift = inFmt.manWidth.U - FpUtils.msbIndex(u.sig)
    val m     = (u.sig << shift)(inFmt.manWidth, 0)
    val e     = u.exp - shift.pad(FpUtils.EW).asSInt
    (m, e)
  }

  /**
    * Normalize a decoded operand for a square root.
    *
    * The significand is placed in `[1, 4)` and the exponent is made even, so that
    * `sqrt(x) = sqrt(M / 2^manWidth) * 2^(E / 2)`.
    *
    * @param u     The decoded operand
    * @param inFmt The operand format
    * @return the normalized significand and the even effective exponent
    */
  def normalizeEven(u: FpUtils.Unpacked, inFmt: FpFormat): (UInt, SInt) = {
    val (m, e) = normalize(u, inFmt)
    val odd    = e(0)
    val m2     = Mux(odd, (m << 1)(inFmt.manWidth + 1, 0), m.pad(inFmt.manWidth + 2))
    val e2     = Mux(odd, e - 1.S(FpUtils.EW.W), e)
    (m2, e2)
  }
}
