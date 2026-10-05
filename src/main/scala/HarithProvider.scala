package harith

import chisel3._
import chisel3.util._
import harith.uint._
import org.chipsalliance.cde.config._

case object HarithConfigKey extends Field[HarithConfig]

/** 
  * The global default set of arithmatic units of Harith
  * 
  * When used with `HasHarith`, this act as a default module factory for the unified arith api
  */
case class HarithConfig(
    val uintAdd: Int => UIntAdd,
    val uintMul: Int => UIntMul,
)

trait HasHarith {
  val p: Parameters
  val conf = p(HarithConfigKey)

  def uintAdd(width: Int)(src1: UInt, src2: UInt, carry: Bool = 0.B): UInt = {
    val adder = Module(conf.uintAdd(width))
    adder.io.src1  := src1
    adder.io.src2  := src2
    adder.io.carry := carry
    adder.io.output
  }

  def uintMul(width: Int)(src1: UInt, src2: UInt): UInt = {
    val mul = Module(conf.uintMul(width))
    mul.io.src1 := src1
    mul.io.src2 := src2
    mul.io.output
  }
}
