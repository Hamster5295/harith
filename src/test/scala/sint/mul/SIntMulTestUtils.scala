package harith.sint

import chisel3._
import chisel3.simulator.PeekPokeAPI._

/**
  * Shared, per-module test helpers for the `SIntMul` family.
  */
object SIntMulTestUtils {

  /**
    * The operand widths exercised by the specs.
    */
  val widths: Seq[Int] = Seq(1, 2, 4, 8, 13)

  /** The widths exercised by the pipelined specs. */
  val pipelinedWidths: Seq[Int] = Seq(1, 4, 13)

  /**
    * Reinterpret `value` as a two's complement number of `width` bits.
    *
    * @param value The value to truncate
    * @param width The number of bits
    * @return the signed value
    */
  def toSigned(value: BigInt, width: Int): BigInt = {
    val mod = BigInt(1) << width
    val raw = ((value % mod) + mod) % mod
    if (raw >= (BigInt(1) << (width - 1))) raw - mod else raw
  }

  /**
    * A reproducible signed value of `width` bits.
    *
    * @param width  The number of bits
    * @param random The random source
    * @return the signed value
    */
  def randomSigned(width: Int, random: scala.util.Random): BigInt =
    toSigned(BigInt(width, random), width)

  /**
    * Corner cases around zero, the operand minimum and the operand maximum.
    *
    * @param width The operand width
    * @return the edge case `(src1, src2)` vectors
    */
  def edgeVectors(width: Int): Seq[(BigInt, BigInt)] = {
    val max = (BigInt(1) << (width - 1)) - 1
    val min = -(BigInt(1) << (width - 1))
    val one = if (max >= 1) BigInt(1) else max
    Seq(
      (BigInt(0), BigInt(0)),
      (one, BigInt(0)),
      (BigInt(0), one),
      (one, one),
      (BigInt(-1), one),
      (one, BigInt(-1)),
      (BigInt(-1), BigInt(-1)),
      (max, one),
      (min, one),
      (min, BigInt(-1)),
      (max, max),
      (min, min),
      (min, max),
      (max, min),
    )
  }

  /**
    * Reproducible pseudorandom operand vectors.
    *
    * @param width The operand width
    * @param seed  The random seed
    * @param count The number of vectors to generate
    * @return the random `(src1, src2)` vectors
    */
  def randomVectors(width: Int, seed: Int, count: Int = 12): Seq[(BigInt, BigInt)] = {
    val random = new scala.util.Random(seed)
    Seq.fill(count)((randomSigned(width, random), randomSigned(width, random)))
  }

  /**
    * The `2 * width` bit reference product of `a * b`.
    *
    * @param a     The first operand
    * @param b     The second operand
    * @param width The operand width
    * @return the signed product
    */
  def reference(a: BigInt, b: BigInt, width: Int): BigInt =
    toSigned(a * b, 2 * width)

  /**
    * Check a combinational multiplier against the reference in the same cycle.
    *
    * @param dut     The multiplier under test
    * @param vectors The `(src1, src2)` vectors to apply
    */
  def checkCombinational(dut: SIntMul, vectors: Seq[(BigInt, BigInt)]): Unit = {
    val width = dut.io.src1.getWidth
    vectors.foreach { case (a, b) =>
      dut.io.src1.poke(a.S(width.W))
      dut.io.src2.poke(b.S(width.W))
      dut.io.output.expect(reference(a, b, width).S((2 * width).W), s"a=$a b=$b")
    }
  }


  /** Check a fully pipelined signed multiplier, waiting `latency` cycles. */
  def checkPipelined(dut: SIntMul, vectors: Seq[(BigInt, BigInt)]): Unit = {
    val width   = dut.io.src1.getWidth
    val latency = dut.latency
    vectors.foreach { case (a, b) =>
      dut.io.src1.poke(a.S(width.W))
      dut.io.src2.poke(b.S(width.W))
      dut.clock.step(latency)
      dut.io.output.expect(reference(a, b, width).S((2 * width).W), s"a=$a b=$b")
    }
  }
}
