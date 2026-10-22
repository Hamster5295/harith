package harith.fp

import chisel3._
import chisel3.simulator.PeekPokeAPI._

/**
  * Shared helpers for the unary floating-point specs.
  *
  * The reference is an exact `BigDecimal` evaluation of the operation followed by a round to
  * nearest even into the target format.
  */
object FpUnaryTestUtils {

  private val mc = new java.math.MathContext(160)

  private def dec(j: java.math.BigDecimal): BigDecimal = BigDecimal(j)

  /** The exact `2^n`. */
  def pow2(n: Int): BigDecimal =
    if (n >= 0) BigDecimal(BigInt(1) << n)
    else BigDecimal(1) / BigDecimal(BigInt(1) << -n)

  /** Decode a finite bit pattern into its exact value. */
  def toValue(bits: BigInt, fmt: FpFormat): BigDecimal = {
    val sign = ((bits >> (fmt.width - 1)) & 1) == 1
    val expF = ((bits >> fmt.manWidth) & fmt.expMask).toInt
    val man  = bits & fmt.manMask
    val (exp, sig) =
      if (expF == 0) (fmt.minExp, man)
      else if (expF == fmt.expMask.toInt) throw new IllegalArgumentException("non-finite")
      else (BigInt(expF) - fmt.bias, man + (BigInt(1) << fmt.manWidth))
    val v = BigDecimal(sig) * pow2(exp.toInt - fmt.manWidth)
    if (sign) -v else v
  }

  private def estimateExponent(v: BigDecimal): Int = {
    val d = v.toDouble
    if (d.isInfinite) 1100
    else if (d == 0.0) -1100
    else math.getExponent(d)
  }

  /** Round a non-negative exact value to `fmt` with round-to-nearest-even. */
  def roundToFormat(value: BigDecimal, fmt: FpFormat, sign: Boolean): BigInt = {
    require(value >= 0, "the magnitude must be non-negative")
    val signBit = if (sign) BigInt(1) << (fmt.width - 1) else BigInt(0)
    if (value == 0) return signBit
    var e = estimateExponent(value)
    while (value < pow2(e)) e -= 1
    while (value >= pow2(e + 1)) e += 1

    val manWidth = fmt.manWidth
    val eUse     = math.max(e, fmt.minExp.toInt)
    val scaled   = dec(value.bigDecimal.divide(pow2(eUse - manWidth).bigDecimal, mc))
    var m        = scaled.setScale(0, BigDecimal.RoundingMode.HALF_EVEN).toBigInt
    var eFinal   = eUse
    if (m >= (BigInt(1) << (manWidth + 1))) {
      m >>= 1
      eFinal += 1
    }

    if (eFinal > fmt.maxExp) {
      signBit | (fmt.expMask << fmt.manWidth)
    } else if (eFinal < fmt.minExp || (eFinal == fmt.minExp && m < (BigInt(1) << manWidth))) {
      val man = m & fmt.manMask
      signBit | man
    } else {
      val expField = BigInt(eFinal) + fmt.bias
      signBit | (expField << manWidth) | (m & fmt.manMask)
    }
  }

  /** The correctly rounded reciprocal. */
  def reciprocalRef(bits: BigInt, inFmt: FpFormat, outFmt: FpFormat): BigInt = {
    val x = toValue(bits, inFmt)
    val v = dec(BigDecimal(1).bigDecimal.divide(x.bigDecimal, mc))
    roundToFormat(v.abs, outFmt, v < 0)
  }

  /** The correctly rounded square root. */
  def sqrtRef(bits: BigInt, inFmt: FpFormat, outFmt: FpFormat): BigInt = {
    val x = toValue(bits, inFmt)
    val v = dec(x.bigDecimal.sqrt(mc))
    roundToFormat(v, outFmt, false)
  }

  /** The correctly rounded reciprocal square root. */
  def rsqrtRef(bits: BigInt, inFmt: FpFormat, outFmt: FpFormat): BigInt = {
    val x = toValue(bits, inFmt)
    val s = dec(x.bigDecimal.sqrt(mc))
    val v = dec(BigDecimal(1).bigDecimal.divide(s.bigDecimal, mc))
    roundToFormat(v, outFmt, false)
  }

  /** The finite positive random operands of `fmt`. */
  def randomPositive(fmt: FpFormat, seed: Int, count: Int): Seq[BigInt] = {
    val rng = new scala.util.Random(seed)
    Seq.fill(count) {
      // A normal positive value with a random exponent and significand.
      val expF = 1 + rng.nextInt((fmt.expMask - 2).toInt)
      val man  = BigInt(fmt.manWidth, rng)
      (BigInt(expF) << fmt.manWidth) | man
    }
  }

  /** Drive one request and wait for the held response. */
  def request(dut: FpUnary, src: BigInt, inFmt: FpFormat): Unit = {
    val timeout = 8 * dut.latency + 32
    dut.io.in.valid.poke(true.B)
    dut.io.in.bits.src.poke(src.U(inFmt.width.W))
    dut.io.in.bits.rm.poke(0.U(3.W))
    var guard = 0
    while (!dut.io.in.ready.peekBoolean()) {
      dut.clock.step()
      guard += 1
      require(guard <= timeout, "the unit never became ready")
    }
    dut.clock.step()
    dut.io.in.valid.poke(false.B)
    guard = 0
    while (!dut.io.out.valid.peekBoolean()) {
      dut.clock.step()
      guard += 1
      require(guard <= timeout, "the unit never produced a valid response")
    }
  }

  /** Check a unary unit against the reference for every operand. */
  def check(
      dut:     FpUnary,
      inFmt:   FpFormat,
      outFmt:  FpFormat,
      vectors: Seq[BigInt],
      ref:     BigInt => BigInt,
  ): Unit =
    vectors.foreach { src =>
      request(dut, src, inFmt)
      dut.io.out.bits.output.expect(ref(src).U(outFmt.width.W), s"src=$src")
    }

  /** Check a faithfully rounded unit (within one unit in the last place). */
  def checkFaithful(
      dut:     FpUnary,
      inFmt:   FpFormat,
      outFmt:  FpFormat,
      vectors: Seq[BigInt],
      ref:     BigInt => BigInt,
  ): Unit =
    vectors.foreach { src =>
      request(dut, src, inFmt)
      val obs = dut.io.out.bits.output.peek().litValue
      val exp = ref(src)
      assert((obs - exp).abs <= 1, s"src=$src observed=$obs expected=$exp")
    }

  /** The zero bit pattern. */
  def zeroBits(fmt: FpFormat, sign: Boolean = false): BigInt =
    if (sign) BigInt(1) << (fmt.width - 1) else BigInt(0)

  /** The infinity bit pattern. */
  def infBits(fmt: FpFormat, sign: Boolean = false): BigInt =
    (if (sign) BigInt(1) << (fmt.width - 1) else BigInt(0)) | (fmt.expMask << fmt.manWidth)

  /** An arbitrary NaN bit pattern. */
  def nanBits(fmt: FpFormat): BigInt = infBits(fmt) | BigInt(1)

  /** The canonical NaN bit pattern used by the library. */
  def canonicalNaNBits(fmt: FpFormat): BigInt =
    infBits(fmt) | (BigInt(1) << (fmt.manWidth - 1))

  /** The bit pattern of `+1.0` or `-1.0`. */
  def oneBits(fmt: FpFormat, sign: Boolean = false): BigInt =
    (if (sign) BigInt(1) << (fmt.width - 1) else BigInt(0)) |
      (fmt.bias << fmt.manWidth)

  /** Check a special operand and its result flags. */
  def checkSpecial(
      dut:      FpUnary,
      src:      BigInt,
      inFmt:    FpFormat,
      outFmt:   FpFormat,
      expected: BigInt,
      dz:       Boolean = false,
      nv:       Boolean = false,
  ): Unit = {
    request(dut, src, inFmt)
    dut.io.out.bits.output.expect(expected.U(outFmt.width.W), s"src=$src")
    dut.io.out.bits.fflags.dz.expect(dz.B)
    dut.io.out.bits.fflags.nv.expect(nv.B)
    dut.io.out.bits.fflags.nx.expect(false.B)
    dut.io.out.bits.fflags.uf.expect(false.B)
    dut.io.out.bits.fflags.of.expect(false.B)
  }
}
