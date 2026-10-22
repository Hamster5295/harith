package harith.fp

import harith.uint.UIntMacroMul
import chisel3.simulator.scalatest.ChiselSim
import hammer.test._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class FpNewtonSqrtSpec extends AnyFreeSpec with Matchers with ChiselSim {

  private val formats = Seq(FpFormat.Fp16, FpFormat.Bf16, FpFormat.Fp32, FpFormat.Fp64)

  "FpNewtonSqrt" - {
    formats.foreach { fmt =>
      s"${fmt.expWidth}_${fmt.manWidth}" in Sim(new FpNewtonSqrt(fmt, fmt, w => new UIntMacroMul(w))) { dut =>
        Test("random vectors", dut) { dut =>
          val vectors = FpUnaryTestUtils.randomPositive(fmt, fmt.manWidth + 11, 30)
          FpUnaryTestUtils.checkFaithful(dut, fmt, fmt, vectors, x =>
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
  }
}
