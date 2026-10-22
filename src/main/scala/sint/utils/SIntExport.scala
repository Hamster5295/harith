package harith.sint

/**
  * The shared `firtool` lowering options for the signed export entry points.
  */
private[sint] object SIntExport {
  val opts: Array[String] = Array(
    "--lowering-options=mitigateVivadoArrayIndexConstPropBug,disallowLocalVariables,disallowPackedArrays",
  )
}
