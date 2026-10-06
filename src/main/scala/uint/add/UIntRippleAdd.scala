package harith.uint

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis

/**
  * A structural ripple carry adder.
  *
  * This is the most resource efficient combinational adder, at the cost of an O(width) critical
  * path. On FPGAs the inferred [[UIntMacroAdd]] is usually preferable.
  *
  * fpga@32bit: delay[i/o/max] = 6.366ns/6.938ns/6.366ns  area = 56luts + 0ff
  *
  * 55nm@32bit: delay[i/o/max] = 2.7160ns/2.7160ns/2.2595ns  area = 386.68um²
  *
  * @param width The width of the operands
  */
class UIntRippleAdd(val width: Int) extends UIntAdd {
  val io = IO(new UIntAddIO(width))

  val sums    = Wire(Vec(width, Bool()))
  val carries = Wire(Vec(width + 1, Bool()))
  carries(0) := io.carry

  for (i <- 0 until width) {
    val (sum, carryOut) = AddUtils.fullAdd(io.src1(i), io.src2(i), carries(i))
    sums(i)        := sum
    carries(i + 1) := carryOut
  }

  io.output := Cat(carries(width), sums.asUInt)
}

object UIntRippleAdd extends App {
  ExportForAnalysis(
    new UIntRippleAdd(32),
    args,
    Array(
      "--lowering-options=mitigateVivadoArrayIndexConstPropBug,disallowLocalVariables,disallowPackedArrays",
    ),
  )
}
