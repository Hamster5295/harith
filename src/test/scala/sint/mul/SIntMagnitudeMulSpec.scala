package harith.sint

import chisel3.simulator.scalatest.ChiselSim
import hammer.test._
import harith.uint.UIntMacroMul
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class SIntMagnitudeMulSpec extends AnyFreeSpec with Matchers with ChiselSim {

  "SIntMagnitudeMul" - {
    SIntMulTestUtils.widths.foreach { width =>
      s"width = $width" in Sim(new SIntMagnitudeMul(width, w => new UIntMacroMul(w))) { dut =>
        Test("edge vectors", dut) { dut =>
          SIntMulTestUtils.checkCombinational(dut, SIntMulTestUtils.edgeVectors(width))
        }
        Test("random vectors", dut) { dut =>
          SIntMulTestUtils.checkCombinational(dut, SIntMulTestUtils.randomVectors(width, width))
        }
      }
    }
  }
}
