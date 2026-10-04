package harith.uint

import chisel3.simulator.scalatest.ChiselSim
import hammer.test._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class UIntCarryLookaheadAdderSpec extends AnyFreeSpec with Matchers with ChiselSim {

  "UIntCarryLookaheadAdder" - {
    Seq(1, 4, 5).foreach { groupSize =>
      s"groupSize = $groupSize" - {
        AdderTestUtils.widths.foreach { width =>
          s"width = $width" in Sim(new UIntCarryLookaheadAdder(width, groupSize)) { dut =>
            Test("edge vectors", dut) { dut =>
              AdderTestUtils.checkCombinational(dut, AdderTestUtils.edgeVectors(width))
            }
            Test("random vectors", dut) { dut =>
              AdderTestUtils.checkCombinational(dut, AdderTestUtils.randomVectors(width, width + groupSize))
            }
          }
        }
      }
    }
  }
}
