package harith.uint

import chisel3.simulator.scalatest.ChiselSim
import hammer.test._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class UIntSrt4DividerSpec extends AnyFreeSpec with Matchers with ChiselSim {

  "UIntSrt4Divider" - {
    DividerTestUtils.widths.foreach { width =>
      s"width = $width" in Sim(new UIntSrt4Divider(width)) { dut =>
        Test("edge vectors", dut) { dut =>
          DividerTestUtils.check(dut, DividerTestUtils.edgeVectors(width))
        }
        Test("random vectors", dut) { dut =>
          DividerTestUtils.check(dut, DividerTestUtils.randomVectors(width, width + 4))
        }
        Test("flush aborts", dut) { dut =>
          DividerTestUtils.checkFlush(dut, (BigInt(1) << width) - 1, BigInt(1))
        }
      }
    }
  }
}
