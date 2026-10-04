package harith.uint

import chisel3.simulator.scalatest.ChiselSim
import hammer.test._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class UIntSrt2DividerSpec extends AnyFreeSpec with Matchers with ChiselSim {

  "UIntSrt2Divider" - {
    DividerTestUtils.widths.foreach { width =>
      s"width = $width" in Sim(new UIntSrt2Divider(width)) { dut =>
        Test("edge vectors", dut) { dut =>
          DividerTestUtils.check(dut, DividerTestUtils.edgeVectors(width))
        }
        Test("random vectors", dut) { dut =>
          DividerTestUtils.check(dut, DividerTestUtils.randomVectors(width, width + 2))
        }
        Test("flush aborts", dut) { dut =>
          DividerTestUtils.checkFlush(dut, (BigInt(1) << width) - 1, BigInt(1))
        }
      }
    }
  }
}
