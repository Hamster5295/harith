package harith.uint

import chisel3._
import chisel3.util._
import harith.ExportForAnalysis

/**
  * A hierarchical carry lookahead adder.
  *
  * The bits are split into groups. Each group resolves its internal carries with a lookahead
  * network, exposes a group generate/propagate pair, and the group carries are resolved by a
  * second lookahead level. This keeps the critical path logarithmic while using far less logic
  * than a fully parallel prefix adder.
  *
  * fpga@32bit: delay[i/o/max] = 3.879ns/4.451ns/3.879ns  area = 70luts + 0ff
  *
  * 55nm@32bit: delay[i/o/max] = 1.4466ns/1.4466ns/1.4466ns  area = 493.08um²
  *
  * @param width     The width of the operands
  * @param groupSize The number of bits per lookahead group
  */
class UIntCarryLookaheadAdd(val width: Int, val groupSize: Int) extends UIntAdd {
  val io = IO(new UIntAddIO(width))
  require(groupSize > 0, "groupSize must be positive")

  val numGroups = (width + groupSize - 1) / groupSize

  val propagate = io.src1 ^ io.src2
  val generate  = io.src1 & io.src2

  val groupPropagate = Wire(Vec(numGroups, Bool()))
  val groupGenerate  = Wire(Vec(numGroups, Bool()))

  val groupNodes = (0 until numGroups).map { k =>
    val base             = k * groupSize
    val end              = math.min(base + groupSize, width)
    val (gNodes, pNodes) =
      PrefixNet.nodes(
        (base until end).map(propagate(_)),
        (base until end).map(generate(_)),
        PrefixStyle.Sklansky,
      )
    groupPropagate(k) := pNodes.last
    groupGenerate(k)  := gNodes.last
    (gNodes, pNodes)
  }

  val groupCarry =
    PrefixNet.carries(
      (0 until numGroups).map(groupPropagate(_)),
      (0 until numGroups).map(groupGenerate(_)),
      io.carry,
      PrefixStyle.Sklansky,
    )

  val sums = Wire(Vec(width, Bool()))
  for (k <- 0 until numGroups) {
    val base             = k * groupSize
    val end              = math.min(base + groupSize, width)
    val (gNodes, pNodes) = groupNodes(k)
    for (j <- 0 until (end - base)) {
      val carry =
        if (j == 0) groupCarry(k)
        else gNodes(j - 1) | (pNodes(j - 1) & groupCarry(k))
      sums(base + j) := propagate(base + j) ^ carry
    }
  }

  io.output := Cat(groupCarry(numGroups), sums.asUInt)
}

object UIntCarryLookaheadAdd extends App {
  ExportForAnalysis(
    new UIntCarryLookaheadAdd(32, 4),
    args,
    Array(
      "--lowering-options=mitigateVivadoArrayIndexConstPropBug,disallowLocalVariables,disallowPackedArrays",
    ),
  )
}
