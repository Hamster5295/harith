package harith

import chisel3._
import chisel3.reflect.DataMirror
import chisel3.reflect.DataMirror.internal.chiselTypeClone
import chisel3.util._
import hammer.Export
import scala.collection.immutable.ListMap

/**
  * A dynamically-typed record mirroring a child module's ports at the top level.
  *
  * `io` is a type-clone of the child's io bundle; `outputs` names the ports whose leaves are
  * all outputs. Those are exposed both raw (`<name>`) and registered (`<name>_reg`), while
  * every other port is forwarded unchanged.
  */
private[harith] class AnalysisPorts(io: Record, outputs: Set[String]) extends Record {
  val elements: ListMap[String, Data] = ListMap(
    io.elements.toSeq.flatMap { case (name, data) =>
      if (outputs(name)) Seq(name -> data, s"${name}_reg" -> Output(chiselTypeClone(data)))
      else Seq(name               -> data)
    }: _*,
  )
}

/**
  * A wrapper that makes a module fit for standalone timing analysis.
  *
  * Every pure output is probed twice: once raw, so the register-to-output delay can be
  * measured, and once through an output register, so the register-terminated critical path
  * that fixes the achievable frequency can be measured. Inputs and mixed-direction (e.g.
  * flipped-decoupled) ports are forwarded leaf by leaf according to their direction.
  */
private[harith] class ExportForAnalysisModule(gen: => Module) extends Module {
  private val child = Module(gen)

  private val childIo: Record = child.getClass.getMethod("io").invoke(child).asInstanceOf[Record]

  override def desiredName: String = child.getClass.getSimpleName + "_analysis"

  private val ports: Seq[(String, Data)] = childIo.elements.toSeq

  private val outputNames: Set[String] = ports.collect {
    case (name, data) if DataMirror.directionOf(data) == ActualDirection.Output => name
  }.toSet

  val io: AnalysisPorts = IO(new AnalysisPorts(chiselTypeClone(childIo), outputNames))

  private def forward(inner: Data, outer: Data): Unit = (inner, outer) match {
    case (i: Record, o: Record) => i.elements.foreach { case (name, data) =>
        forward(data, o.elements(name))
      }
    case (i: Vec[_], o: Vec[_])   => i.zip(o).foreach { case (data, top) => forward(data, top) }
    case (i: Element, o: Element) =>
      DataMirror.directionOf(i) match {
        case ActualDirection.Input => i := o
        case _                     => o := i
      }
    case _ =>
      throw new IllegalArgumentException(s"Cannot forward ${inner.getClass} to ${outer.getClass}")
  }

  ports.foreach { case (name, data) =>
    if (outputNames(name)) {
      io.elements(name)           := data
      io.elements(s"${name}_reg") := RegNext(data)
    } else {
      forward(data, io.elements(name))
    }
  }
}

object ExportForAnalysis {
  def apply(
      gen:     => Module,
      args:    Array[String],
      firOpts: Array[String] = Array(),
  ): Unit =
    Export(new ExportForAnalysisModule(gen), args, firOpts)
}
