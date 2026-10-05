package harith.fp

import chisel3._
import chisel3.simulator.PeekPokeAPI._
import chisel3.simulator.scalatest.ChiselSim
import hammer.test._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

object FpPipelinedSpec {

  def checkMul(dut: FpMul, aFmt: FpFormat, bFmt: FpFormat, outFmt: FpFormat, vectors: Seq[(BigInt, BigInt)], ref: (BigInt, BigInt) => BigInt): Unit =
    vectors.foreach { case (x, y) =>
      dut.io.src1.poke(x.U(aFmt.width.W))
      dut.io.src2.poke(y.U(bFmt.width.W))
      dut.io.rm.poke(0.U(3.W))
      dut.clock.step(dut.latency)
      dut.io.output.expect(ref(x, y).U(outFmt.width.W), s"x=$x y=$y")
    }

  def checkFma(dut: FpFma, vectors: Seq[(BigInt, BigInt, BigInt)]): Unit =
    vectors.foreach { case (x, y, z) =>
      dut.io.src1.poke(x.U(FpFormat.Fp32.width.W))
      dut.io.src2.poke(y.U(FpFormat.Fp32.width.W))
      dut.io.add.poke(z.U(FpFormat.Fp32.width.W))
      dut.io.rm.poke(0.U(3.W))
      dut.clock.step(dut.latency)
      dut.io.output.expect(FpFmaSpec.fmaFp32Ref(x, y, z, FpFormat.Fp32, FpFormat.Fp32).U, s"x=$x y=$y z=$z")
    }

  def checkAdd(dut: FpAdd, vectors: Seq[(BigInt, BigInt)]): Unit =
    vectors.foreach { case (x, y) =>
      dut.io.src1.poke(x.U(FpFormat.Fp32.width.W))
      dut.io.src2.poke(y.U(FpFormat.Fp32.width.W))
      dut.io.rm.poke(0.U(3.W))
      dut.clock.step(dut.latency)
      dut.io.output.expect(FpAddSpec.addFp32Ref(x, y, FpFormat.Fp32, FpFormat.Fp32).U, s"x=$x y=$y")
    }

  def checkConvert(dut: FpConvert, vectors: Seq[BigInt]): Unit =
    vectors.foreach { x =>
      dut.io.src.poke(x.U(FpFormat.Fp16.width.W))
      dut.io.rm.poke(0.U(3.W))
      dut.clock.step(dut.latency)
      val f = FpFmaSpec.fp16ToFloat(x)
      val e =
        if (java.lang.Float.isNaN(f)) BigInt(0x7fc00000L)
        else BigInt(java.lang.Float.floatToRawIntBits(f) & 0xffffffffL)
      dut.io.output.expect(e.U, s"x=$x")
    }
}

class FpPipelinedSpec extends AnyFreeSpec with Matchers with ChiselSim {
  import FpPipelinedSpec._

  private val n  = 20
  private val va = FpFmaSpec.randomBits(FpFormat.Fp32, n)
  private val vb = FpFmaSpec.randomBits(FpFormat.Fp32, n)
  private val vc = FpFmaSpec.randomBits(FpFormat.Fp32, n)
  private val ab = va.zip(vb)

  "FpPipelinedMul" - {
    "matches the product after its latency" in Sim(new FpPipelinedMul(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, 2)) { dut =>
      Test("random", dut)(d => checkMul(d, FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, ab, FpMulSpec.fp32Ref))
    }
  }

  "FpPipelinedFma" - {
    "matches the fused result after its latency" in Sim(
      new FpPipelinedFma(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, 2),
    ) { dut =>
      Test("random", dut)(d => checkFma(d, va.zip(vb).zip(vc).map { case ((x, y), z) => (x, y, z) }))
    }
  }

  "FpPipelinedAdd" - {
    "matches the sum after its latency" in Sim(
      new FpPipelinedAdd(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, 2),
    ) { dut =>
      Test("random", dut)(d => checkAdd(d, ab))
    }
  }

  "FpPipelinedConvert" - {
    "matches the widened result after its latency" in Sim(new FpPipelinedConvert(FpFormat.Fp16, FpFormat.Fp32, 2)) { dut =>
      Test("random", dut)(d => checkConvert(d, FpFmaSpec.randomBits(FpFormat.Fp16, n)))
    }
  }
}
