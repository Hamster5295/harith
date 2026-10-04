package harith.uint

import chisel3.simulator.scalatest.ChiselSim
import hammer.test._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class UIntPipelinedArrayMultiplierSpec extends AnyFreeSpec with Matchers with ChiselSim {

  "UIntPipelinedArrayMultiplier" - {
    MultiplierTestUtils.pipelinedWidths.foreach { width =>
      Seq(0, 1, 3).foreach { stages =>
        s"width = $width, stages = $stages" in Sim(new UIntPipelinedArrayMultiplier(width, stages)) { dut =>
          Test("edge vectors", dut) { dut =>
            MultiplierTestUtils.checkPipelined(dut, MultiplierTestUtils.edgeVectors(width))
          }
          Test("random vectors", dut) { dut =>
            MultiplierTestUtils.checkPipelined(dut, MultiplierTestUtils.randomVectors(width, width + stages))
          }
        }
      }
    }
  }
}
