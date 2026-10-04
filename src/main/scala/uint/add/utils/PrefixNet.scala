package harith.uint

import chisel3._
import chisel3.util._

/**
  * Builds parallel prefix networks shared by [[UIntPrefixAdder]] and [[UIntCarryLookaheadAdder]].
  *
  * A network is expressed as a sequence of levels, each level being a sequence of
  * `(target, source)` index pairs. All pairs of a level read the state produced by the previous
  * level, matching the parallelism of the underlying hardware.
  */
private[uint] object PrefixNet {

  /**
    * The smallest power of two greater than or equal to `n`.
    *
    * @param n The lower bound
    * @return the smallest power of two that is at least `n`
    */
  def nextPow2(n: Int): Int = {
    require(n > 0, "nextPow2 requires a positive size")
    1 << log2Ceil(n)
  }

  /**
    * The prefix levels of `n` generated according to `style`.
    *
    * `n` must be a power of two.
    *
    * @param n     The number of bits
    * @param style The parallel prefix network style
    * @return the levels of `(target, source)` index pairs
    */
  def levels(n: Int, style: PrefixStyle): Seq[Seq[(Int, Int)]] = {
    require(n > 0 && (n & (n - 1)) == 0, "prefix networks require a power-of-two size")
    style match {
      case PrefixStyle.KoggeStone =>
        val out = scala.collection.mutable.ArrayBuffer.empty[Seq[(Int, Int)]]
        var d   = 1
        while (d < n) {
          out += (d until n).map(i => (i, i - d))
          d *= 2
        }
        out.toSeq
      case PrefixStyle.BrentKung =>
        val out = scala.collection.mutable.ArrayBuffer.empty[Seq[(Int, Int)]]
        var d   = 1
        while (d < n) {
          out += (2 * d - 1 until n by 2 * d).map(i => (i, i - d))
          d *= 2
        }
        d = n / 4
        while (d >= 1) {
          out += (3 * d - 1 until n by 2 * d).map(i => (i, i - d))
          d /= 2
        }
        out.toSeq
      case PrefixStyle.Sklansky =>
        val out = scala.collection.mutable.ArrayBuffer.empty[Seq[(Int, Int)]]
        var d   = 1
        while (d < n) {
          val pairs = scala.collection.mutable.ArrayBuffer.empty[(Int, Int)]
          var start = 0
          while (start + 2 * d <= n) {
            for (j <- 0 until d) pairs += ((start + d + j, start + d - 1))
            start += 2 * d
          }
          out += pairs.toSeq
          d *= 2
        }
        out.toSeq
      case PrefixStyle.HanCarlson =>
        val out = scala.collection.mutable.ArrayBuffer.empty[Seq[(Int, Int)]]
        out += (1 until n by 2).map(i => (i, i - 1))
        var d = 2
        while (d < n) {
          out += (d + 1 until n by 2).map(i => (i, i - d))
          d *= 2
        }
        out += (2 until n by 2).map(i => (i, i - 1))
        out.toSeq
    }
  }

  /**
    * Apply the inclusive prefix network to per-bit generate/propagate values.
    *
    * Bit `i` of the inputs describes bit `i` of the operands. The network is padded to a power of
    * two with the neutral element `(g, p) = (false, true)`, so arbitrary widths are supported.
    *
    * @param p     The per-bit propagate values
    * @param g     The per-bit generate values
    * @param style The parallel prefix network style
    * @return the inclusive prefix generate and propagate nodes, truncated to the input width
    */
  def nodes(p: Seq[Bool], g: Seq[Bool], style: PrefixStyle): (Seq[Bool], Seq[Bool]) = {
    val width = p.length
    require(width == g.length && width > 0, "prefix networks require non-empty, matching inputs")
    val n = nextPow2(width)

    var gs: Seq[Bool] = Seq.tabulate(n)(i => if (i < width) g(i) else false.B)
    var ps: Seq[Bool] = Seq.tabulate(n)(i => if (i < width) p(i) else true.B)

    for (level <- levels(n, style)) {
      val oldG = gs
      val oldP = ps
      val newG = oldG.toArray
      val newP = oldP.toArray
      for ((target, source) <- level) {
        newG(target) = oldG(target) | (oldP(target) & oldG(source))
        newP(target) = oldP(target) & oldP(source)
      }
      gs = newG.toSeq
      ps = newP.toSeq
    }

    (gs.take(width), ps.take(width))
  }

  /**
    * Compute the carry into each bit, including the final carry out.
    *
    * @param p       The per-bit propagate values
    * @param g       The per-bit generate values
    * @param carryIn The carry into the least significant bit
    * @param style   The parallel prefix network style
    * @return a sequence of `width + 1` carries, where index `i` is the carry into bit `i`
    */
  def carries(p: Seq[Bool], g: Seq[Bool], carryIn: Bool, style: PrefixStyle): Seq[Bool] = {
    val (prefixG, prefixP) = nodes(p, g, style)
    Seq.tabulate(p.length + 1) { i =>
      if (i == 0) carryIn else prefixG(i - 1) | (prefixP(i - 1) & carryIn)
    }
  }
}
