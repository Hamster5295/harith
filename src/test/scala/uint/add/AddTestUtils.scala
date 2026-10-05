package harith.uint

import chisel3._
import chisel3.simulator.PeekPokeAPI._

/**
  * Shared, per-module test helpers for the `UIntAdd` family.
  */
object AddTestUtils {

  /**
    * The operand widths exercised by the per-module specs.
    */
  val widths: Seq[Int] = Seq(1, 13, 32)

  private def maxValue(width: Int): BigInt = (BigInt(1) << width) - 1

  /**
    * Corner cases around zero, the operand maximum and the carry input.
    *
    * @param width The operand width
    * @return the edge case `(src1, src2, carry)` vectors
    */
  def edgeVectors(width: Int): Seq[(BigInt, BigInt, Boolean)] = {
    val max = maxValue(width)
    Seq(
      (BigInt(0), BigInt(0), false),
      (BigInt(0), BigInt(0), true),
      (max, max, false),
      (max, max, true),
      (max, BigInt(1), false),
      (BigInt(1), max, true),
      (max, BigInt(0), false),
      (BigInt(0), max, true),
      (BigInt(1), BigInt(1), false),
      (max - 1, max, true),
    )
  }

  /**
    * Reproducible pseudorandom operand vectors.
    *
    * @param width The operand width
    * @param seed  The random seed
    * @param count The number of vectors to generate
    * @return the random `(src1, src2, carry)` vectors
    */
  def randomVectors(width: Int, seed: Int, count: Int = 24): Seq[(BigInt, BigInt, Boolean)] = {
    val random = new scala.util.Random(seed)
    Seq.fill(count)(
      (BigInt(width, random), BigInt(width, random), random.nextBoolean()),
    )
  }

  /**
    * The `width + 1` bit reference sum of `a + b + carry`.
    *
    * @param a     The first operand
    * @param b     The second operand
    * @param carry The carry input
    * @param width The operand width
    * @return the truncated `width + 1` bit sum
    */
  def reference(a: BigInt, b: BigInt, carry: Boolean, width: Int): BigInt =
    (a + b + (if (carry) 1 else 0)) & ((BigInt(1) << (width + 1)) - 1)

  /**
    * Check a combinational adder against the reference in the same cycle.
    *
    * @param dut     The adder under test
    * @param vectors The `(src1, src2, carry)` vectors to apply
    */
  def checkCombinational(dut: UIntAdd, vectors: Seq[(BigInt, BigInt, Boolean)]): Unit = {
    val width = dut.io.src1.getWidth
    vectors.foreach { case (a, b, carry) =>
      dut.io.src1.poke(a.U(width.W))
      dut.io.src2.poke(b.U(width.W))
      dut.io.carry.poke(carry.B)
      dut.io.output.expect(reference(a, b, carry, width), s"a=$a b=$b carry=$carry")
    }
  }

  /**
    * Check a fully pipelined adder, waiting `latency` cycles after each operand pair.
    *
    * @param dut     The adder under test
    * @param vectors The `(src1, src2, carry)` vectors to apply
    */
  def checkPipelined(dut: UIntAdd, vectors: Seq[(BigInt, BigInt, Boolean)]): Unit = {
    val width   = dut.io.src1.getWidth
    val latency = dut.latency
    vectors.foreach { case (a, b, carry) =>
      dut.io.src1.poke(a.U(width.W))
      dut.io.src2.poke(b.U(width.W))
      dut.io.carry.poke(carry.B)
      dut.clock.step(latency)
      dut.io.output.expect(reference(a, b, carry, width), s"a=$a b=$b carry=$carry")
    }
  }
}
