package harith.fp

import chisel3._
import chisel3.simulator.PeekPokeAPI._
import chisel3.simulator.scalatest.ChiselSim
import hammer.test._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

object FpConvertSpec {
  import FpFmaSpec.{bf16ToFloat, fp16ToFloat, randomBits}

  def check(
      dut:     FpConvert,
      inFmt:   FpFormat,
      outFmt:  FpFormat,
      vectors: Seq[BigInt],
      ref:     BigInt => BigInt,
  ): Unit =
    vectors.foreach { a =>
      dut.io.src.poke(a.U(inFmt.width.W))
      dut.io.rm.poke(0.U(3.W))
      dut.io.output.expect(ref(a).U(outFmt.width.W), s"a=$a")
      dut.clock.step()
    }
}

class FpConvertSpec extends AnyFreeSpec with Matchers with ChiselSim {
  import FpConvertSpec._
  import FpFmaSpec.{bf16ToFloat, fp16ToFloat, randomBits}

  "Fp16ToFp32" - {
    "is exact" in Sim(new FpGenericConvert(FpFormat.Fp16, FpFormat.Fp32)) { dut =>
      Test("random vectors", dut) { dut =>
        check(dut, FpFormat.Fp16, FpFormat.Fp32, randomBits(FpFormat.Fp16, 300), a => {
          val f = fp16ToFloat(a)
          if (java.lang.Float.isNaN(f)) BigInt(0x7fc00000L)
          else BigInt(java.lang.Float.floatToRawIntBits(f) & 0xffffffffL)
        })
      }
    }
  }

  "Bf16ToFp32" - {
    "is exact" in Sim(new FpGenericConvert(FpFormat.Bf16, FpFormat.Fp32)) { dut =>
      Test("random vectors", dut) { dut =>
        check(dut, FpFormat.Bf16, FpFormat.Fp32, randomBits(FpFormat.Bf16, 300), a => {
          val f = bf16ToFloat(a)
          if (java.lang.Float.isNaN(f)) BigInt(0x7fc00000L)
          else BigInt(java.lang.Float.floatToRawIntBits(f) & 0xffffffffL)
        })
      }
    }
  }

  "Fp32ToFp64" - {
    "is exact" in Sim(new FpGenericConvert(FpFormat.Fp32, FpFormat.Fp64)) { dut =>
      Test("random vectors", dut) { dut =>
        check(dut, FpFormat.Fp32, FpFormat.Fp64, randomBits(FpFormat.Fp32, 300), a => {
          val f = java.lang.Float.intBitsToFloat(a.toInt)
          if (java.lang.Float.isNaN(f)) BigInt(0x7ff8000000000000L)
          else BigInt(java.lang.Double.doubleToRawLongBits(f.toDouble)) & ((BigInt(1) << 64) - 1)
        })
      }
    }
  }

  "Fp64ToFp32" - {
    "rounds to nearest" in Sim(new FpGenericConvert(FpFormat.Fp64, FpFormat.Fp32)) { dut =>
      Test("random vectors", dut) { dut =>
        check(dut, FpFormat.Fp64, FpFormat.Fp32, randomBits(FpFormat.Fp64, 300), a => {
          val r = java.lang.Double.longBitsToDouble(a.toLong).toFloat
          if (java.lang.Float.isNaN(r)) BigInt(0x7fc00000L)
          else BigInt(java.lang.Float.floatToRawIntBits(r) & 0xffffffffL)
        })
      }
    }
  }
}
