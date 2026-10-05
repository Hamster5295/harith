package harith.uint

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis

/**
  * A block carry skip (carry bypass) adder.
  *
  * Each block ripples internally, while a block whose bits all propagate lets the incoming carry
  * skip over it, shortening the worst case critical path at little area cost.
  *
  * fpga@32bit: delay[i/o/max] = 6.359ns/6.931ns/6.359ns  area = 55luts + 0ff
  *
  * 55nm@32bit: delay[i/o/max] = 2.6155ns/2.6155ns/2.6155ns  area = 308.00um²
  *
  * @param width     The width of the operands
  * @param blockSize The number of bits per skip block
  */
class UIntCarrySkipAdd(val width: Int, val blockSize: Int) extends UIntAdd {
  val io = IO(new UIntAddIO(width))
  require(blockSize > 0, "blockSize must be positive")

  val numBlocks = (width + blockSize - 1) / blockSize

  val propagate = io.src1 ^ io.src2

  val sums       = Wire(Vec(width, Bool()))
  val blockCarry = Wire(Vec(numBlocks + 1, Bool()))
  blockCarry(0) := io.carry

  for (k <- 0 until numBlocks) {
    val base = k * blockSize
    val end  = math.min(base + blockSize, width)

    val blockPropagate = (base until end).map(i => propagate(i)).reduce(_ && _)

    val rippleCarry = Wire(Vec(end - base + 1, Bool()))
    rippleCarry(0) := blockCarry(k)
    for (i <- base until end) {
      val (sum, carryOut) = AddUtils.fullAdd(io.src1(i), io.src2(i), rippleCarry(i - base))
      sums(i)                   := sum
      rippleCarry(i - base + 1) := carryOut
    }

    blockCarry(k + 1) := Mux(blockPropagate, blockCarry(k), rippleCarry(end - base))
  }

  io.output := Cat(blockCarry(numBlocks), sums.asUInt)
}

object UIntCarrySkipAdd extends App {
  ExportForAnalysis(
    new UIntCarrySkipAdd(32, 4),
    args,
    Array(
      "--lowering-options=mitigateVivadoArrayIndexConstPropBug,disallowLocalVariables,disallowPackedArrays",
    ),
  )
}
