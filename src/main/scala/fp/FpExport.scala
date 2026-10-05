package harith.fp

import hammer.Export

/**
  * Standalone export entry points for the floating-point units.
  *
  * Each object elaborates one unit at 32 bit so that it can be analysed on its own with
  * `make fpga TARGET=harith.fp.<Name>` or `make asic TARGET=harith.fp.<Name>`.
  */
private[fp] object FpExportOptions {
  val opts: Array[String] = Array(
    "--lowering-options=mitigateVivadoArrayIndexConstPropBug,disallowLocalVariables,disallowPackedArrays",
  )
}

/** A 32 bit inferred multiplier. */
object Fp32MulExport extends App {
  Export(new Fp32Mul, args, FpExportOptions.opts)
}

/** A 32 bit array multiplier. */
object Fp32ArrayMul extends App {
  Export(new FpArrayMul(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32), args, FpExportOptions.opts)
}

/** A 32 bit Booth tree multiplier. */
object Fp32BoothMul extends App {
  Export(new FpBoothMul(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32), args, FpExportOptions.opts)
}

/** A 32 bit AND partial product tree multiplier. */
object Fp32TreeMul extends App {
  Export(new FpTreeMul(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32), args, FpExportOptions.opts)
}

/** A 32 bit inferred fused multiply-adder. */
object Fp32FmaExport extends App {
  Export(new Fp32Fma, args, FpExportOptions.opts)
}

/** A 32 bit array fused multiply-adder. */
object Fp32ArrayFma extends App {
  Export(
    new FpArrayFma(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32),
    args,
    FpExportOptions.opts,
  )
}

/** A 32 bit Booth tree fused multiply-adder. */
object Fp32BoothFma extends App {
  Export(
    new FpBoothFma(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32),
    args,
    FpExportOptions.opts,
  )
}

/** A 32 bit AND partial product tree fused multiply-adder. */
object Fp32TreeFma extends App {
  Export(
    new FpTreeFma(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32),
    args,
    FpExportOptions.opts,
  )
}

/** A 32 bit fused multiply-adder with a ripple alignment adder. */
object Fp32RippleFma extends App {
  Export(
    new FpRippleFma(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32),
    args,
    FpExportOptions.opts,
  )
}

/** A 32 bit fused multiply-adder with a prefix alignment adder. */
object Fp32PrefixFma extends App {
  Export(
    new FpPrefixFma(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32),
    args,
    FpExportOptions.opts,
  )
}

/** A 32 bit inferred adder. */
object Fp32AddExport extends App {
  Export(new Fp32Add, args, FpExportOptions.opts)
}

/** A 32 bit ripple carry adder. */
object Fp32RippleAdd extends App {
  Export(new FpRippleAdd(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32), args, FpExportOptions.opts)
}

/** A 32 bit parallel prefix adder. */
object Fp32PrefixAdd extends App {
  Export(new FpPrefixAdd(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32), args, FpExportOptions.opts)
}

/** A 32 bit block carry select adder. */
object Fp32CarrySelectAdd extends App {
  Export(
    new FpCarrySelectAdd(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32),
    args,
    FpExportOptions.opts,
  )
}

/** A 32 bit hierarchical carry lookahead adder. */
object Fp32CarryLookaheadAdd extends App {
  Export(
    new FpCarryLookaheadAdd(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32),
    args,
    FpExportOptions.opts,
  )
}
