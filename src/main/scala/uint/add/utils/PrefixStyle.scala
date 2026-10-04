package harith.uint

/**
  * The parallel prefix network styles of [[UIntPrefixAdder]].
  *
  * They trade logic depth against area and wiring, spanning the high-performance to resource
  * constrained range of prefix adders.
  */
sealed trait PrefixStyle

/**
  * The available [[PrefixStyle]] networks.
  */
object PrefixStyle {

  /**
    * Minimum depth (log2(n)) with the largest area and fanout.
    */
  case object KoggeStone extends PrefixStyle

  /**
    * Minimum area and fanout with depth 2 * log2(n) - 1.
    */
  case object BrentKung extends PrefixStyle

  /**
    * Depth log2(n) with lower area than [[KoggeStone]] but high fanout.
    */
  case object Sklansky extends PrefixStyle

  /**
    * A sparse Kogge-Stone hybrid of depth log2(n) + 1, balancing area and wiring.
    */
  case object HanCarlson extends PrefixStyle
}
