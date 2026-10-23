package harith.sint

import chisel3.simulator.scalatest.ChiselSim
import hammer.test._
import harith.uint.{PrefixStyle, ReductionStyle, UIntPrefixAdd}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class SIntTreeFmaSpec extends AnyFreeSpec with Matchers with ChiselSim {

  "SIntTreeFma" - {
    SIntFmaTestUtils.widths.foreach { width =>
      s"width = $width" in Sim(new SIntTreeFma(width, ReductionStyle.Dadda, w => new UIntPrefixAdd(w, PrefixStyle.KoggeStone))) { dut =>
        Test("edge vectors", dut) { dut =>
          SIntFmaTestUtils.checkCombinational(dut, SIntFmaTestUtils.edgeVectors(width))
        }
        Test("random vectors", dut) { dut =>
          SIntFmaTestUtils.checkCombinational(dut, SIntFmaTestUtils.randomVectors(width, width + 2))
        }
      }
    }
  }
}
