package harith.uint

import chisel3.simulator.scalatest.ChiselSim
import hammer.test._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class UIntRippleAddSpec extends AnyFreeSpec with Matchers with ChiselSim {

  "UIntRippleAdd" - {
    AddTestUtils.widths.foreach { width =>
      s"width = $width" in Sim(new UIntRippleAdd(width)) { dut =>
        Test("edge vectors", dut) { dut =>
          AddTestUtils.checkCombinational(dut, AddTestUtils.edgeVectors(width))
        }
        Test("random vectors", dut) { dut =>
          AddTestUtils.checkCombinational(dut, AddTestUtils.randomVectors(width, width))
        }
      }
    }
  }
}
