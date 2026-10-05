package harith.uint

import chisel3.simulator.scalatest.ChiselSim
import hammer.test._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class UIntPipelinedTreeMulSpec extends AnyFreeSpec with Matchers with ChiselSim {

  private val styles = Seq(ReductionStyle.Wallace, ReductionStyle.Dadda)

  "UIntPipelinedTreeMul" - {
    styles.foreach { style =>
      s"reduction style = $style" - {
        MulTestUtils.pipelinedWidths.foreach { width =>
          Seq(1, 3).foreach { stages =>
            s"width = $width, stages = $stages" in Sim(
              new UIntPipelinedTreeMul(
                width,
                style,
                new UIntPipelinedPrefixAdd(width * 2, PrefixStyle.KoggeStone, 0),
                stages,
              ),
            ) { dut =>
              Test("edge vectors", dut) { dut =>
                MulTestUtils.checkPipelined(dut, MulTestUtils.edgeVectors(width))
              }
              Test("random vectors", dut) { dut =>
                MulTestUtils.checkPipelined(dut, MulTestUtils.randomVectors(width, width + stages))
              }
            }
          }
        }
      }
    }
  }

  "UIntPipelinedTreeMul with a pipelined final adder" - {
    "width = 8, stages = 1, adder stages = 1" in Sim(
      new UIntPipelinedTreeMul(
        8,
        ReductionStyle.Wallace,
        new UIntPipelinedPrefixAdd(16, PrefixStyle.KoggeStone, 1),
        1,
      ),
    ) { dut =>
      Test("edge vectors", dut) { dut =>
        MulTestUtils.checkPipelined(dut, MulTestUtils.edgeVectors(8))
      }
      Test("random vectors", dut) { dut =>
        MulTestUtils.checkPipelined(dut, MulTestUtils.randomVectors(8, 8))
      }
    }
  }
}
