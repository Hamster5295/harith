package harith.fp

import chisel3._
import chisel3.util._

/**
  * The IEEE-754 exception flags, matching the RISC-V `fflags` field.
  *
  * The flags are sticky at the system level; an operation reports the exceptions it raises.
  */
class FpFlags extends Bundle {

  /** Inexact result. */
  val nx = Bool()

  /** Underflow. */
  val uf = Bool()

  /** Overflow. */
  val of = Bool()

  /** Divide by zero. */
  val dz = Bool()

  /** Invalid operation. */
  val nv = Bool()

  /** The flags packed in the RISC-V `fcsr` layout `{NV, DZ, OF, UF, NX}`. */
  def bits: UInt = Cat(nv, dz, of, uf, nx)
}

object FpFlags {

  /**
    * Build the flag bundle from the individual flags.
    *
    * @param nx Inexact
    * @param uf Underflow
    * @param of Overflow
    * @param dz Divide by zero
    * @param nv Invalid operation
    * @return the flag bundle
    */
  def apply(nx: Bool, uf: Bool, of: Bool, dz: Bool, nv: Bool): FpFlags = {
    val flags = Wire(new FpFlags)
    flags.nx := nx
    flags.uf := uf
    flags.of := of
    flags.dz := dz
    flags.nv := nv
    flags
  }
}
