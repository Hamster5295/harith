package harith.fp

import chisel3._
import chisel3.util._
import harith.uint._

/**
  * A significand multiplier strategy.
  *
  * The strategy produces the exact product of the two significands. It is applied during
  * elaboration, so concrete strategies may instantiate different `harith.uint` multiplier
  * datapaths, spanning the high-performance to low-cost range.
  */
trait FpSigMul {
  def apply(x: UInt, y: UInt): UInt
}

/**
  * The inferred significand multiplier, built from the `*` operator.
  */
private[fp] object FpSigMulGeneric extends FpSigMul {
  def apply(x: UInt, y: UInt): UInt = x * y
}

/**
  * A carry save array significand multiplier, the cheapest option.
  */
private[fp] object FpSigMulArray extends FpSigMul {
  def apply(x: UInt, y: UInt): UInt = {
    val w   = math.max(x.getWidth, y.getWidth)
    val mul = Module(new UIntArrayMul(w))
    mul.io.src1 := x.pad(w)
    mul.io.src2 := y.pad(w)
    mul.io.output
  }
}

/**
  * A modified Booth radix-4 tree significand multiplier, the fast option.
  */
private[fp] object FpSigMulBooth extends FpSigMul {
  def apply(x: UInt, y: UInt): UInt = {
    val w   = math.max(x.getWidth, y.getWidth)
    val mul = Module(
      new UIntBoothMul(
        w,
        ReductionStyle.Dadda,
        new UIntPrefixAdd(2 * w, PrefixStyle.KoggeStone),
      ),
    )
    mul.io.src1 := x.pad(w)
    mul.io.src2 := y.pad(w)
    mul.io.output
  }
}

/**
  * An AND partial product carry save tree significand multiplier.
  */
private[fp] object FpSigMulTree extends FpSigMul {
  def apply(x: UInt, y: UInt): UInt = {
    val w   = math.max(x.getWidth, y.getWidth)
    val mul = Module(
      new UIntTreeMul(
        w,
        ReductionStyle.Dadda,
        new UIntPrefixAdd(2 * w, PrefixStyle.KoggeStone),
      ),
    )
    mul.io.src1 := x.pad(w)
    mul.io.src2 := y.pad(w)
    mul.io.output
  }
}

/**
  * A signed wide adder strategy for the alignment sum.
  *
  * Both inputs have the same width and the result is one bit wider, so the sum never wraps.
  */
trait FpSigAdd {
  def apply(x: SInt, y: SInt): SInt
}

/**
  * The inferred adder, built from the `+` operator.
  */
private[fp] object FpSigAddGeneric extends FpSigAdd {
  def apply(x: SInt, y: SInt): SInt = {
    val w = x.getWidth
    x.pad(w + 1) + y.pad(w + 1)
  }
}

/**
  * The two's complement wide sum implemented with the given unsigned adder.
  */
private[fp] object FpSigAddUtil {
  def sum(adder: UIntAdd)(x: SInt, y: SInt): SInt = {
    val w = x.getWidth
    // Sign extend both operands to w + 1 bits before the unsigned add, then keep the low w + 1
    // bits, which are the correct two's complement sum.
    adder.io.src1  := x.pad(w + 1).asUInt
    adder.io.src2  := y.pad(w + 1).asUInt
    adder.io.carry := false.B
    adder.io.output(w, 0).asSInt
  }
}

/**
  * A ripple carry wide adder, the cheapest option.
  */
private[fp] object FpSigAddRipple extends FpSigAdd {
  def apply(x: SInt, y: SInt): SInt =
    FpSigAddUtil.sum(Module(new UIntRippleAdd(x.getWidth + 1)))(x, y)
}

/**
  * A parallel prefix wide adder, the fast option.
  */
private[fp] object FpSigAddPrefix extends FpSigAdd {
  def apply(x: SInt, y: SInt): SInt =
    FpSigAddUtil.sum(Module(new UIntPrefixAdd(x.getWidth + 1, PrefixStyle.KoggeStone)))(x, y)
}

/**
  * A block carry select wide adder.
  */
private[fp] object FpSigAddCarrySelect extends FpSigAdd {
  def apply(x: SInt, y: SInt): SInt =
    FpSigAddUtil.sum(Module(new UIntCarrySelectAdd(x.getWidth + 1, 4)))(x, y)
}

/**
  * A hierarchical carry lookahead wide adder.
  */
private[fp] object FpSigAddCarryLookahead extends FpSigAdd {
  def apply(x: SInt, y: SInt): SInt =
    FpSigAddUtil.sum(Module(new UIntCarryLookaheadAdd(x.getWidth + 1, 4)))(x, y)
}
