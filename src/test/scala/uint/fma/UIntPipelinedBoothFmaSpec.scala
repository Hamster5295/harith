package harith.uint

import chisel3.simulator.scalatest.ChiselSim
import hammer.test._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class UIntPipelinedBoothFmaSpec extends AnyFreeSpec with Matchers with ChiselSim {

  private val styles = Seq(ReductionStyle.Wallace, ReductionStyle.Dadda)

  "UIntPipelinedBoothFma" - {
    styles.foreach { style =>
      s"reduction style = $style" - {
        FmaTestUtils.pipelinedWidths.foreach { width =>
          Seq(1, 3).foreach { stages =>
            s"width = $width, stages = $stages" in Sim(
              new UIntPipelinedBoothFma(
                width,
                style,
                new UIntPipelinedPrefixAdder(2 * width + 1, PrefixStyle.KoggeStone, 0),
                stages,
              ),
            ) { dut =>
              Test("edge vectors", dut) { dut =>
                FmaTestUtils.checkPipelined(dut, FmaTestUtils.edgeVectors(width))
              }
              Test("random vectors", dut) { dut =>
                FmaTestUtils.checkPipelined(dut, FmaTestUtils.randomVectors(width, width + stages + 1))
              }
            }
          }
        }
      }
    }
  }
}
