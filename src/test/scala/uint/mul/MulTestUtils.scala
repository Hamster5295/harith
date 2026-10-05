package harith.uint

import chisel3._
import chisel3.simulator.PeekPokeAPI._

/**
  * Shared, per-module test helpers for the `UIntMul` family.
  */
object MulTestUtils {

  /**
    * The operand widths exercised by the combinational specs.
    */
  val widths: Seq[Int] = Seq(1, 2, 4, 8, 13)

  /**
    * The operand widths exercised by the pipelined specs, which are more expensive to simulate.
    */
  val pipelinedWidths: Seq[Int] = Seq(1, 4, 13)

  /**
    * Corner cases around zero and the operand maximum.
    *
    * @param width The operand width
    * @return the edge case `(src1, src2)` vectors
    */
  def edgeVectors(width: Int): Seq[(BigInt, BigInt)] = {
    val max = (BigInt(1) << width) - 1
    Seq(
      (BigInt(0), BigInt(0)),
      (max, BigInt(0)),
      (BigInt(0), max),
      (max, max),
      (BigInt(1), BigInt(1)),
      (max, BigInt(1)),
      (BigInt(1), max),
      (max - 1, max),
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
    Seq.fill(count)((BigInt(width, random), BigInt(width, random)))
  }

  /**
    * The `2 * width` bit reference product of `a * b`.
    *
    * @param a     The first operand
    * @param b     The second operand
    * @param width The operand width
    * @return the truncated `2 * width` bit product
    */
  def reference(a: BigInt, b: BigInt, width: Int): BigInt =
    (a * b) & ((BigInt(1) << (2 * width)) - 1)

  /**
    * Check a combinational multiplier against the reference in the same cycle.
    *
    * @param dut     The multiplier under test
    * @param vectors The `(src1, src2)` vectors to apply
    */
  def checkCombinational(dut: UIntMul, vectors: Seq[(BigInt, BigInt)]): Unit = {
    val width = dut.io.src1.getWidth
    vectors.foreach { case (a, b) =>
      dut.io.src1.poke(a.U(width.W))
      dut.io.src2.poke(b.U(width.W))
      dut.io.output.expect(reference(a, b, width), s"a=$a b=$b")
    }
  }

  /**
    * Check a fully pipelined multiplier, waiting `latency` cycles after each operand pair.
    *
    * @param dut     The multiplier under test
    * @param vectors The `(src1, src2)` vectors to apply
    */
  def checkPipelined(dut: UIntMul, vectors: Seq[(BigInt, BigInt)]): Unit = {
    val width   = dut.io.src1.getWidth
    val latency = dut.latency
    vectors.foreach { case (a, b) =>
      dut.io.src1.poke(a.U(width.W))
      dut.io.src2.poke(b.U(width.W))
      dut.clock.step(latency)
      dut.io.output.expect(reference(a, b, width), s"a=$a b=$b")
    }
  }
}
