package harith.uint

import chisel3.simulator.scalatest.ChiselSim
import hammer.test._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class UIntPrefixAdderSpec extends AnyFreeSpec with Matchers with ChiselSim {

  private val styles = Seq(
    PrefixStyle.KoggeStone,
    PrefixStyle.BrentKung,
    PrefixStyle.Sklansky,
    PrefixStyle.HanCarlson,
  )

  "UIntPrefixAdder" - {
    styles.foreach { style =>
      s"prefix style = $style" - {
        AdderTestUtils.widths.foreach { width =>
          s"width = $width" in Sim(new UIntPrefixAdder(width, style)) { dut =>
            Test("edge vectors", dut) { dut =>
              AdderTestUtils.checkCombinational(dut, AdderTestUtils.edgeVectors(width))
            }
            Test("random vectors", dut) { dut =>
              AdderTestUtils.checkCombinational(dut, AdderTestUtils.randomVectors(width, width))
            }
          }
        }
      }
    }
  }
}
