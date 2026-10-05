package harith.uint

import chisel3.simulator.scalatest.ChiselSim
import hammer.test._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class UIntPrefixAddSpec extends AnyFreeSpec with Matchers with ChiselSim {

  private val styles = Seq(
    PrefixStyle.KoggeStone,
    PrefixStyle.BrentKung,
    PrefixStyle.Sklansky,
    PrefixStyle.HanCarlson,
  )

  "UIntPrefixAdd" - {
    styles.foreach { style =>
      s"prefix style = $style" - {
        AddTestUtils.widths.foreach { width =>
          s"width = $width" in Sim(new UIntPrefixAdd(width, style)) { dut =>
            Test("edge vectors", dut) { dut =>
              AddTestUtils.checkCombinational(dut, AddTestUtils.edgeVectors(width))
            }
            Test("random vectors", dut) { dut =>
              AddTestUtils.checkCombinational(dut, AddTestUtils.randomVectors(width, width))
            }
          }
        }
      }
    }
  }
}
