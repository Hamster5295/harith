package harith.sint

import chisel3._
import harith.ExportForAnalysis

/**
  * The signed multiplier implemented with the `*` operator.
  *
  * Both operands are two's complement and the product is exact in `2 * width` bits. Synthesis maps
  * the expression to a signed multiplier (a Baugh-Wooley array, or an unsigned array with sign
  * extension).
  *
  * fpga@32bit: delay[i/o/max] = 7.721ns/8.293ns/7.721ns  area = 47luts + 0ff
  *
  * 55nm@32bit: delay[i/o/max] = 4.0572ns/4.0572ns/4.0572ns  area = 18430.16um²
  *
  * @param width The width of the operands
  */
class SIntMacroMul(val width: Int) extends SIntMul {
  val io = IO(new SIntMulIO(width))
  require(width > 0, "width must be positive")

  io.output := io.src1 * io.src2
}

object SIntMacroMul extends App {
  ExportForAnalysis(new SIntMacroMul(32), args, SIntExport.opts)
}
