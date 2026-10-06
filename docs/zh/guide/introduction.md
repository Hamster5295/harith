# Harith 是什么

Harith，全称 *Hamster's ARITHmatic Lib*，是一个面向标准化 DSP 接口与实现的 Chisel 库，旨在提供两大核心特性。


## 💍 统一的接口

Chisel 带给我们的最强大工具之一是 **OOP** 模式：通过合理的继承树，可以*在同一接口下提供多种实现*。

Harith 为每种功能提供唯一接口，并在不同架构下实现它，覆盖从高性能到低成本的应用场景。

以 [`UIntAdd`](/zh/uint/add/) 为例：

```scala
/**
  * The ports of a [[UIntAdd]].
  *
  * @param width The width of the operands
  */
class UIntAddIO(width: Int) extends Bundle {
  val src1   = Input(UInt(width.W))
  val src2   = Input(UInt(width.W))
  val carry  = Input(Bool())
  val output = Output(UInt((width + 1).W))
}

/**
  * The common interface of the unsigned adders.
  *
  * Every implementation computes `src1 + src2 + carry` as a `width + 1` bit result through
  * [[UIntAddIO]].
  */
trait UIntAdd extends Module {
  val io: UIntAddIO

  /**
    * Result latency in clock cycles, where 0 marks a purely combinational adder.
    *
    * Downstream logic and testbenches use this to align the pipeline. Pipelined adders are fully
    * pipelined with a fixed latency and accept a new operand pair every cycle.
    */
  def latency: Int = 0
}
```

- `UIntAddIO` 是所有执行无符号整数加法模块的标准接口。
- `UIntAdd` 是一个 trait，它要求另一个类：
   1. 继承 Chisel 的 `Module`  
   2. 提供类型为 `UIntAddIO` 的 `io`  

任何派生自 `UIntAdd` 的类都被视为合法的 UInt 加法器，无论其具体实现如何。

假设有一个复杂模块内部使用了 4 个 UInt 加法器：

```scala
class ReallyComplexModule(adder: Int => UIntAdd) extends Module {
    val io = IO(new Bundle { /* ... */ })

    val add1 = Module(adder(32))    // Create a 32-bit adder
    val add2 = Module(adder(16))    // Create a 16-bit adder
    val add3 = Module(adder(16))    // Create a 16-bit adder
    val add4 = Module(adder(8))     // Create a 8-bit  adder

    // Some other logic
}
```

注意这里我们提供了*工厂函数* `adder: Int => UIntAdd` 作为参数，其中的 `Int` 指定位宽。

于是可以这样实例化该模块：

```scala
// Create an instance that uses Ripper Adder as UIntAdd implemention.  
// This is ideal for low-area designs.
val mod1 = Module(new ReallyComplexModule(w => new UIntRippleAdd(w)))

// Create an instance that uses Macro as UIntAdd implemention. 
// This is ideal for FPGA.
val mod2 = Module(new ReallyComplexModule(w => new UIntMacroAdd(w)))
```

这里 `UIntRippleAdd` 和 `UIntMacroAdd` 都是 `UIntAdd` 的合法实现，我们只需修改**唯一一个**参数即可**切换**这 4 个加法器实例。

对于更大的设计，这可能至关重要，因为你可能想尝试不同实现以获得最佳 PPA。你也可以通过*扩展对应的 trait* 来创建自己的实现。


## ⚡ 附带 PPA

长久以来，时序与面积开销只能在设计阶段凭经验推断，而实际做分析既费时又费力。

如今，借助开源 PDK、工具链与 LLM agent，我们得以维护 Harith 全部模块的庞大 PPA 分析数据库。

每个模块都经过了 **FPGA 与 ASIC** 综合与 STA。分析条件见[此处](/zh/guide/analysis-condition)。
