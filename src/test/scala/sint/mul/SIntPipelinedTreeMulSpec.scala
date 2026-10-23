package harith.sint

import chisel3.simulator.scalatest.ChiselSim
import hammer.test._
import harith.uint.{PrefixStyle, ReductionStyle, UIntPrefixAdd}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class SIntPipelinedTreeMulSpec extends AnyFreeSpec with Matchers with ChiselSim {

  "SIntPipelinedTreeMul" - {
    SIntMulTestUtils.pipelinedWidths.foreach { width =>
      s"width = $width" in Sim(new SIntPipelinedTreeMul(width, ReductionStyle.Dadda, w => new UIntPrefixAdd(w, PrefixStyle.KoggeStone), 2)) { dut =>
        Test("edge vectors", dut) { dut =>
          SIntMulTestUtils.checkPipelined(dut, SIntMulTestUtils.edgeVectors(width))
        }
        Test("random vectors", dut) { dut =>
          SIntMulTestUtils.checkPipelined(dut, SIntMulTestUtils.randomVectors(width, width + 2))
        }
      }
    }
  }
}
