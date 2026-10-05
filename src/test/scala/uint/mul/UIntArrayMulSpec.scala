package harith.uint

import chisel3.simulator.scalatest.ChiselSim
import hammer.test._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class UIntArrayMulSpec extends AnyFreeSpec with Matchers with ChiselSim {

  "UIntArrayMul" - {
    MulTestUtils.widths.foreach { width =>
      s"width = $width" in Sim(new UIntArrayMul(width)) { dut =>
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
