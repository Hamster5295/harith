# What's Harith

Harith, or *Hamster's ARITHmatic Lib*, is a Chisel library for standardized DSP interfaces and implementions. It aims to provide 2 core features.


## 💍 One Interface to Rule

One of the most powerful tools Chisel brought us is the **OOP** pattern, which allows *providing multiple implementions under the same interface* through a proper inheritance tree.

Harith provides one single interface per function, then implement it under different architectures, covering from high-performance to low-cose applications.  

Take [`UIntAdd`](/uint/add/) as an instance: 

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

- `UIntAddIO` is the standard interface for every module that carries out unsigned integer add.
- `UIntAdd` is a trait, which requires another class to 
   1. Extend the Chisel `Module`  
   2. Provide `io` as `UIntAddIO`  

Every class that derives `UIntAdd` is seen as a valid UInt adder, regardless of its implemention.

Now, say there's a complex module that uses 4 UInt adders inside: 
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

Note here we provides a *factory function*: `adder: Int => UIntAdd` as its parameter, where the input Int specifies its width.

Now we can instantiate this module as below:
```scala
// Create an instance that uses Ripper Adder as UIntAdd implemention.  
// This is ideal for low-area designs.
val mod1 = Module(new ReallyComplexModule(w => new UIntRippleAdd(w)))

// Create an instance that uses Macro as UIntAdd implemention. 
// This is ideal for FPGA.
val mod2 = Module(new ReallyComplexModule(w => new UIntMacroAdd(w)))
```

Here, `UIntRippleAdd` and `UIntMacroAdd` are valid implemention of `UIntAdd`, and we're able to **switch** the 4 adder instances by modifying **only one** parameter.  

For larger designs, this can be critical, as you might want to try out different implementions for best PPA. You can also create your own implemention by *extending the corresponding trait*


## ⚡ PPA included

For long, the timing and area cost can only be inferred from experience at designing stage, while doing analyze costs time and labor.  

Now, with the help of open-source PDKs, toolchains and LLM agents, we're able to maintain a large database of PPA analysis for all modules provided by Harith.

Each module has gone under **FPGA & ASIC** synthesis and STA. The analysis conditions can be found [here](/guide/analysis-condition).