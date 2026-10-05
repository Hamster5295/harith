package harith.fp

import chisel3._
import chisel3.simulator.PeekPokeAPI._
import chisel3.simulator.scalatest.ChiselSim
import hammer.test._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

object FpAddSpec {
  import FpFmaSpec.{bf16ToFloat, fp16ToFloat, randomBits, toFloat}

  def addFp32Ref(a: BigInt, b: BigInt, aFmt: FpFormat, bFmt: FpFormat): BigInt = {
    val r = toFloat(a, aFmt) + toFloat(b, bFmt)
    if (java.lang.Float.isNaN(r)) BigInt(0x7fc00000L)
    else BigInt(java.lang.Float.floatToRawIntBits(r) & 0xffffffffL)
  }

  def addFp64Ref(a: BigInt, b: BigInt): BigInt = {
    val r = java.lang.Double.longBitsToDouble(a.toLong) + java.lang.Double.longBitsToDouble(b.toLong)
    if (java.lang.Double.isNaN(r)) BigInt(0x7ff8000000000000L)
    else BigInt(java.lang.Double.doubleToRawLongBits(r)) & ((BigInt(1) << 64) - 1)
  }

  def check(
      dut:     FpAdd,
      aFmt:    FpFormat,
      bFmt:    FpFormat,
      outFmt:  FpFormat,
      vectors: Seq[(BigInt, BigInt)],
      ref:     (BigInt, BigInt) => BigInt,
  ): Unit =
    vectors.foreach { case (a, b) =>
      dut.io.src1.poke(a.U(aFmt.width.W))
      dut.io.src2.poke(b.U(bFmt.width.W))
      dut.io.rm.poke(0.U(3.W))
      dut.io.output.expect(ref(a, b).U(outFmt.width.W), s"a=$a b=$b")
      dut.clock.step()
    }
}

class FpAddSpec extends AnyFreeSpec with Matchers with ChiselSim {
  import FpAddSpec._
  import FpFmaSpec.randomBits

  "Fp32Add" - {
    "matches IEEE single precision" in Sim(new Fp32Add) { dut =>
      Test("random vectors", dut) { dut =>
        val a = randomBits(FpFormat.Fp32, 300)
        val b = randomBits(FpFormat.Fp32, 300)
        check(dut, FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, a.zip(b), (x, y) => addFp32Ref(x, y, FpFormat.Fp32, FpFormat.Fp32))
      }
    }
  }

  "Fp64Add" - {
    "matches IEEE double precision" in Sim(new Fp64Add) { dut =>
      Test("random vectors", dut) { dut =>
        val a = randomBits(FpFormat.Fp64, 150)
        val b = randomBits(FpFormat.Fp64, 150)
        check(dut, FpFormat.Fp64, FpFormat.Fp64, FpFormat.Fp64, a.zip(b), addFp64Ref)
      }
    }
  }

  "Fp16Fp32Add" - {
    "matches single precision" in Sim(new Fp16Fp32Add) { dut =>
      Test("random vectors", dut) { dut =>
        val a = randomBits(FpFormat.Fp16, 300)
        val b = randomBits(FpFormat.Fp32, 300)
        check(dut, FpFormat.Fp16, FpFormat.Fp32, FpFormat.Fp32, a.zip(b), (x, y) => addFp32Ref(x, y, FpFormat.Fp16, FpFormat.Fp32))
      }
    }
  }

  "FpBf16Fp32Add" - {
    "matches single precision" in Sim(new FpBf16Fp32Add) { dut =>
      Test("random vectors", dut) { dut =>
        val a = randomBits(FpFormat.Bf16, 300)
        val b = randomBits(FpFormat.Fp32, 300)
        check(dut, FpFormat.Bf16, FpFormat.Fp32, FpFormat.Fp32, a.zip(b), (x, y) => addFp32Ref(x, y, FpFormat.Bf16, FpFormat.Fp32))
      }
    }
  }
}
