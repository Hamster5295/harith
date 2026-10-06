package harith.uint

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis

/**
  * The unsigned multiplier implemented with the `*` operator.
  *
  * FPGA will likely implement it as an inner DSP.
  *
  * fpga@32bit: delay[i/o/max] = 7.721ns/8.293ns/7.721ns  area = 47luts + 0ff
  *
  * 55nm@32bit: delay[i/o/max] = 3.4547ns/3.4547ns/2.6354ns  area = 16555.56um²
  *
  * @param width The width of the operands
  */
class UIntMacroMul(width: Int) extends UIntMul {
  val io = IO(new UIntMulIO(width))

  io.output := io.src1 * io.src2
}

object UIntMacroMul extends App {
  ExportForAnalysis(
    new UIntMacroMul(32),
    args,
    Array(
      "--lowering-options=mitigateVivadoArrayIndexConstPropBug,disallowLocalVariables,disallowPackedArrays",
    ),
  )
}
