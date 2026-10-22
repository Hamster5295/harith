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
          w =>
            new UIntTreeMul(
              w,
              ReductionStyle.Dadda,
              aw => new UIntPrefixAdd(aw, PrefixStyle.KoggeStone),
            ),
          w => new UIntPrefixAdd(w, PrefixStyle.KoggeStone),
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
        new UIntComposedFma(width, w => new UIntArrayMul(w), w => new UIntRippleAdd(w)),
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
