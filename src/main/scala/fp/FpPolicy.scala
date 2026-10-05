package harith.fp

/**
  * The compile-time numeric policy of a floating-point unit.
  *
  * The rounding mode is a runtime port on every unit, so it is not part of the policy. The policy
  * only carries the options that change the datapath structure.
  *
  * @param ftz Flush a subnormal result to zero
  * @param daz Treat a subnormal operand as zero
  */
final case class FpPolicy(ftz: Boolean = false, daz: Boolean = false)
