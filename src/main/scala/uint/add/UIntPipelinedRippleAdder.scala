package harith.uint

import chisel3._
import chisel3.util._

/**
  * A pipelined ripple carry adder.
  *
  * The carry chain is cut at every block boundary and the carry, the operand slices and the
  * accumulated sums advance one block per cycle. This yields a short, block sized critical path and
  * a throughput of one addition per cycle with a fixed latency of the number of blocks.
  *
  * @param width     The width of the operands
  * @param blockSize The number of bits per pipeline stage
  */
class UIntPipelinedRippleAdder(val width: Int, val blockSize: Int) extends UIntAdder {
  val io = IO(new UIntAdderIO(width))
  require(width > 0, "width must be positive")
  require(blockSize > 0, "blockSize must be positive")

  val numBlocks = (width + blockSize - 1) / blockSize

  override def latency: Int = numBlocks

  var src1:  UInt      = io.src1
  var src2:  UInt      = io.src2
  var carry: Bool      = io.carry
  var sums:  Vec[Bool] = VecInit(Seq.fill(width)(false.B))

  for (k <- 0 until numBlocks) {
    val base = k * blockSize
    val end  = math.min(base + blockSize, width)

    val blockCarry = Wire(Vec(end - base + 1, Bool()))
    blockCarry(0) := carry
    val blockSums = Wire(Vec(end - base, Bool()))
    for (i <- base until end) {
      val (sum, carryOut) = AdderUtils.fullAdder(src1(i), src2(i), blockCarry(i - base))
      blockSums(i - base)      := sum
      blockCarry(i - base + 1) := carryOut
    }

    val sumsNext = Wire(Vec(width, Bool()))
    for (i <- 0 until width) {
      if (i >= base && i < end) {
        sumsNext(i) := blockSums(i - base)
      } else {
        sumsNext(i) := sums(i)
      }
    }

    src1 = RegNext(src1)
    src2 = RegNext(src2)
    carry = RegNext(blockCarry(end - base))
    sums = RegNext(sumsNext)
  }

  io.output := Cat(carry, sums.asUInt)
}
