package harith.uint

import chisel3.simulator.scalatest.ChiselSim
import hammer.test._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class UIntTreeMultiplierSpec extends AnyFreeSpec with Matchers with ChiselSim {

  private val styles = Seq(ReductionStyle.Wallace, ReductionStyle.Dadda)

  "UIntTreeMultiplier" - {
    styles.foreach { style =>
      s"reduction style = $style" - {
        MultiplierTestUtils.widths.foreach { width =>
          s"width = $width" in Sim(
            new UIntTreeMultiplier(width, style, new UIntPrefixAdder(width * 2, PrefixStyle.KoggeStone)),
          ) { dut =>
            Test("edge vectors", dut) { dut =>
              MultiplierTestUtils.checkCombinational(dut, MultiplierTestUtils.edgeVectors(width))
            }
            Test("random vectors", dut) { dut =>
              MultiplierTestUtils.checkCombinational(dut, MultiplierTestUtils.randomVectors(width, width))
            }
          }
        }
      }
    }
  }
}
