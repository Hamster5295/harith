package harith.fp

import chisel3.simulator.scalatest.ChiselSim
import hammer.test._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class FpRestoringSqrtSpec extends AnyFreeSpec with Matchers with ChiselSim {

  private val formats = Seq(FpFormat.Fp16, FpFormat.Bf16, FpFormat.Fp32, FpFormat.Fp64)

  "FpRestoringSqrt" - {
    formats.foreach { fmt =>
      s"${fmt.expWidth}_${fmt.manWidth}" in Sim(new FpRestoringSqrt(fmt, fmt)) { dut =>
        Test("random vectors", dut) { dut =>
          val vectors = FpUnaryTestUtils.randomPositive(fmt, fmt.manWidth + 1, 30)
          FpUnaryTestUtils.check(dut, fmt, fmt, vectors, x =>
            FpUnaryTestUtils.sqrtRef(x, fmt, fmt),
          )
        }
        Test("special values", dut) { dut =>
          FpUnaryTestUtils.checkSpecial(dut, FpUnaryTestUtils.zeroBits(fmt), fmt, fmt, FpUnaryTestUtils.zeroBits(fmt))
          FpUnaryTestUtils.checkSpecial(dut, FpUnaryTestUtils.zeroBits(fmt, true), fmt, fmt, FpUnaryTestUtils.zeroBits(fmt, true))
          FpUnaryTestUtils.checkSpecial(dut, FpUnaryTestUtils.infBits(fmt), fmt, fmt, FpUnaryTestUtils.infBits(fmt))
          FpUnaryTestUtils.checkSpecial(dut, FpUnaryTestUtils.infBits(fmt, true), fmt, fmt, FpUnaryTestUtils.canonicalNaNBits(fmt), nv = true)
          FpUnaryTestUtils.checkSpecial(dut, FpUnaryTestUtils.oneBits(fmt, true), fmt, fmt, FpUnaryTestUtils.canonicalNaNBits(fmt), nv = true)
          FpUnaryTestUtils.checkSpecial(dut, FpUnaryTestUtils.nanBits(fmt), fmt, fmt, FpUnaryTestUtils.canonicalNaNBits(fmt))
        }
      }
    }

    "bf16 to fp32" in Sim(new FpRestoringSqrt(FpFormat.Bf16, FpFormat.Fp32)) { dut =>
      Test("random vectors", dut) { dut =>
        val vectors = FpUnaryTestUtils.randomPositive(FpFormat.Bf16, 8, 40)
        FpUnaryTestUtils.check(dut, FpFormat.Bf16, FpFormat.Fp32, vectors, x =>
          FpUnaryTestUtils.sqrtRef(x, FpFormat.Bf16, FpFormat.Fp32),
        )
      }
    }
  }
}
