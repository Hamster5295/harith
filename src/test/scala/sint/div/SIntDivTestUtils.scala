package harith.sint

import chisel3._
import chisel3.simulator.PeekPokeAPI._

/**
  * Shared, per-module test helpers for the `SIntDiv` family.
  */
object SIntDivTestUtils {

  /**
    * The operand widths exercised by the specs.
    */
  val widths: Seq[Int] = Seq(1, 2, 4, 8, 13)

  /**
    * Corner cases around zero, one, the operand minimum and the operand maximum.
    *
    * @param width The operand width
    * @return the edge case `(dividend, divisor)` vectors
    */
  def edgeVectors(width: Int): Seq[(BigInt, BigInt)] = {
    val max = (BigInt(1) << (width - 1)) - 1
    val min = -(BigInt(1) << (width - 1))
    val one = if (max >= 1) BigInt(1) else max
    Seq(
      (BigInt(0), BigInt(0)),
      (BigInt(0), one),
      (one, BigInt(0)),
      (one, one),
      (BigInt(-1), one),
      (one, BigInt(-1)),
      (BigInt(-1), BigInt(-1)),
      (max, one),
      (min, one),
      (min, BigInt(-1)),
      (min, BigInt(0)),
      (max, max),
      (min, min),
      (min, max),
    )
  }

  /**
    * Reproducible pseudorandom operand vectors.
    *
    * @param width The operand width
    * @param seed  The random seed
    * @param count The number of vectors to generate
    * @return the random `(dividend, divisor)` vectors
    */
  def randomVectors(width: Int, seed: Int, count: Int = 12): Seq[(BigInt, BigInt)] = {
    val random = new scala.util.Random(seed)
    Seq.fill(count)((SIntMulTestUtils.randomSigned(width, random), SIntMulTestUtils.randomSigned(width, random)))
  }

  /**
    * The reference result with the RISC-V signed division semantics.
    *
    * @param dividend The dividend
    * @param divisor  The divisor
    * @param width    The operand width
    * @return the quotient, remainder and divide-by-zero flag
    */
  def reference(dividend: BigInt, divisor: BigInt, width: Int): (BigInt, BigInt, Boolean) = {
    val min = -(BigInt(1) << (width - 1))
    if (divisor == 0) (BigInt(-1), dividend, true)
    else if (dividend == min && divisor == -1) (min, BigInt(0), false)
    else (dividend / divisor, dividend % divisor, false)
  }

  /**
    * Present one request on the decoupled input and wait for a valid response.
    *
    * @param dut      The divider under test
    * @param dividend The dividend
    * @param divisor  The divisor
    */
  def request(dut: SIntDiv, dividend: BigInt, divisor: BigInt): Unit = {
    val width   = dut.io.in.bits.dividend.getWidth
    val timeout = 8 * width + 32

    dut.io.in.valid.poke(true.B)
    dut.io.in.bits.dividend.poke(dividend.S(width.W))
    dut.io.in.bits.divisor.poke(divisor.S(width.W))

    var guard = 0
    while (!dut.io.in.ready.peekBoolean()) {
      dut.clock.step()
      guard += 1
      require(guard <= timeout, "the divider never became ready")
    }
    dut.clock.step()
    dut.io.in.valid.poke(false.B)

    guard = 0
    while (!dut.io.out.valid.peekBoolean()) {
      dut.clock.step()
      guard += 1
      require(guard <= timeout, "the divider never produced a valid response")
    }
  }

  /**
    * Check a divider against the reference for every vector.
    *
    * @param dut     The divider under test
    * @param vectors The `(dividend, divisor)` vectors to apply
    */
  def check(dut: SIntDiv, vectors: Seq[(BigInt, BigInt)]): Unit = {
    val width = dut.io.in.bits.dividend.getWidth
    vectors.foreach { case (dividend, divisor) =>
      request(dut, dividend, divisor)
      val (quotient, remainder, divideByZero) = reference(dividend, divisor, width)
      dut.io.out.bits.quotient.expect(quotient.S(width.W), s"dividend=$dividend divisor=$divisor")
      dut.io.out.bits.remainder.expect(remainder.S(width.W), s"dividend=$dividend divisor=$divisor")
      dut.io.out.bits.divideByZero.expect(divideByZero.B, s"dividend=$dividend divisor=$divisor")
    }
  }

  /**
    * Start a division, abort it with `flush` and check that no response is produced before the
    * divider recovers on a fresh request.
    *
    * @param dut      The divider under test
    * @param dividend The dividend used for the aborted and the recovery division
    * @param divisor  The divisor used for the aborted and the recovery division
    */
  def checkFlush(dut: SIntDiv, dividend: BigInt, divisor: BigInt): Unit = {
    val width = dut.io.in.bits.dividend.getWidth

    dut.io.in.valid.poke(true.B)
    dut.io.in.bits.dividend.poke(dividend.S(width.W))
    dut.io.in.bits.divisor.poke(divisor.S(width.W))
    while (!dut.io.in.ready.peekBoolean()) {
      dut.clock.step()
    }
    dut.clock.step()
    dut.io.in.valid.poke(false.B)

    dut.clock.step(2)
    dut.io.flush.poke(true.B)
    dut.clock.step()
    dut.io.flush.poke(false.B)
    dut.clock.step(2)
    dut.io.out.valid.expect(false.B, "flush must drop the pending response")

    request(dut, dividend, divisor)
    val (quotient, remainder, divideByZero) = reference(dividend, divisor, width)
    dut.io.out.bits.quotient.expect(quotient.S(width.W))
    dut.io.out.bits.remainder.expect(remainder.S(width.W))
    dut.io.out.bits.divideByZero.expect(divideByZero.B)
  }
}
