package harith.fp

import harith.uint.UIntMacroMul
import chisel3.simulator.scalatest.ChiselSim
import hammer.test._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class FpPipelinedNewtonRsqrtSpec extends AnyFreeSpec with Matchers with ChiselSim {

  private val formats = Seq(FpFormat.Fp16, FpFormat.Bf16, FpFormat.Fp32, FpFormat.Fp64)

  "FpPipelinedNewtonRsqrt" - {
    formats.foreach { fmt =>
      s"${fmt.expWidth}_${fmt.manWidth}" in Sim(new FpPipelinedNewtonRsqrt(fmt, fmt, w => new UIntMacroMul(w))) { dut =>
        Test("stream", dut) { dut =>
          val vectors = FpUnaryTestUtils.randomPositive(fmt, fmt.manWidth + 13, 40)
          FpUnaryTestUtils.checkStream(dut, fmt, fmt, vectors, x =>
            FpUnaryTestUtils.rsqrtRef(x, fmt, fmt),
          faithful = true)
        }
        Test("special values", dut) { dut =>
          FpUnaryTestUtils.checkSpecial(dut, FpUnaryTestUtils.zeroBits(fmt), fmt, fmt, FpUnaryTestUtils.infBits(fmt), dz = true)
          FpUnaryTestUtils.checkSpecial(dut, FpUnaryTestUtils.zeroBits(fmt, true), fmt, fmt, FpUnaryTestUtils.infBits(fmt, true), dz = true)
          FpUnaryTestUtils.checkSpecial(dut, FpUnaryTestUtils.infBits(fmt), fmt, fmt, FpUnaryTestUtils.zeroBits(fmt))
          FpUnaryTestUtils.checkSpecial(dut, FpUnaryTestUtils.infBits(fmt, true), fmt, fmt, FpUnaryTestUtils.canonicalNaNBits(fmt), nv = true)
          FpUnaryTestUtils.checkSpecial(dut, FpUnaryTestUtils.nanBits(fmt), fmt, fmt, FpUnaryTestUtils.canonicalNaNBits(fmt))
        }
      }
    }
  }
}
