package harith.sint

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis
import harith.uint.MulUtils

/**
  * A pipelined signed carry save array multiplier using Baugh-Wooley partial products.
  *
  * The rows are distributed over `stages` register layers; `stages = 0` is the combinational array.
  *
  * fpga@32bit-2cyc: delay[i/o/max] = 7.452ns/6.329ns/7.332ns  area = 2177luts + 220ff
  *
  * 55nm@32bit-2cyc: delay[i/o/max] = 3.4569ns/2.4716ns/3.4569ns  area = 19392.24um²
  *
  * @param width  The width of the operands
  * @param stages The number of pipeline register layers, which is also the latency
  */
class SIntPipelinedArrayMul(val width: Int, val stages: Int) extends SIntMul {
  val io = IO(new SIntMulIO(width))
  require(width > 0, "width must be positive")
  require(stages >= 0, "stages must be non-negative")

  private val nCols = 2 * width

  var src1:  SInt      = io.src1
  var src2:  SInt      = io.src2
  var acc:   Seq[Bool] = Seq.fill(nCols)(false.B)
  var carry: Seq[Bool] = Seq.fill(nCols)(false.B)

  private val rowCount = width + (if (width > 1) 1 else 0)
  private val rows     = (0 until rowCount).toSeq
  private val groups   = if (stages == 0) Seq(rows) else MulUtils.partition(rows, stages)

  for (group <- groups) {
    for (row <- group) {
      val bits =
        if (row < width) SIntMulUtils.baughWooleyRow(src1, src2, row, width)
        else SIntMulUtils.correctionRow(width)
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

  io.output := MulUtils.finalAdd(acc, carry).asSInt

  override def latency: Int = stages
}

object SIntPipelinedArrayMul extends App {
  ExportForAnalysis(new SIntPipelinedArrayMul(32, 2), args, SIntExport.opts)
}
