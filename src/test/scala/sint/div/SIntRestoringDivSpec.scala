package harith.sint

import chisel3.simulator.scalatest.ChiselSim
import hammer.test._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class SIntRestoringDivSpec extends AnyFreeSpec with Matchers with ChiselSim {

  "SIntRestoringDiv" - {
    SIntDivTestUtils.widths.foreach { width =>
      s"width = $width" in Sim(new SIntRestoringDiv(width)) { dut =>
        Test("edge vectors", dut) { dut =>
          SIntDivTestUtils.check(dut, SIntDivTestUtils.edgeVectors(width))
        }
        Test("random vectors", dut) { dut =>
          SIntDivTestUtils.check(dut, SIntDivTestUtils.randomVectors(width, width))
        }
        Test("flush", dut) { dut =>
          val max = (BigInt(1) << (width - 1)) - 1
          SIntDivTestUtils.checkFlush(dut, max, if (width == 1) BigInt(-1) else BigInt(1))
        }
      }
    }
  }
}
