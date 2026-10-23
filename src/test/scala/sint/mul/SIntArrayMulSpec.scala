package harith.sint

import chisel3.simulator.scalatest.ChiselSim
import hammer.test._
import harith.uint.{PrefixStyle, ReductionStyle, UIntPrefixAdd}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class SIntArrayMulSpec extends AnyFreeSpec with Matchers with ChiselSim {

  "SIntArrayMul" - {
    SIntMulTestUtils.widths.foreach { width =>
      s"width = $width" in Sim(new SIntArrayMul(width)) { dut =>
        Test("edge vectors", dut) { dut =>
          SIntMulTestUtils.checkCombinational(dut, SIntMulTestUtils.edgeVectors(width))
        }
        Test("random vectors", dut) { dut =>
          SIntMulTestUtils.checkCombinational(dut, SIntMulTestUtils.randomVectors(width, width))
        }
      }
    }
  }
}
