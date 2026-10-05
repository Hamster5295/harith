package harith.fp

import chisel3._
import chisel3.util._

/**
  * An IEEE-754 binary floating-point format.
  *
  * A value has `1 + expWidth + manWidth` bits. The exponent bias is the IEEE binary interchange
  * default `2^(expWidth - 1) - 1`, so it is derived rather than stored.
  *
  * @param expWidth The number of exponent bits
  * @param manWidth The number of explicit stored mantissa bits
  */
final case class FpFormat(expWidth: Int, manWidth: Int) {
  require(expWidth >= 2, "an IEEE binary format needs at least 2 exponent bits")
  require(manWidth >= 1, "an IEEE binary format needs at least 1 mantissa bit")

  /** The total number of bits. */
  def width: Int = 1 + expWidth + manWidth

  /** The all ones exponent field. */
  def expMask: BigInt = (BigInt(1) << expWidth) - 1

  /** The all ones mantissa field. */
  def manMask: BigInt = (BigInt(1) << manWidth) - 1

  /** The index of the sign bit. */
  def signBit: Int = width - 1

  /** The exponent bias. */
  def bias: BigInt = (BigInt(1) << (expWidth - 1)) - 1

  /** The largest unbiased normal exponent. */
  def maxExp: BigInt = expMask - 1 - bias

  /** The smallest unbiased normal exponent. */
  def minExp: BigInt = 1 - bias

  /** The smallest unbiased subnormal exponent. */
  def minSubExp: BigInt = minExp - manWidth

  /**
    * The signed zero bit pattern.
    *
    * @param sign The sign of the zero
    * @return the bit pattern
    */
  def zero(sign: Bool): UInt = Cat(sign, 0.U((width - 1).W))

  /** The positive infinity bit pattern. */
  def infinity: UInt = Cat(0.B, expMask.U(expWidth.W), 0.U(manWidth.W))

  /** The unsigned infinity magnitude bit pattern. */
  def infinityMag: UInt = Cat(expMask.U(expWidth.W), 0.U(manWidth.W))

  /** The canonical quiet NaN bit pattern mandated by RISC-V. */
  def canonicalNaN: UInt =
    Cat(0.B, expMask.U(expWidth.W), (BigInt(1) << (manWidth - 1)).U(manWidth.W))

  /** The largest finite magnitude bit pattern. */
  def maxFiniteMag: UInt = Cat((expMask - 1).U(expWidth.W), manMask.U(manWidth.W))

  /** The largest finite magnitude with a positive sign. */
  def maxFinite: UInt = Cat(0.B, maxFiniteMag)
}

/**
  * The regular floating-point formats supported by the scalar `fp` package.
  */
object FpFormat {
  val Fp64 = FpFormat(11, 52)
  val Fp32 = FpFormat(8, 23)
  val Tf32 = FpFormat(8, 10)
  val Fp16 = FpFormat(5, 10)
  val Bf16 = FpFormat(8, 7)
}
