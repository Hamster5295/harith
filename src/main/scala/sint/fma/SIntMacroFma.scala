package harith.sint

import chisel3._
import harith.ExportForAnalysis

/**
  * The signed FMA implemented with the `*` and `+` operators.
  *
  * The whole expression is left to the synthesis tool, which on FPGA will likely map it into an
  * inner DSP with a built-in multiply-accumulate.
  *
  * fpga@32bit: delay[i/o/max] = 8.869ns/9.441ns/8.869ns  area = 111luts + 0ff
  *
  * 55nm@32bit: delay[i/o/max] = 4.3248ns/4.3248ns/4.3248ns  area = 20068.16um²
  *
  * @param width The width of the multiplicands
  */
class SIntMacroFma(val width: Int) extends SIntFma {
  val io = IO(new SIntFmaIO(width))
  require(width > 0, "width must be positive")

  val product = io.mul1 * io.mul2
  io.output := product.pad(2 * width + 1) + io.add.pad(2 * width + 1)
}

object SIntMacroFma extends App {
  ExportForAnalysis(new SIntMacroFma(32), args, SIntExport.opts)
}
