package harith.uint

import chisel3.simulator.scalatest.ChiselSim
import hammer.test._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class UIntMacroFmaSpec extends AnyFreeSpec with Matchers with ChiselSim {

  "UIntMacroFma" - {
    FmaTestUtils.widths.foreach { width =>
      s"width = $width" in Sim(new UIntMacroFma(width)) { dut =>
        Test("edge vectors", dut) { dut =>
          FmaTestUtils.checkCombinational(dut, FmaTestUtils.edgeVectors(width))
        }
        Test("random vectors", dut) { dut =>
          FmaTestUtils.checkCombinational(dut, FmaTestUtils.randomVectors(width, width))
        }
      }
    }
  }
}
