package harith.uint

import chisel3._
import chisel3.util._
import hammer.Export

/**
  * A block carry select adder.
  *
  * Each block speculatively computes its result for both possible incoming carries and selects the
  * correct one once the real carry arrives. The duplicated logic reduces the critical path to one
  * carry select per block.
  *
  * @param width     The width of the operands
  * @param blockSize The number of bits per select block
  *
  * delay = 6.506, area = 70 @32bit@fpga
  * delay = 3.3749, area = 404.60 @32bit@55nm
  */
class UIntCarrySelectAdder(val width: Int, val blockSize: Int) extends UIntAdder {
  val io = IO(new UIntAdderIO(width))
  require(blockSize > 0, "blockSize must be positive")

  val numBlocks = (width + blockSize - 1) / blockSize

  val sums       = Wire(Vec(width, Bool()))
  val blockCarry = Wire(Vec(numBlocks + 1, Bool()))
  blockCarry(0) := io.carry

  for (k <- 0 until numBlocks) {
    val base = k * blockSize
    val end  = math.min(base + blockSize, width)
    val size = end - base

    val sumForZero   = Wire(Vec(size, Bool()))
    val sumForOne    = Wire(Vec(size, Bool()))
    val carryForZero = Wire(Vec(size + 1, Bool()))
    val carryForOne  = Wire(Vec(size + 1, Bool()))
    carryForZero(0) := false.B
    carryForOne(0)  := true.B

    for (i <- 0 until size) {
      val (sum0, carry0) =
        AdderUtils.fullAdder(io.src1(base + i), io.src2(base + i), carryForZero(i))
      val (sum1, carry1) =
        AdderUtils.fullAdder(io.src1(base + i), io.src2(base + i), carryForOne(i))
      sumForZero(i)       := sum0
      sumForOne(i)        := sum1
      carryForZero(i + 1) := carry0
      carryForOne(i + 1)  := carry1
    }

    for (i <- 0 until size) {
      sums(base + i) := Mux(blockCarry(k), sumForOne(i), sumForZero(i))
    }
    blockCarry(k + 1) := Mux(blockCarry(k), carryForOne(size), carryForZero(size))
  }

  io.output := Cat(blockCarry(numBlocks), sums.asUInt)
}

object UIntCarrySelectAdder extends App {
  Export(
    new UIntCarrySelectAdder(32, 4),
    args,
    Array(
      "--lowering-options=mitigateVivadoArrayIndexConstPropBug,disallowLocalVariables,disallowPackedArrays",
    ),
  )
}
