package harith.uint

import chisel3.simulator.scalatest.ChiselSim
import hammer.test._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class UIntPipelinedTreeMultiplierSpec extends AnyFreeSpec with Matchers with ChiselSim {

  private val styles = Seq(ReductionStyle.Wallace, ReductionStyle.Dadda)

  "UIntPipelinedTreeMultiplier" - {
    styles.foreach { style =>
      s"reduction style = $style" - {
        MultiplierTestUtils.pipelinedWidths.foreach { width =>
          Seq(1, 3).foreach { stages =>
            s"width = $width, stages = $stages" in Sim(
              new UIntPipelinedTreeMultiplier(
                width,
                style,
                new UIntPipelinedPrefixAdder(width * 2, PrefixStyle.KoggeStone, 0),
                stages,
              ),
            ) { dut =>
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
  }

  "UIntPipelinedTreeMultiplier with a pipelined final adder" - {
    "width = 8, stages = 1, adder stages = 1" in Sim(
      new UIntPipelinedTreeMultiplier(
        8,
        ReductionStyle.Wallace,
        new UIntPipelinedPrefixAdder(16, PrefixStyle.KoggeStone, 1),
        1,
      ),
    ) { dut =>
      Test("edge vectors", dut) { dut =>
        MultiplierTestUtils.checkPipelined(dut, MultiplierTestUtils.edgeVectors(8))
      }
      Test("random vectors", dut) { dut =>
        MultiplierTestUtils.checkPipelined(dut, MultiplierTestUtils.randomVectors(8, 8))
      }
    }
  }
}
