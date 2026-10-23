package harith.sint

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis
import harith.uint.MulUtils
import harith.uint.PrefixStyle
import harith.uint.ReductionStyle
import harith.uint.UIntAdd
import harith.uint.UIntPrefixAdd

/**
  * A signed fused multiply-adder using signed modified Booth partial products with the addend merged
  * into the reduction.
  *
  * fpga@32bit: delay[i/o/max] = 10.316ns/10.888ns/10.316ns  area = 3311luts + 0ff
  *
  * 55nm@32bit: delay[i/o/max] = 5.1612ns/5.1612ns/5.1612ns  area = 18097.52um²
  *
  * @param width          The width of the multiplicands
  * @param reductionStyle The reduction style of the partial product tree
  * @param adder          The final carry propagate adder factory, given the `2 * width` bit width
  */
class SIntBoothFma(
    val width:          Int,
    val reductionStyle: ReductionStyle,
    adder:              Int => UIntAdd,
) extends SIntFma {
  val io = IO(new SIntFmaIO(width))
  require(width > 0, "width must be positive")

  private val nCols = 2 * width

  val columns =
    SIntMulUtils.withAddend(SIntMulUtils.boothColumns(io.mul1, io.mul2, width), io.add, width)
  val (lower, upper) = MulUtils.toRows(MulUtils.reduce(columns, reductionStyle))

  val finalAdd = Module(adder(nCols))
  finalAdd.io.src1  := lower
  finalAdd.io.src2  := upper
  finalAdd.io.carry := false.B

  val pSign = SIntMulUtils.productSign(io.mul1, io.mul2, width)
  val cSign = io.add(nCols - 1)
  val sum   = finalAdd.io.output(nCols - 1, 0)
  val sMsb  = sum(nCols - 1)
  val ovf   = (pSign === cSign) && (sMsb =/= pSign)
  val top   = sMsb ^ ovf
  io.output := Cat(top, sum).asSInt

  override def latency: Int = finalAdd.latency
}

object SIntBoothFma extends App {
  ExportForAnalysis(
    new SIntBoothFma(32, ReductionStyle.Dadda, w => new UIntPrefixAdd(w, PrefixStyle.KoggeStone)),
    args,
    SIntExport.opts,
  )
}
