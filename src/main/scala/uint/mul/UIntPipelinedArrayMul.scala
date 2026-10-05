package harith.uint

import chisel3._
import chisel3.util._
import hammer.Export

/**
  * A pipelined carry save array multiplier.
  *
  * The partial product rows are distributed over `stages` register layers. The throughput is one
  * product per cycle and the latency equals `stages`. A value of 0 makes the multiplier
  * combinational, equivalently to [[UIntArrayMul]].
  *
  * fpga@32bit-2cyc: delay = 7.545ns  area = 2124luts + 220ff
  *
  * 55nm@32bit-2cyc: delay = 2.7713ns  area = 18377.24um²
  *
  * @param width  The width of the operands
  * @param stages The number of pipeline register layers
  */
class UIntPipelinedArrayMul(val width: Int, val stages: Int) extends Module
    with UIntMul {
  val io = IO(new UIntMulIO(width))
  require(width > 0, "width must be positive")
  require(stages >= 0, "stages must be non-negative")

  val nCols = MulUtils.columnCount(width)

  var src1:  UInt      = io.src1
  var src2:  UInt      = io.src2
  var acc:   Seq[Bool] = Seq.fill(nCols)(false.B)
  var carry: Seq[Bool] = Seq.fill(nCols)(false.B)

  val rows   = (0 until width).toSeq
  val groups = if (stages == 0) Seq(rows) else MulUtils.partition(rows, stages)

  for (group <- groups) {
    for (row <- group) {
      val bits = MulUtils.andRow(src1, src2, row, width)
      if (row == 0) {
        acc = bits
        carry = Seq.fill(nCols)(false.B)
      } else {
        val (nextAcc, nextCarry) = MulUtils.compressRow(acc, carry, bits)
        acc = nextAcc
        carry = nextCarry
      }
    }
    if (stages > 0) {
      acc = acc.map(bit => RegNext(bit))
      carry = carry.map(bit => RegNext(bit))
      src1 = RegNext(src1)
      src2 = RegNext(src2)
    }
  }

  io.output := MulUtils.finalAdd(acc, carry)

  override def latency: Int = stages
}

object UIntPipelinedArrayMul extends App {
  Export(
    new UIntPipelinedArrayMul(32, 2),
    args,
    Array(
      "--lowering-options=mitigateVivadoArrayIndexConstPropBug,disallowLocalVariables,disallowPackedArrays",
    ),
  )
}
