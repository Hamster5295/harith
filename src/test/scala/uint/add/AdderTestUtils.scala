package harith.uint

import chisel3._
import chisel3.simulator.PeekPokeAPI._

/** Shared, per-module test helpers for the `UIntAdder` family. */
object AdderTestUtils {

  val widths: Seq[Int] = Seq(1, 13, 32)

  private def maxValue(width: Int): BigInt = (BigInt(1) << width) - 1

  /** Corner cases around zero, the operand maximum and the carry input. */
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

  /** Reproducible pseudorandom operand vectors. */
  def randomVectors(width: Int, seed: Int, count: Int = 24): Seq[(BigInt, BigInt, Boolean)] = {
    val random = new scala.util.Random(seed)
    Seq.fill(count)(
      (BigInt(width, random), BigInt(width, random), random.nextBoolean()),
    )
  }

  /** The width + 1 bit reference sum of `a + b + carry`. */
  def reference(a: BigInt, b: BigInt, carry: Boolean, width: Int): BigInt =
    (a + b + (if (carry) 1 else 0)) & ((BigInt(1) << (width + 1)) - 1)

  /** Check a combinational adder against the reference in the same cycle. */
  def checkCombinational(dut: Module with UIntAdder, vectors: Seq[(BigInt, BigInt, Boolean)]): Unit = {
    val width = dut.io.src1.getWidth
    vectors.foreach { case (a, b, carry) =>
      dut.io.src1.poke(a.U(width.W))
      dut.io.src2.poke(b.U(width.W))
      dut.io.carry.poke(carry.B)
      dut.io.output.expect(reference(a, b, carry, width), s"a=$a b=$b carry=$carry")
    }
  }

  /** Check a fully pipelined adder, waiting `latency` cycles after each operand pair. */
  def checkPipelined(dut: Module with UIntAdder, vectors: Seq[(BigInt, BigInt, Boolean)]): Unit = {
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
