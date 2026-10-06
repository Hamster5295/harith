package harith.uint

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis

/**
  * A fully parallel prefix adder.
  *
  * All carries are computed by a single prefix network, giving a logarithmic critical path. The
  * [[PrefixStyle]] selects the network shape and therefore the area/performance point.
  *
  * fpga@32bit: delay[i/o/max] = 3.816ns/4.388ns/3.816ns  area = 159luts + 0ff
  *
  * 55nm@32bit: delay[i/o/max] = 1.3195ns/1.3195ns/1.3195ns  area = 945.84um²
  *
  * @param width The width of the operands
  * @param style The parallel prefix network style
  */
class UIntPrefixAdd(val width: Int, val style: PrefixStyle) extends UIntAdd {
  val io = IO(new UIntAddIO(width))
  require(width > 0, "width must be positive")

  val propagate = io.src1 ^ io.src2
  val generate  = io.src1 & io.src2

  val carries = PrefixNet.carries(
    (0 until width).map(propagate(_)),
    (0 until width).map(generate(_)),
    io.carry,
    style,
  )

  val sums = Wire(Vec(width, Bool()))
  for (i <- 0 until width) {
    sums(i) := propagate(i) ^ carries(i)
  }

  io.output := Cat(carries(width), sums.asUInt)
}

object UIntPrefixAdd extends App {
  ExportForAnalysis(
    new UIntPrefixAdd(32, PrefixStyle.KoggeStone),
    args,
    Array(
      "--lowering-options=mitigateVivadoArrayIndexConstPropBug,disallowLocalVariables,disallowPackedArrays",
    ),
  )
}
