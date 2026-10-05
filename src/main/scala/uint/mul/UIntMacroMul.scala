package harith.uint

import chisel3._
import chisel3.util._
import hammer.Export

/**
  * The unsigned multiplier implemented with the `*` operator.
  *
  * FPGA will likely implement it as an inner DSP.
  *
  * fpga@32bit: delay = 10.660ns  area = 47luts + 0ff
  *
  * 55nm@32bit: delay = 3.6637ns  area = 16409.96um²
  *
  * @param width The width of the operands
  */
class UIntMacroMul(width: Int) extends UIntMul {
  val io = IO(new UIntMulIO(width))

  io.output := io.src1 * io.src2
}

object UIntMacroMul extends App {
  Export(
    new UIntMacroMul(32),
    args,
    Array(
      "--lowering-options=mitigateVivadoArrayIndexConstPropBug,disallowLocalVariables,disallowPackedArrays",
    ),
  )
}
