package harith.uint

/**
  * The partial product reduction styles of [[UIntTreeMul]] and [[UIntBoothMul]].
  *
  * They trade the number of carry save compressors against the reduction wiring, spanning the
  * fastest to the smallest tree multiplier.
  */
sealed trait ReductionStyle

/**
  * The available [[ReductionStyle]] reductions.
  */
object ReductionStyle {

  /**
    * Compress every column with at least three bits at each level.
    *
    * This is the fastest reduction and uses the most compressors.
    */
  case object Wallace extends ReductionStyle

  /**
    * Compress only the columns above the Dadda target height at each level.
    *
    * This uses fewer compressors than [[Wallace]] at the cost of a slightly less regular network.
    */
  case object Dadda extends ReductionStyle
}
