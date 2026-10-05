package harith.uint

import chisel3.simulator.scalatest.ChiselSim
import hammer.test._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class UIntPipelinedRippleAddSpec extends AnyFreeSpec with Matchers with ChiselSim {

  "UIntPipelinedRippleAdd" - {
    Seq(1, 4, 5).foreach { blockSize =>
      s"blockSize = $blockSize" - {
        AddTestUtils.widths.foreach { width =>
          s"width = $width" in Sim(new UIntPipelinedRippleAdd(width, blockSize)) { dut =>
            Test("edge vectors", dut) { dut =>
              AddTestUtils.checkPipelined(dut, AddTestUtils.edgeVectors(width))
            }
            Test("random vectors", dut) { dut =>
              AddTestUtils.checkPipelined(dut, AddTestUtils.randomVectors(width, width + blockSize))
            }
          }
        }
      }
    }
  }
}
