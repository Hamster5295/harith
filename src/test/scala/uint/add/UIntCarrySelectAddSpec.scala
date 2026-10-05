package harith.uint

import chisel3.simulator.scalatest.ChiselSim
import hammer.test._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class UIntCarrySelectAddSpec extends AnyFreeSpec with Matchers with ChiselSim {

  "UIntCarrySelectAdd" - {
    Seq(1, 4, 5).foreach { blockSize =>
      s"blockSize = $blockSize" - {
        AddTestUtils.widths.foreach { width =>
          s"width = $width" in Sim(new UIntCarrySelectAdd(width, blockSize)) { dut =>
            Test("edge vectors", dut) { dut =>
              AddTestUtils.checkCombinational(dut, AddTestUtils.edgeVectors(width))
            }
            Test("random vectors", dut) { dut =>
              AddTestUtils.checkCombinational(dut, AddTestUtils.randomVectors(width, width + blockSize))
            }
          }
        }
      }
    }
  }
}
