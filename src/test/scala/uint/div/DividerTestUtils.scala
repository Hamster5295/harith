package harith.uint

import chisel3._
import chisel3.simulator.PeekPokeAPI._

/**
  * Shared, per-module test helpers for the `UIntDivider` family.
  */
object DividerTestUtils {

  /**
    * The operand widths exercised by the per-module specs.
    */
  val widths: Seq[Int] = Seq(1, 2, 4, 8, 13)

  /**
    * Corner cases around zero, one and the operand maximum.
    *
    * @param width The operand width
    * @return the edge case `(dividend, divisor)` vectors
    */
  def edgeVectors(width: Int): Seq[(BigInt, BigInt)] = {
    val max = (BigInt(1) << width) - 1
    Seq(
      (BigInt(0), BigInt(0)),
      (BigInt(0), BigInt(1)),
      (BigInt(1), BigInt(0)),
      (BigInt(1), BigInt(1)),
      (max, BigInt(0)),
      (max, BigInt(1)),
      (BigInt(1), max),
      (max, max),
      (max - 1, max),
      (max / 2, max),
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
    Seq.fill(count)((BigInt(width, random), BigInt(width, random)))
  }

  /**
    * The reference result with the RISC-V divide-by-zero semantics.
    *
    * @param dividend The dividend
    * @param divisor  The divisor
    * @param width    The operand width
    * @return the quotient, remainder and divide-by-zero flag
    */
  def reference(dividend: BigInt, divisor: BigInt, width: Int): (BigInt, BigInt, Boolean) =
    if (divisor == 0) (((BigInt(1) << width) - 1), dividend, true)
    else (dividend / divisor, dividend % divisor, false)

  /**
    * Present one request on the decoupled input and wait for a valid response.
    *
    * @param dut      The divider under test
    * @param dividend The dividend
    * @param divisor  The divisor
    */
  def request(dut: UIntDivider, dividend: BigInt, divisor: BigInt): Unit = {
    val width   = dut.io.in.bits.dividend.getWidth
    val timeout = 8 * width + 32

    dut.io.in.valid.poke(true.B)
    dut.io.in.bits.dividend.poke(dividend.U(width.W))
    dut.io.in.bits.divisor.poke(divisor.U(width.W))

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
  def check(dut: UIntDivider, vectors: Seq[(BigInt, BigInt)]): Unit = {
    val width = dut.io.in.bits.dividend.getWidth
    vectors.foreach { case (dividend, divisor) =>
      request(dut, dividend, divisor)
      val (quotient, remainder, divideByZero) = reference(dividend, divisor, width)
      dut.io.out.bits.quotient.expect(quotient.U(width.W), s"dividend=$dividend divisor=$divisor")
      dut.io.out.bits.remainder.expect(remainder.U(width.W), s"dividend=$dividend divisor=$divisor")
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
  def checkFlush(dut: UIntDivider, dividend: BigInt, divisor: BigInt): Unit = {
    val width = dut.io.in.bits.dividend.getWidth

    dut.io.in.valid.poke(true.B)
    dut.io.in.bits.dividend.poke(dividend.U(width.W))
    dut.io.in.bits.divisor.poke(divisor.U(width.W))
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
    dut.io.out.bits.quotient.expect(quotient.U(width.W))
    dut.io.out.bits.remainder.expect(remainder.U(width.W))
    dut.io.out.bits.divideByZero.expect(divideByZero.B)
  }
}
