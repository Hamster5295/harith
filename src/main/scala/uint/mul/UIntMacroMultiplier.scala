package harith.uint

import chisel3._
import chisel3.util._
import hammer.Export

/**
  * The unsigned multiplier implemented with the `*` operator.
  *
  * FPGA will likely implement it as an inner DSP.
  *
  * @param width The width of the operands
  *
  * delay = 10.660, area = 47 @32bit@fpga
  * delay = 3.6637, area = 16409.96 @32bit@55nm
  */
class UIntMacroMultiplier(width: Int) extends UIntMultiplier {
  val io = IO(new UIntMultiplierIO(width))

  io.output := io.src1 * io.src2
}

object UIntMacroMultiplier extends App {
  Export(
    new UIntMacroMultiplier(32),
    args,
    Array(
      "--lowering-options=mitigateVivadoArrayIndexConstPropBug,disallowLocalVariables,disallowPackedArrays",
    ),
  )
}
