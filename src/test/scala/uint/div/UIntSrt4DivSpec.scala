package harith.uint

import chisel3.simulator.scalatest.ChiselSim
import hammer.test._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class UIntSrt4DivSpec extends AnyFreeSpec with Matchers with ChiselSim {

  "UIntSrt4Div" - {
    DivTestUtils.widths.foreach { width =>
      s"width = $width" in Sim(new UIntSrt4Div(width)) { dut =>
        Test("edge vectors", dut) { dut =>
          DivTestUtils.check(dut, DivTestUtils.edgeVectors(width))
        }
        Test("random vectors", dut) { dut =>
          DivTestUtils.check(dut, DivTestUtils.randomVectors(width, width + 4))
        }
        Test("flush aborts", dut) { dut =>
          DivTestUtils.checkFlush(dut, (BigInt(1) << width) - 1, BigInt(1))
        }
      }
    }
  }
}
