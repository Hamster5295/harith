package harith.uint

import chisel3.simulator.scalatest.ChiselSim
import hammer.test._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class UIntArrayMultiplierSpec extends AnyFreeSpec with Matchers with ChiselSim {

  "UIntArrayMultiplier" - {
    MultiplierTestUtils.widths.foreach { width =>
      s"width = $width" in Sim(new UIntArrayMultiplier(width)) { dut =>
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
