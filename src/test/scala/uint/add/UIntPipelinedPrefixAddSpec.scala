package harith.uint

import chisel3.simulator.scalatest.ChiselSim
import hammer.test._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class UIntPipelinedPrefixAddSpec extends AnyFreeSpec with Matchers with ChiselSim {

  private val styles = Seq(
    PrefixStyle.KoggeStone,
    PrefixStyle.BrentKung,
    PrefixStyle.Sklansky,
    PrefixStyle.HanCarlson,
  )

  private val stages = Seq(0, 1, 3)

  "UIntPipelinedPrefixAdd" - {
    styles.foreach { style =>
      s"prefix style = $style" - {
        for (stage <- stages; width <- AddTestUtils.widths) {
          s"width = $width, stages = $stage" in Sim(new UIntPipelinedPrefixAdd(width, style, stage)) { dut =>
            Test("edge vectors", dut) { dut =>
              AddTestUtils.checkPipelined(dut, AddTestUtils.edgeVectors(width))
            }
            Test("random vectors", dut) { dut =>
              AddTestUtils.checkPipelined(dut, AddTestUtils.randomVectors(width, width + stage))
            }
          }
        }
      }
    }
  }
}
