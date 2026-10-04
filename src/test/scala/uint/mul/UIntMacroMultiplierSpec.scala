package harith.uint

import chisel3.simulator.scalatest.ChiselSim
import hammer.test._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class UIntMacroMultiplierSpec extends AnyFreeSpec with Matchers with ChiselSim {

  "UIntMacroMultiplier" - {
    MultiplierTestUtils.widths.foreach { width =>
      s"width = $width" in Sim(new UIntMacroMultiplier(width)) { dut =>
        Test("edge vectors", dut) { dut =>
          MultiplierTestUtils.checkCombinational(dut, MultiplierTestUtils.edgeVectors(width))
        }
        Test("random vectors", dut) { dut =>
          MultiplierTestUtils.checkCombinational(dut, MultiplierTestUtils.randomVectors(width, width))
        }
      }
    }
  }
}
