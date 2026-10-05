package harith.fp

import chisel3._
import chisel3.simulator.PeekPokeAPI._
import chisel3.simulator.scalatest.ChiselSim
import hammer.test._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

object FpFmaSpec {

  val rng = new scala.util.Random(67890)

  def randomBits(format: FpFormat, count: Int): Seq[BigInt] =
    Seq.fill(count)(BigInt(format.width, rng))

  def fp16ToFloat(bits: BigInt): Float = {
    val s = ((bits >> 15) & 1).toInt
    val e = ((bits >> 10) & 0x1f).toInt
    val m = (bits & 0x3ff).toInt
    val v =
      if (e == 0) (m.toDouble / 1024.0) * math.pow(2, -14)
      else if (e == 31) { if (m == 0) Double.PositiveInfinity else Double.NaN }
      else (1.0 + m.toDouble / 1024.0) * math.pow(2, e - 15)
    (if (s == 1) -v else v).toFloat
  }

  def bf16ToFloat(bits: BigInt): Float = {
    val s = ((bits >> 15) & 1).toInt
    val e = ((bits >> 7) & 0xff).toInt
    val m = (bits & 0x7f).toInt
    val v =
      if (e == 0) (m.toDouble / 128.0) * math.pow(2, -126)
      else if (e == 255) { if (m == 0) Double.PositiveInfinity else Double.NaN }
      else (1.0 + m.toDouble / 128.0) * math.pow(2, e - 127)
    (if (s == 1) -v else v).toFloat
  }

  def toFloat(bits: BigInt, format: FpFormat): Float =
    format match {
      case FpFormat.Fp16 => fp16ToFloat(bits)
      case FpFormat.Bf16 => bf16ToFloat(bits)
      case FpFormat.Fp32 => java.lang.Float.intBitsToFloat(bits.toInt)
      case _             => throw new IllegalArgumentException(s"unsupported $format")
    }

  def fmaFp32Ref(a: BigInt, b: BigInt, c: BigInt, aFmt: FpFormat, bFmt: FpFormat): BigInt = {
    val r = Math.fma(toFloat(a, aFmt), toFloat(b, bFmt), java.lang.Float.intBitsToFloat(c.toInt))
    if (java.lang.Float.isNaN(r)) BigInt(0x7fc00000L)
    else BigInt(java.lang.Float.floatToRawIntBits(r) & 0xffffffffL)
  }

  def fmaFp64Ref(a: BigInt, b: BigInt, c: BigInt): BigInt = {
    val r = Math.fma(
      java.lang.Double.longBitsToDouble(a.toLong),
      java.lang.Double.longBitsToDouble(b.toLong),
      java.lang.Double.longBitsToDouble(c.toLong),
    )
    if (java.lang.Double.isNaN(r)) BigInt(0x7ff8000000000000L)
    else BigInt(java.lang.Double.doubleToRawLongBits(r)) & ((BigInt(1) << 64) - 1)
  }

  def check(
      dut:     FpFma,
      aFmt:    FpFormat,
      bFmt:    FpFormat,
      cFmt:    FpFormat,
      outFmt:  FpFormat,
      vectors: Seq[(BigInt, BigInt, BigInt)],
      ref:     (BigInt, BigInt, BigInt) => BigInt,
  ): Unit = {
    vectors.foreach { case (a, b, c) =>
      dut.io.src1.poke(a.U(aFmt.width.W))
      dut.io.src2.poke(b.U(bFmt.width.W))
      dut.io.add.poke(c.U(cFmt.width.W))
      dut.io.rm.poke(0.U(3.W))
      dut.io.output.expect(ref(a, b, c).U(outFmt.width.W), s"a=$a b=$b c=$c")
      dut.clock.step()
    }
  }
}

class FpFmaSpec extends AnyFreeSpec with Matchers with ChiselSim {
  import FpFmaSpec._

  "Fp32Fma" - {
    "matches fused single precision" in Sim(new Fp32Fma) { dut =>
      Test("random vectors", dut) { dut =>
        val a = randomBits(FpFormat.Fp32, 300)
        val b = randomBits(FpFormat.Fp32, 300)
        val c = randomBits(FpFormat.Fp32, 300)
        check(dut, FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, a.zip(b).zip(c).map { case ((x, y), z) => (x, y, z) }, (x, y, z) => fmaFp32Ref(x, y, z, FpFormat.Fp32, FpFormat.Fp32))
      }
    }
  }

  "Fp64Fma" - {
    "matches fused double precision" in Sim(new Fp64Fma) { dut =>
      Test("random vectors", dut) { dut =>
        val a = randomBits(FpFormat.Fp64, 150)
        val b = randomBits(FpFormat.Fp64, 150)
        val c = randomBits(FpFormat.Fp64, 150)
        check(dut, FpFormat.Fp64, FpFormat.Fp64, FpFormat.Fp64, FpFormat.Fp64, a.zip(b).zip(c).map { case ((x, y), z) => (x, y, z) }, fmaFp64Ref)
      }
    }
  }

  "Fp16Fp32Fma" - {
    "matches fused fp32 from fp16 inputs" in Sim(new Fp16Fp32Fma) { dut =>
      Test("random vectors", dut) { dut =>
        val a = randomBits(FpFormat.Fp16, 300)
        val b = randomBits(FpFormat.Fp16, 300)
        val c = randomBits(FpFormat.Fp32, 300)
        check(dut, FpFormat.Fp16, FpFormat.Fp16, FpFormat.Fp32, FpFormat.Fp32, a.zip(b).zip(c).map { case ((x, y), z) => (x, y, z) }, (x, y, z) => fmaFp32Ref(x, y, z, FpFormat.Fp16, FpFormat.Fp16))
      }
    }
  }

  "FpBf16Fp32Fma" - {
    "matches fused fp32 from bf16 inputs" in Sim(new FpBf16Fp32Fma) { dut =>
      Test("random vectors", dut) { dut =>
        val a = randomBits(FpFormat.Bf16, 300)
        val b = randomBits(FpFormat.Bf16, 300)
        val c = randomBits(FpFormat.Fp32, 300)
        check(dut, FpFormat.Bf16, FpFormat.Bf16, FpFormat.Fp32, FpFormat.Fp32, a.zip(b).zip(c).map { case ((x, y), z) => (x, y, z) }, (x, y, z) => fmaFp32Ref(x, y, z, FpFormat.Bf16, FpFormat.Bf16))
      }
    }
  }
}
