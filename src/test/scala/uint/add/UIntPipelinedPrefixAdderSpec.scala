package harith.uint

import chisel3.simulator.scalatest.ChiselSim
import hammer.test._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class UIntPipelinedPrefixAdderSpec extends AnyFreeSpec with Matchers with ChiselSim {

  private val styles = Seq(
    PrefixStyle.KoggeStone,
    PrefixStyle.BrentKung,
    PrefixStyle.Sklansky,
    PrefixStyle.HanCarlson,
  )

  private val stages = Seq(0, 1, 3)

  "UIntPipelinedPrefixAdder" - {
    styles.foreach { style =>
      s"prefix style = $style" - {
        for (stage <- stages; width <- AdderTestUtils.widths) {
          s"width = $width, stages = $stage" in Sim(new UIntPipelinedPrefixAdder(width, style, stage)) { dut =>
            Test("edge vectors", dut) { dut =>
              AdderTestUtils.checkPipelined(dut, AdderTestUtils.edgeVectors(width))
            }
            Test("random vectors", dut) { dut =>
              AdderTestUtils.checkPipelined(dut, AdderTestUtils.randomVectors(width, width + stage))
            }
          }
        }
      }
    }
  }
}
