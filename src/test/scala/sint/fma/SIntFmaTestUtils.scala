package harith.sint

import chisel3._
import chisel3.simulator.PeekPokeAPI._

/**
  * Shared, per-module test helpers for the `SIntFma` family.
  */
object SIntFmaTestUtils {

  /**
    * The operand widths exercised by the specs.
    */
  val widths: Seq[Int] = Seq(1, 2, 4, 8, 13)

  /**
    * Corner cases around zero, the operand minimum and the operand maximum.
    *
    * @param width The width of the multiplicands
    * @return the edge case `(mul1, mul2, addend)` vectors
    */
  def edgeVectors(width: Int): Seq[(BigInt, BigInt, BigInt)] = {
    val max  = (BigInt(1) << (width - 1)) - 1
    val min  = -(BigInt(1) << (width - 1))
    val one  = if (max >= 1) BigInt(1) else max
    val maxC = (BigInt(1) << (2 * width - 1)) - 1
    val minC = -(BigInt(1) << (2 * width - 1))
    Seq(
      (BigInt(0), BigInt(0), BigInt(0)),
      (one, one, BigInt(0)),
      (BigInt(-1), one, BigInt(0)),
      (BigInt(-1), BigInt(-1), BigInt(0)),
      (max, max, maxC),
      (min, min, maxC),
      (min, max, maxC),
      (max, min, minC),
      (min, one, minC),
      (max, one, maxC),
      (BigInt(0), BigInt(0), maxC),
      (BigInt(0), BigInt(0), minC),
      (min, min, minC),
    )
  }

  /**
    * Reproducible pseudorandom operand vectors.
    *
    * @param width The width of the multiplicands
    * @param seed  The random seed
    * @param count The number of vectors to generate
    * @return the random `(mul1, mul2, addend)` vectors
    */
  def randomVectors(width: Int, seed: Int, count: Int = 12): Seq[(BigInt, BigInt, BigInt)] = {
    val random = new scala.util.Random(seed)
    Seq.fill(count)(
      (
        SIntMulTestUtils.randomSigned(width, random),
        SIntMulTestUtils.randomSigned(width, random),
        SIntMulTestUtils.randomSigned(2 * width, random),
      ),
    )
  }

  /**
    * The `2 * width + 1` bit reference value of `a * b + c`.
    *
    * @param a     The multiplicand
    * @param b     The multiplier
    * @param c     The addend
    * @param width The width of the multiplicands
    * @return the signed result
    */
  def reference(a: BigInt, b: BigInt, c: BigInt, width: Int): BigInt =
    SIntMulTestUtils.toSigned(a * b + c, 2 * width + 1)

  /**
    * Check a combinational FMA against the reference in the same cycle.
    *
    * @param dut     The FMA under test
    * @param vectors The `(mul1, mul2, addend)` vectors to apply
    */
  def checkCombinational(dut: SIntFma, vectors: Seq[(BigInt, BigInt, BigInt)]): Unit = {
    val width = dut.io.mul1.getWidth
    vectors.foreach { case (a, b, c) =>
      dut.io.mul1.poke(a.S(width.W))
      dut.io.mul2.poke(b.S(width.W))
      dut.io.add.poke(c.S((2 * width).W))
      dut.io.output.expect(reference(a, b, c, width).S((2 * width + 1).W), s"a=$a b=$b c=$c")
    }
  }
}
