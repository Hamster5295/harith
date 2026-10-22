package harith.uint

import chisel3.simulator.scalatest.ChiselSim
import hammer.test._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class UIntBoothFmaSpec extends AnyFreeSpec with Matchers with ChiselSim {

  private val styles = Seq(ReductionStyle.Wallace, ReductionStyle.Dadda)

  "UIntBoothFma" - {
    styles.foreach { style =>
      s"reduction style = $style" - {
        FmaTestUtils.widths.foreach { width =>
          s"width = $width" in Sim(
            new UIntBoothFma(width, style, w => new UIntPrefixAdd(w, PrefixStyle.KoggeStone)),
          ) { dut =>
            Test("edge vectors", dut) { dut =>
              FmaTestUtils.checkCombinational(dut, FmaTestUtils.edgeVectors(width))
            }
            Test("random vectors", dut) { dut =>
              FmaTestUtils.checkCombinational(dut, FmaTestUtils.randomVectors(width, width + 4))
            }
          }
        }
      }
    }
  }
}
