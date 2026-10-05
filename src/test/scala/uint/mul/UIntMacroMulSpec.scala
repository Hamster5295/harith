package harith.uint

import chisel3.simulator.scalatest.ChiselSim
import hammer.test._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class UIntMacroMulSpec extends AnyFreeSpec with Matchers with ChiselSim {

  "UIntMacroMul" - {
    MulTestUtils.widths.foreach { width =>
      s"width = $width" in Sim(new UIntMacroMul(width)) { dut =>
        Test("edge vectors", dut) { dut =>
          MulTestUtils.checkCombinational(dut, MulTestUtils.edgeVectors(width))
        }
        Test("random vectors", dut) { dut =>
          MulTestUtils.checkCombinational(dut, MulTestUtils.randomVectors(width, width))
        }
      }
    }
  }
}
