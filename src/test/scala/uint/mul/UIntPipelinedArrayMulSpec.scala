package harith.uint

import chisel3.simulator.scalatest.ChiselSim
import hammer.test._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class UIntPipelinedArrayMulSpec extends AnyFreeSpec with Matchers with ChiselSim {

  "UIntPipelinedArrayMul" - {
    MulTestUtils.pipelinedWidths.foreach { width =>
      Seq(0, 1, 3).foreach { stages =>
        s"width = $width, stages = $stages" in Sim(new UIntPipelinedArrayMul(width, stages)) { dut =>
          Test("edge vectors", dut) { dut =>
            MulTestUtils.checkPipelined(dut, MulTestUtils.edgeVectors(width))
          }
          Test("random vectors", dut) { dut =>
            MulTestUtils.checkPipelined(dut, MulTestUtils.randomVectors(width, width + stages))
          }
        }
      }
    }
  }
}
