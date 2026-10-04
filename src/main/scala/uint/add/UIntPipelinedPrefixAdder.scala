package harith.uint

import chisel3._
import chisel3.util._
import hammer.Export

/**
  * A pipelined parallel prefix adder.
  *
  * The prefix levels of the selected [[PrefixStyle]] are distributed over `stages` register layers.
  * The throughput is one addition per cycle and the latency equals `stages`, regardless of whether
  * a layer is used for prefix logic or only for retiming. A value of 0 makes the adder
  * combinational, equivalently to [[UIntPrefixAdder]].
  *
  * @param width  The width of the operands
  * @param style  The parallel prefix network style
  * @param stages The number of pipeline register layers, which is also the latency
  */
class UIntPipelinedPrefixAdder(val width: Int, val style: PrefixStyle, val stages: Int)
    extends Module
    with UIntAdder {
  val io = IO(new UIntAdderIO(width))
  require(width > 0, "width must be positive")
  require(stages >= 0, "stages must be non-negative")

  override def latency: Int = stages

  val propagate = io.src1 ^ io.src2
  val generate  = io.src1 & io.src2

  val size = PrefixNet.nextPow2(width)

  var prefixGenerate:  Seq[Bool] = Seq.tabulate(size)(i => if (i < width) generate(i) else false.B)
  var prefixPropagate: Seq[Bool] = Seq.tabulate(size)(i => if (i < width) propagate(i) else true.B)
  var bitPropagate:    Seq[Bool] = (0 until width).map(propagate(_))
  var carryIn:         Bool      = io.carry

  val allLevels = PrefixNet.levels(size, style)
  val groups    = if (stages == 0) Seq(allLevels) else partition(allLevels, stages)

  for (group <- groups) {
    for (level <- group) {
      val oldGenerate  = prefixGenerate
      val oldPropagate = prefixPropagate
      val newGenerate  = oldGenerate.toArray
      val newPropagate = oldPropagate.toArray
      for ((target, source) <- level) {
        newGenerate(target) = oldGenerate(target) | (oldPropagate(target) & oldGenerate(source))
        newPropagate(target) = oldPropagate(target) & oldPropagate(source)
      }
      prefixGenerate = newGenerate.toSeq
      prefixPropagate = newPropagate.toSeq
    }

    if (stages > 0) {
      prefixGenerate = prefixGenerate.map(RegNext(_))
      prefixPropagate = prefixPropagate.map(RegNext(_))
      bitPropagate = bitPropagate.map(RegNext(_))
      carryIn = RegNext(carryIn)
    }
  }

  val carries = Seq.tabulate(width + 1) { i =>
    if (i == 0) carryIn else prefixGenerate(i - 1) | (prefixPropagate(i - 1) & carryIn)
  }

  val sums = Wire(Vec(width, Bool()))
  for (i <- 0 until width) {
    sums(i) := bitPropagate(i) ^ carries(i)
  }

  io.output := Cat(carries(width), sums.asUInt)

  /**
    * Split `values` into `groups` consecutive, as evenly sized chunks as possible.
    *
    * @param values The sequence to split
    * @param groups The number of chunks to produce
    * @return the consecutive chunks
    */
  private def partition[A](values: Seq[A], groups: Int): Seq[Seq[A]] = {
    val base    = values.length / groups
    val extra   = values.length % groups
    var current = 0
    (0 until groups).map { group =>
      val size  = base + (if (group < extra) 1 else 0)
      val chunk = values.slice(current, current + size)
      current += size
      chunk
    }
  }
}

object UIntPipelinedPrefixAdder extends App {
  Export(
    new UIntPipelinedPrefixAdder(32, PrefixStyle.KoggeStone, 2),
    args,
    Array(
      "--lowering-options=mitigateVivadoArrayIndexConstPropBug,disallowLocalVariables,disallowPackedArrays",
    ),
  )
}
