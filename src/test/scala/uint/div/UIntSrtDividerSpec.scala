package harith.uint

import chisel3.simulator.scalatest.ChiselSim
import hammer.test._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class UIntSrtDividerSpec extends AnyFreeSpec with Matchers with ChiselSim {

  "UIntSrtDivider" - {
    Seq(2, 4).foreach { radix =>
      s"radix = $radix" - {
        DividerTestUtils.widths.foreach { width =>
          s"width = $width" in Sim(new UIntSrtDivider(width, radix)) { dut =>
            Test("edge vectors", dut) { dut =>
              DividerTestUtils.check(dut, DividerTestUtils.edgeVectors(width))
            }
            Test("random vectors", dut) { dut =>
              DividerTestUtils.check(dut, DividerTestUtils.randomVectors(width, width + radix))
            }
            Test("flush aborts", dut) { dut =>
              DividerTestUtils.checkFlush(dut, (BigInt(1) << width) - 1, BigInt(1))
            }
          }
        }
      }
    }
  }
}
