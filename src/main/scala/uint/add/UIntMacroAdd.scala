package harith.uint

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis

/**
  * The unsigned adder implemented with the `+` operator.
  *
  * FPGA will likely implement it as an inner DSP or CARRY primitive.
  *
  * fpga@32bit: delay[i/o/max] = 2.726ns/3.298ns/2.726ns  area = 32luts + 0ff
  *
  * 55nm@32bit: delay[i/o/max] = 1.4861ns/1.4861ns/1.4861ns  area = 378.28um²
  *
  * @param width The width of the operands
  */
class UIntMacroAdd(val width: Int) extends UIntAdd {
  val io = IO(new UIntAddIO(width))
  io.output := io.src1 +& io.src2 + io.carry
}

object UIntMacroAdd extends App {
  ExportForAnalysis(
    new UIntMacroAdd(32),
    args,
    Array(
      "--lowering-options=mitigateVivadoArrayIndexConstPropBug,disallowLocalVariables,disallowPackedArrays",
    ),
  )
}
