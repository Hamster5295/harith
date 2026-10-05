package harith.uint

import chisel3.simulator.scalatest.ChiselSim
import hammer.test._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class UIntCarryLookaheadAddSpec extends AnyFreeSpec with Matchers with ChiselSim {

  "UIntCarryLookaheadAdd" - {
    Seq(1, 4, 5).foreach { groupSize =>
      s"groupSize = $groupSize" - {
        AddTestUtils.widths.foreach { width =>
          s"width = $width" in Sim(new UIntCarryLookaheadAdd(width, groupSize)) { dut =>
            Test("edge vectors", dut) { dut =>
              AddTestUtils.checkCombinational(dut, AddTestUtils.edgeVectors(width))
            }
            Test("random vectors", dut) { dut =>
              AddTestUtils.checkCombinational(dut, AddTestUtils.randomVectors(width, width + groupSize))
            }
          }
        }
      }
    }
  }
}
