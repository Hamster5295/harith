package harith.uint

import chisel3._
import chisel3.simulator.PeekPokeAPI._

/**
  * Shared, per-module test helpers for the `UIntFma` family.
  */
object FmaTestUtils {

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
    * @return the edge case `(src1, src2, addend)` vectors
    */
  def edgeVectors(width: Int): Seq[(BigInt, BigInt, BigInt)] = {
    val max     = (BigInt(1) << width) - 1
    val addend  = (BigInt(1) << (2 * width)) - 1
    Seq(
      (BigInt(0), BigInt(0), BigInt(0)),
      (max, max, BigInt(0)),
      (max, max, addend),
      (BigInt(0), BigInt(0), addend),
      (BigInt(1), BigInt(1), addend),
      (max, BigInt(0), addend),
      (max, BigInt(1), addend),
      (max - 1, max, addend),
    )
  }

  /**
    * Reproducible pseudorandom operand vectors.
    *
    * @param width The operand width
    * @param seed  The random seed
    * @param count The number of vectors to generate
    * @return the random `(src1, src2, addend)` vectors
    */
  def randomVectors(width: Int, seed: Int, count: Int = 12): Seq[(BigInt, BigInt, BigInt)] = {
    val random = new scala.util.Random(seed)
    Seq.fill(count)((BigInt(width, random), BigInt(width, random), BigInt(2 * width, random)))
  }

  /**
    * The `2 * width + 1` bit reference value of `a * b + c`.
    *
    * @param a     The multiplicand
    * @param b     The multiplier
    * @param c     The addend
    * @param width The operand width
    * @return the truncated `2 * width + 1` bit result
    */
  def reference(a: BigInt, b: BigInt, c: BigInt, width: Int): BigInt =
    (a * b + c) & ((BigInt(1) << (2 * width + 1)) - 1)

  /**
    * Check a combinational FMA against the reference in the same cycle.
    *
    * @param dut     The FMA under test
    * @param vectors The `(src1, src2, addend)` vectors to apply
    */
  def checkCombinational(dut: UIntFma, vectors: Seq[(BigInt, BigInt, BigInt)]): Unit = {
    val width = dut.io.src1.getWidth
    vectors.foreach { case (a, b, c) =>
      dut.io.src1.poke(a.U(width.W))
      dut.io.src2.poke(b.U(width.W))
      dut.io.addend.poke(c.U((2 * width).W))
      dut.io.output.expect(reference(a, b, c, width), s"a=$a b=$b c=$c")
    }
  }

  /**
    * Check a fully pipelined FMA, waiting `latency` cycles after each operand triple.
    *
    * @param dut     The FMA under test
    * @param vectors The `(src1, src2, addend)` vectors to apply
    */
  def checkPipelined(dut: UIntFma, vectors: Seq[(BigInt, BigInt, BigInt)]): Unit = {
    val width   = dut.io.src1.getWidth
    val latency = dut.latency
    vectors.foreach { case (a, b, c) =>
      dut.io.src1.poke(a.U(width.W))
      dut.io.src2.poke(b.U(width.W))
      dut.io.addend.poke(c.U((2 * width).W))
      dut.clock.step(latency)
      dut.io.output.expect(reference(a, b, c, width), s"a=$a b=$b c=$c")
    }
  }
}
