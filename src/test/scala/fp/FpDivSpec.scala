package harith.fp

import chisel3._
import chisel3.simulator.PeekPokeAPI._
import chisel3.simulator.scalatest.ChiselSim
import hammer.test._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

object FpDivSpec {
  import FpFmaSpec.randomBits

  def divFp32Ref(a: BigInt, b: BigInt, aFmt: FpFormat, bFmt: FpFormat): BigInt = {
    val r = FpFmaSpec.toFloat(a, aFmt) / FpFmaSpec.toFloat(b, bFmt)
    if (java.lang.Float.isNaN(r)) BigInt(0x7fc00000L)
    else BigInt(java.lang.Float.floatToRawIntBits(r) & 0xffffffffL)
  }

  def divFp64Ref(a: BigInt, b: BigInt): BigInt = {
    val r = java.lang.Double.longBitsToDouble(a.toLong) / java.lang.Double.longBitsToDouble(b.toLong)
    if (java.lang.Double.isNaN(r)) BigInt(0x7ff8000000000000L)
    else BigInt(java.lang.Double.doubleToRawLongBits(r)) & ((BigInt(1) << 64) - 1)
  }

  def request(dut: FpDiv, a: BigInt, b: BigInt, aFmt: FpFormat, bFmt: FpFormat): Unit = {
    dut.io.in.valid.poke(true.B)
    dut.io.in.bits.src1.poke(a.U(aFmt.width.W))
    dut.io.in.bits.src2.poke(b.U(bFmt.width.W))
    dut.io.in.bits.rm.poke(0.U(3.W))
    var guard = 0
    while (!dut.io.in.ready.peekBoolean()) {
      dut.clock.step()
      guard += 1
      require(guard <= 4 * dut.latency + 32, "the divider never became ready")
    }
    dut.clock.step()
    dut.io.in.valid.poke(false.B)
    guard = 0
    while (!dut.io.out.valid.peekBoolean()) {
      dut.clock.step()
      guard += 1
      require(guard <= 4 * dut.latency + 32, "the divider never produced a valid response")
    }
  }

  def check(
      dut:     FpDiv,
      aFmt:    FpFormat,
      bFmt:    FpFormat,
      outFmt:  FpFormat,
      vectors: Seq[(BigInt, BigInt)],
      ref:     (BigInt, BigInt) => BigInt,
  ): Unit =
    vectors.foreach { case (a, b) =>
      request(dut, a, b, aFmt, bFmt)
      dut.io.out.bits.output.expect(ref(a, b).U(outFmt.width.W), s"a=$a b=$b")
    }
}

class FpDivSpec extends AnyFreeSpec with Matchers with ChiselSim {
  import FpDivSpec._
  import FpFmaSpec.randomBits

  "FpRestoringDiv fp32" - {
    "matches IEEE single precision" in Sim(new FpRestoringDiv(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32)) { dut =>
      Test("random vectors", dut) { dut =>
        val a = randomBits(FpFormat.Fp32, 60)
        val b = randomBits(FpFormat.Fp32, 60)
        check(dut, FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, a.zip(b), (x, y) => divFp32Ref(x, y, FpFormat.Fp32, FpFormat.Fp32))
      }
    }
  }

  "FpRestoringDiv fp64" - {
    "matches IEEE double precision" in Sim(new FpRestoringDiv(FpFormat.Fp64, FpFormat.Fp64, FpFormat.Fp64)) { dut =>
      Test("random vectors", dut) { dut =>
        val a = randomBits(FpFormat.Fp64, 30)
        val b = randomBits(FpFormat.Fp64, 30)
        check(dut, FpFormat.Fp64, FpFormat.Fp64, FpFormat.Fp64, a.zip(b), divFp64Ref)
      }
    }
  }

  "FpRestoringDiv fp16 to fp32" - {
    "matches single precision" in Sim(new FpRestoringDiv(FpFormat.Fp16, FpFormat.Fp16, FpFormat.Fp32)) { dut =>
      Test("random vectors", dut) { dut =>
        val a = randomBits(FpFormat.Fp16, 60)
        val b = randomBits(FpFormat.Fp16, 60)
        check(dut, FpFormat.Fp16, FpFormat.Fp16, FpFormat.Fp32, a.zip(b), (x, y) => divFp32Ref(x, y, FpFormat.Fp16, FpFormat.Fp16))
      }
    }
  }
}
