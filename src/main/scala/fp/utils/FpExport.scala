package harith.fp

/**
  * The shared `firtool` lowering options for the floating-point export entry points.
  */
private[fp] object FpExport {
  val opts: Array[String] = Array(
    "--lowering-options=mitigateVivadoArrayIndexConstPropBug,disallowLocalVariables,disallowPackedArrays",
  )
}
