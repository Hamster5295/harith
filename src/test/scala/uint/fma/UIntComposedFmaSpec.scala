package harith.uint

import chisel3.simulator.scalatest.ChiselSim
import hammer.test._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class UIntComposedFmaSpec extends AnyFreeSpec with Matchers with ChiselSim {

  "UIntComposedFma with a tree multiplier and a prefix adder" - {
    FmaTestUtils.widths.foreach { width =>
      s"width = $width" in Sim(
        new UIntComposedFma(
          width,
          new UIntTreeMultiplier(width, ReductionStyle.Dadda, new UIntPrefixAdder(2 * width, PrefixStyle.KoggeStone)),
          new UIntPrefixAdder(2 * width + 1, PrefixStyle.KoggeStone),
        ),
      ) { dut =>
        Test("edge vectors", dut) { dut =>
          FmaTestUtils.checkCombinational(dut, FmaTestUtils.edgeVectors(width))
        }
        Test("random vectors", dut) { dut =>
          FmaTestUtils.checkCombinational(dut, FmaTestUtils.randomVectors(width, width))
        }
      }
    }
  }

  "UIntComposedFma with an array multiplier and a ripple adder" - {
    FmaTestUtils.widths.foreach { width =>
      s"width = $width" in Sim(
        new UIntComposedFma(width, new UIntArrayMultiplier(width), new UIntRippleAdder(2 * width + 1)),
      ) { dut =>
        Test("edge vectors", dut) { dut =>
          FmaTestUtils.checkCombinational(dut, FmaTestUtils.edgeVectors(width))
        }
        Test("random vectors", dut) { dut =>
          FmaTestUtils.checkCombinational(dut, FmaTestUtils.randomVectors(width, width + 1))
        }
      }
    }
  }
}
