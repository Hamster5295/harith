package harith.uint

import chisel3._
import chisel3.util._
import hammer.Export

/**
  * The unsigned adder implemented with the `+` operator.
  *
  * FPGA will likely implement it as an inner DSP or CARRY primitive.
  *
  * @param width The width of the operands
  *
  * delay = 5.710, area = 32 @32bit@fpga
  * delay = 1.8726, area = 378.28 @32bit@55nm
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
