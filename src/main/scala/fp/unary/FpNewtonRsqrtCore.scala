package harith.fp

import chisel3._
import chisel3.util._
import harith.uint.UIntMul

/**
  * The fixed-point Newton-Raphson reciprocal square root core.
  *
  * The residual `x * z^2` is driven towards one with `z <- z * (3 - x * z^2) / 2`. All values use
  * `P` fractional bits; the supplied multiplier must be combinational.
  *
  * @param P        The number of fractional bits
  * @param k        The number of mantissa bits used to index the seed table
  * @param seedFrac The number of fractional bits in the seed table
  * @param table    The seed table
  * @param iters    The number of Newton-Raphson iterations
  * @param mul      The combinational unsigned multiplier factory
  */
private[fp] class FpNewtonRsqrtCore(
    P:        Int,
    k:        Int,
    seedFrac: Int,
    table:    Seq[BigInt],
    iters:    Int,
    mul:      Int => UIntMul,
) extends Module {
  private val W = P + 2

  val io = IO(new Bundle {
    val x     = Input(UInt(W.W))
    val index = Input(UInt((k + 2).W))
    val z     = Output(UInt(W.W))
  })

  val seedVec = VecInit(table.map(_.U((seedFrac + 1).W)))
  var z       = (seedVec(io.index) << (P - seedFrac)).pad(W)

  for (_ <- 0 until iters) {
    val m1 = Module(mul(W))
    m1.io.src1 := z
    m1.io.src2 := z
    val z2 = (m1.io.output >> P)(W - 1, 0)

    val m2 = Module(mul(W))
    m2.io.src1 := io.x
    m2.io.src2 := z2
    val xz2 = (m2.io.output >> P)(W - 1, 0)

    val t = (BigInt(3) << P).U(W.W) - xz2

    val m3 = Module(mul(W))
    m3.io.src1 := z
    m3.io.src2 := t
    z = (m3.io.output >> (P + 1))(W - 1, 0)
  }

  io.z := z
}
