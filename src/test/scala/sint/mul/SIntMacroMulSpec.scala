package harith.sint

import chisel3.simulator.scalatest.ChiselSim
import hammer.test._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class SIntMacroMulSpec extends AnyFreeSpec with Matchers with ChiselSim {

  "SIntMacroMul" - {
    SIntMulTestUtils.widths.foreach { width =>
      s"width = $width" in Sim(new SIntMacroMul(width)) { dut =>
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
