package harith.uint

import chisel3._
import chisel3.util._
import hammer.Export

/**
  * A fully parallel prefix adder.
  *
  * All carries are computed by a single prefix network, giving a logarithmic critical path. The
  * [[PrefixStyle]] selects the network shape and therefore the area/performance point.
  *
  * @param width The width of the operands
  * @param style The parallel prefix network style
  *
  * delay = 6.814, area = 162 @32bit@fpga
  * delay = 1.6987, area = 919.24 @32bit@55nm
  */
class UIntPrefixAdder(val width: Int, val style: PrefixStyle) extends UIntAdder {
  val io = IO(new UIntAdderIO(width))
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

object UIntPrefixAdder extends App {
  Export(
    new UIntPrefixAdder(32, PrefixStyle.KoggeStone),
    args,
    Array(
      "--lowering-options=mitigateVivadoArrayIndexConstPropBug,disallowLocalVariables,disallowPackedArrays",
    ),
  )
}
