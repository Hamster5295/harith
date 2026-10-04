package harith.uint

import chisel3.simulator.scalatest.ChiselSim
import hammer.test._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class UIntMacroAdderSpec extends AnyFreeSpec with Matchers with ChiselSim {

  "UIntMacroAdder" - {
    AdderTestUtils.widths.foreach { width =>
      s"width = $width" in Sim(new UIntMacroAdder(width)) { dut =>
        Test("edge vectors", dut) { dut =>
          AdderTestUtils.checkCombinational(dut, AdderTestUtils.edgeVectors(width))
        }
        Test("random vectors", dut) { dut =>
          AdderTestUtils.checkCombinational(dut, AdderTestUtils.randomVectors(width, width))
        }
      }
    }
  }
}
