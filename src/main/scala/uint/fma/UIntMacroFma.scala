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
  * fpga@32bit: delay = 11.917ns  area = 111luts + 0ff
  *
  * 55nm@32bit: delay = 3.6603ns  area = 17201.52um²
  *
  * @param width The width of the operands
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
