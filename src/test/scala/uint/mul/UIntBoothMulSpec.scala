package harith.uint

import chisel3.simulator.scalatest.ChiselSim
import hammer.test._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class UIntBoothMulSpec extends AnyFreeSpec with Matchers with ChiselSim {

  private val styles = Seq(ReductionStyle.Wallace, ReductionStyle.Dadda)

  "UIntBoothMul" - {
    styles.foreach { style =>
      s"reduction style = $style" - {
        MulTestUtils.widths.foreach { width =>
          s"width = $width" in Sim(
            new UIntBoothMul(width, style, new UIntPrefixAdd(width * 2, PrefixStyle.KoggeStone)),
          ) { dut =>
            Test("edge vectors", dut) { dut =>
              MulTestUtils.checkCombinational(dut, MulTestUtils.edgeVectors(width))
            }
            Test("random vectors", dut) { dut =>
              MulTestUtils.checkCombinational(dut, MulTestUtils.randomVectors(width, width + 1))
            }
          }
        }
      }
    }
  }
}
