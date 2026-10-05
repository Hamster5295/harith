package harith.fp

import chisel3.simulator.scalatest.ChiselSim
import hammer.test._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

/**
  * Check the alternative significand datapath variants against the same references as the generic
  * implementations.
  */
class FpVariantsSpec extends AnyFreeSpec with Matchers with ChiselSim {

  private val n = 24
  private val va = FpFmaSpec.randomBits(FpFormat.Fp32, n)
  private val vb = FpFmaSpec.randomBits(FpFormat.Fp32, n)
  private val vc = FpFmaSpec.randomBits(FpFormat.Fp32, n)
  private val ab = va.zip(vb)
  private val abc = va.zip(vb).zip(vc).map { case ((x, y), z) => (x, y, z) }

  private def mulRef(x: BigInt, y: BigInt): BigInt =
    FpMulSpec.fp32Ref(x, y)
  private def fmaRef(x: BigInt, y: BigInt, z: BigInt): BigInt =
    FpFmaSpec.fmaFp32Ref(x, y, z, FpFormat.Fp32, FpFormat.Fp32)
  private def addRef(x: BigInt, y: BigInt): BigInt =
    FpAddSpec.addFp32Ref(x, y, FpFormat.Fp32, FpFormat.Fp32)

  "multiplier variants" - {
    "FpArrayMul" in Sim(new FpArrayMul(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32)) { dut =>
      Test("random", dut)(d => FpMulSpec.check(d, FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, ab, mulRef))
    }
    "FpBoothMul" in Sim(new FpBoothMul(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32)) { dut =>
      Test("random", dut)(d => FpMulSpec.check(d, FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, ab, mulRef))
    }
    "FpTreeMul" in Sim(new FpTreeMul(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32)) { dut =>
      Test("random", dut)(d => FpMulSpec.check(d, FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, ab, mulRef))
    }
  }

  "fma variants" - {
    "FpArrayFma" in Sim(new FpArrayFma(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32)) { dut =>
      Test("random", dut)(d => FpFmaSpec.check(d, FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, abc, fmaRef))
    }
    "FpBoothFma" in Sim(new FpBoothFma(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32)) { dut =>
      Test("random", dut)(d => FpFmaSpec.check(d, FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, abc, fmaRef))
    }
    "FpTreeFma" in Sim(new FpTreeFma(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32)) { dut =>
      Test("random", dut)(d => FpFmaSpec.check(d, FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, abc, fmaRef))
    }
    "FpRippleFma" in Sim(new FpRippleFma(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32)) { dut =>
      Test("random", dut)(d => FpFmaSpec.check(d, FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, abc, fmaRef))
    }
    "FpPrefixFma" in Sim(new FpPrefixFma(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32)) { dut =>
      Test("random", dut)(d => FpFmaSpec.check(d, FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, abc, fmaRef))
    }
  }

  "adder variants" - {
    "FpRippleAdd" in Sim(new FpRippleAdd(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32)) { dut =>
      Test("random", dut)(d => FpAddSpec.check(d, FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, ab, addRef))
    }
    "FpPrefixAdd" in Sim(new FpPrefixAdd(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32)) { dut =>
      Test("random", dut)(d => FpAddSpec.check(d, FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, ab, addRef))
    }
    "FpCarrySelectAdd" in Sim(new FpCarrySelectAdd(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32)) { dut =>
      Test("random", dut)(d => FpAddSpec.check(d, FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, ab, addRef))
    }
    "FpCarryLookaheadAdd" in Sim(new FpCarryLookaheadAdd(FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32)) { dut =>
      Test("random", dut)(d => FpAddSpec.check(d, FpFormat.Fp32, FpFormat.Fp32, FpFormat.Fp32, ab, addRef))
    }
  }
}
