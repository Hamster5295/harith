package harith.sint

import chisel3.simulator.scalatest.ChiselSim
import hammer.test._
import harith.uint.{UIntMacroMul, UIntPipelinedArrayMul}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class SIntComposedFmaSpec extends AnyFreeSpec with Matchers with ChiselSim {

  "SIntComposedFma" - {
    SIntFmaTestUtils.widths.foreach { width =>
      s"width = $width" in Sim(new SIntComposedFma(width, w => new UIntMacroMul(w))) { dut =>
        Test("edge vectors", dut) { dut =>
          SIntFmaTestUtils.checkCombinational(dut, SIntFmaTestUtils.edgeVectors(width))
        }
        Test("random vectors", dut) { dut =>
          SIntFmaTestUtils.checkCombinational(dut, SIntFmaTestUtils.randomVectors(width, width))
        }
      }
    }
  }

  "SIntComposedFma with a pipelined multiplier" - {
    SIntFmaTestUtils.widths.foreach { width =>
      s"width = $width" in Sim(new SIntComposedFma(width, w => new UIntPipelinedArrayMul(w, 2))) { dut =>
        Test("random vectors", dut) { dut =>
          SIntFmaTestUtils.checkPipelined(dut, SIntFmaTestUtils.randomVectors(width, width + 1))
        }
      }
    }
  }
}
