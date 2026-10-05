package harith.fp

import chisel3._
import chisel3.simulator.PeekPokeAPI._
import chisel3.simulator.scalatest.ChiselSim
import hammer.test._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

object FpMulSpec {

  val rng = new scala.util.Random(12345)

  def randomBits(format: FpFormat, count: Int): Seq[BigInt] =
    Seq.fill(count)(BigInt(format.width, rng))

  def edgeBits(format: FpFormat): Seq[BigInt] = {
    val expMask = (1 << format.expWidth) - 1
    val manMask = (1 << format.manWidth) - 1
    def bits(sign: Int, e: Int, m: Int): BigInt =
      (BigInt(sign) << (format.expWidth + format.manWidth)) |
        (BigInt(e) << format.manWidth) |
        BigInt(m)
    Seq(
      bits(0, 0, 0),
      bits(1, 0, 0),
      bits(0, expMask, 0),
      bits(1, expMask, 0),
      bits(0, expMask, 1),
      bits(0, expMask, manMask),
      bits(0, 0, 1),
      bits(1, 0, 1),
      bits(0, 1, 0),
      bits(1, 1, 0),
      bits(0, (1 << (format.expWidth - 1)) - 1, 0),
      bits(0, expMask - 1, manMask),
      bits(1, expMask - 1, manMask),
    )
  }

  def isNaN(f: FpFormat, bits: BigInt): Boolean = {
    val e = (bits >> f.manWidth) & ((1 << f.expWidth) - 1)
    val m = bits & ((1 << f.manWidth) - 1)
    e == ((1 << f.expWidth) - 1) && m != 0
  }

  def fp16ToDouble(bits: BigInt): Double = {
    val s = ((bits >> 15) & 1).toInt
    val e = ((bits >> 10) & 0x1f).toInt
    val m = (bits & 0x3ff).toInt
    val v =
      if (e == 0) (m.toDouble / 1024.0) * math.pow(2, -14)
      else if (e == 31) { if (m == 0) Double.PositiveInfinity else Double.NaN }
      else (1.0 + m.toDouble / 1024.0) * math.pow(2, e - 15)
    if (s == 1) -v else v
  }

  def bf16ToDouble(bits: BigInt): Double = {
    val s = ((bits >> 15) & 1).toInt
    val e = ((bits >> 7) & 0xff).toInt
    val m = (bits & 0x7f).toInt
    val v =
      if (e == 0) (m.toDouble / 128.0) * math.pow(2, -126)
      else if (e == 255) { if (m == 0) Double.PositiveInfinity else Double.NaN }
      else (1.0 + m.toDouble / 128.0) * math.pow(2, e - 127)
    if (s == 1) -v else v
  }

  def check(
      dut:     FpMul,
      aFmt:    FpFormat,
      bFmt:    FpFormat,
      outFmt:  FpFormat,
      vectors: Seq[(BigInt, BigInt)],
      ref:     (BigInt, BigInt) => BigInt,
  ): Unit = {
    vectors.foreach { case (a, b) =>
      dut.io.src1.poke(a.U(aFmt.width.W))
      dut.io.src2.poke(b.U(bFmt.width.W))
      dut.io.rm.poke(0.U(3.W))
      dut.io.output.expect(ref(a, b).U(outFmt.width.W), s"a=$a b=$b")
      dut.clock.step()
    }
  }

  def fp32Ref(a: BigInt, b: BigInt): BigInt = {
    val r = java.lang.Float.intBitsToFloat(a.toInt) * java.lang.Float.intBitsToFloat(b.toInt)
    if (java.lang.Float.isNaN(r)) BigInt(0x7fc00000L)
    else BigInt(java.lang.Float.floatToRawIntBits(r) & 0xffffffffL)
  }

  def fp64Ref(a: BigInt, b: BigInt): BigInt = {
    val r = java.lang.Double.longBitsToDouble(a.toLong) * java.lang.Double.longBitsToDouble(b.toLong)
    if (java.lang.Double.isNaN(r)) BigInt(0x7ff8000000000000L)
    else BigInt(java.lang.Double.doubleToRawLongBits(r)) & ((BigInt(1) << 64) - 1)
  }

  def fp16Fp32Ref(a: BigInt, b: BigInt): BigInt = {
    val p = fp16ToDouble(a) * fp16ToDouble(b)
    if (p.isNaN) BigInt(0x7fc00000L)
    else BigInt(java.lang.Float.floatToRawIntBits(p.toFloat) & 0xffffffffL)
  }

  def bf16Fp32Ref(a: BigInt, b: BigInt): BigInt = {
    val p = bf16ToDouble(a) * bf16ToDouble(b)
    if (p.isNaN) BigInt(0x7fc00000L)
    else BigInt(java.lang.Float.floatToRawIntBits(p.toFloat) & 0xffffffffL)
  }
}

class FpMulSpec extends AnyFreeSpec with Matchers with ChiselSim {
  import FpMulSpec._

  "Fp32Mul" - {
    "matches IEEE single precision" in Sim(new Fp32Mul) { dut =>
      Test("random vectors", dut) { dut =>
        val a = randomBits(FpFormat.Fp32, 200)
        val b = randomBits(FpFormat.Fp32, 200)
        check(dut, FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, a.zip(b), fp32Ref)
      }
      Test("edge vectors", dut) { dut =>
        val e = edgeBits(FpFormat.Fp32)
        check(dut, FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, e.flatMap(x => e.map(y => (x, y))), fp32Ref)
      }
    }
  }

  "Fp64Mul" - {
    "matches IEEE double precision" in Sim(new Fp64Mul) { dut =>
      Test("random vectors", dut) { dut =>
        val a = randomBits(FpFormat.Fp64, 100)
        val b = randomBits(FpFormat.Fp64, 100)
        check(dut, FpFormat.Fp64, FpFormat.Fp64, FpFormat.Fp64, a.zip(b), fp64Ref)
      }
    }
  }

  "Fp16Fp32Mul" - {
    "matches exact product" in Sim(new Fp16Fp32Mul) { dut =>
      Test("random vectors", dut) { dut =>
        val a = randomBits(FpFormat.Fp16, 200)
        val b = randomBits(FpFormat.Fp16, 200)
        check(dut, FpFormat.Fp16, FpFormat.Fp16, FpFormat.Fp32, a.zip(b), fp16Fp32Ref)
      }
    }
  }

  "FpBf16Fp32Mul" - {
    "matches exact product" in Sim(new FpBf16Fp32Mul) { dut =>
      Test("random vectors", dut) { dut =>
        val a = randomBits(FpFormat.Bf16, 200)
        val b = randomBits(FpFormat.Bf16, 200)
        check(dut, FpFormat.Bf16, FpFormat.Bf16, FpFormat.Fp32, a.zip(b), bf16Fp32Ref)
      }
    }
  }
}
