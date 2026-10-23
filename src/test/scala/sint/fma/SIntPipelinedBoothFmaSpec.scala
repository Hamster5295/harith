package harith.sint

import chisel3.simulator.scalatest.ChiselSim
import hammer.test._
import harith.uint.{PrefixStyle, ReductionStyle, UIntPrefixAdd}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class SIntPipelinedBoothFmaSpec extends AnyFreeSpec with Matchers with ChiselSim {

  "SIntPipelinedBoothFma" - {
    SIntFmaTestUtils.pipelinedWidths.foreach { width =>
      s"width = $width" in Sim(new SIntPipelinedBoothFma(width, ReductionStyle.Dadda, w => new UIntPrefixAdd(w, PrefixStyle.KoggeStone), 2)) { dut =>
        Test("edge vectors", dut) { dut =>
          SIntFmaTestUtils.checkPipelined(dut, SIntFmaTestUtils.edgeVectors(width))
        }
        Test("random vectors", dut) { dut =>
          SIntFmaTestUtils.checkPipelined(dut, SIntFmaTestUtils.randomVectors(width, width + 2))
        }
      }
    }
  }
}
