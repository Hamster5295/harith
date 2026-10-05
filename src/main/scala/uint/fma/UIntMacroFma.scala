package harith.uint

import chisel3._
import chisel3.util._
import hammer.Export

/**
  * The unsigned FMA implemented with the `*` and `+` operators.
  *
  * The whole expression is left to the synthesis tool, which on FPGA will likely map it into an
  * inner DSP with a built-in multiply-accumulate.
  *
  * @param width The width of the operands
  *
  * delay = 11.917, area = 111 @32bit@fpga
  * delay = 3.6603, area = 17201.52 @32bit@55nm
  */
class UIntMacroFma(val width: Int) extends UIntFma {
  val io = IO(new UIntFmaIO(width))

  io.output := (io.mul1 * io.mul2) +& io.add
}

object UIntMacroFma extends App {
  Export(
    new UIntMacroFma(32),
    args,
    Array(
      "--lowering-options=mitigateVivadoArrayIndexConstPropBug,disallowLocalVariables,disallowPackedArrays",
    ),
  )
}
