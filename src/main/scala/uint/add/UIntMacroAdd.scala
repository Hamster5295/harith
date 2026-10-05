package harith.uint

import chisel3._
import chisel3.util._
import hammer.Export

/**
  * The unsigned adder implemented with the `+` operator.
  *
  * FPGA will likely implement it as an inner DSP or CARRY primitive.
  *
  * fpga@32bit: delay = 5.710ns  area = 32luts + 0ff
  *
  * 55nm@32bit: delay = 1.8726ns  area = 378.28um²
  *
  * @param width The width of the operands
  */
class UIntMacroAdd(val width: Int) extends UIntAdd {
  val io = IO(new UIntAddIO(width))
  io.output := io.src1 +& io.src2 + io.carry
}

object UIntMacroAdd extends App {
  Export(
    new UIntMacroAdd(32),
    args,
    Array(
      "--lowering-options=mitigateVivadoArrayIndexConstPropBug,disallowLocalVariables,disallowPackedArrays",
    ),
  )
}
