package harith.uint

import chisel3.simulator.scalatest.ChiselSim
import hammer.test._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class UIntCarrySkipAdderSpec extends AnyFreeSpec with Matchers with ChiselSim {

  "UIntCarrySkipAdder" - {
    Seq(1, 4, 5).foreach { blockSize =>
      s"blockSize = $blockSize" - {
        AdderTestUtils.widths.foreach { width =>
          s"width = $width" in Sim(new UIntCarrySkipAdder(width, blockSize)) { dut =>
            Test("edge vectors", dut) { dut =>
              AdderTestUtils.checkCombinational(dut, AdderTestUtils.edgeVectors(width))
            }
            Test("random vectors", dut) { dut =>
              AdderTestUtils.checkCombinational(dut, AdderTestUtils.randomVectors(width, width + blockSize))
            }
          }
        }
      }
    }
  }
}
