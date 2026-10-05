package harith.uint

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis

/**
  * A structural carry save array multiplier.
  *
  * Each partial product row is accumulated in carry save form with a row of full adders, and a
  * final ripple carry chain produces the product. The regular structure gives the lowest cost at
  * the price of an O(width) critical path. On FPGAs the inferred [[UIntMacroMul]] is
  * usually preferable.
  *
  * fpga@32bit: delay[i/o/max] = 20.305ns/20.877ns/20.305ns  area = 2589luts + 0ff
  *
  * 55nm@32bit: delay[i/o/max] = 7.9127ns/7.9127ns/7.9127ns  area = 16088.52um²
  *
  * @param width The width of the operands
  */
class UIntArrayMul(val width: Int) extends UIntMul {
  val io = IO(new UIntMulIO(width))
  require(width > 0, "width must be positive")

  val nCols = MulUtils.columnCount(width)

  var acc:   Seq[Bool] = Seq.fill(nCols)(false.B)
  var carry: Seq[Bool] = Seq.fill(nCols)(false.B)

  for (row <- 0 until width) {
    val bits = MulUtils.andRow(io.src1, io.src2, row, width)
    if (row == 0) {
      acc = bits
      carry = Seq.fill(nCols)(false.B)
    } else {
      val (nextAcc, nextCarry) = MulUtils.compressRow(acc, carry, bits)
      acc = nextAcc
      carry = nextCarry
    }
  }

  io.output := MulUtils.finalAdd(acc, carry)
}

object UIntArrayMul extends App {
  ExportForAnalysis(
    new UIntArrayMul(32),
    args,
    Array(
      "--lowering-options=mitigateVivadoArrayIndexConstPropBug,disallowLocalVariables,disallowPackedArrays",
    ),
  )
}
