# Harith

A standerdized DSP library for Chisel HDL.

See [The Docs](https://hamster5295.github.io/harith) for more.


## Quick Start

Harith is published to Maven Central under `io.github.hamster5295`. Add it to your `build.mill`
alongside Chisel:

```scala
override def mvnDeps = Seq(
  mvn"org.chipsalliance::chisel:7.15.0",
  mvn"io.github.hamster5295::harith:0.1.0",
)
```

After `mill` resolves, you're able to use every Harith module in your design.  

For `mill 1.x` below or `sbt` users, change the dependency line accordingly.


## Why Harith?

### 💍 One Interface to Rule

One of the core feature is the standardized interface built for **Typed Language**(i.e. Scala).  
You're able to *switch between different implementions* while maintaining a stable architecture. 

This example shows a complex module with multiple adders inside, which can be switched through Parameterization：

```scala
// The `adder: Int => UIntAdd` here is a special parameter that act as a factory to create adders
// Every module, as long as it implements the UIntAdd trait, is recognized as a valid UInt adder, 
// and can be switched through this parameter.
class ReallyComplexModule(adder: Int => UIntAdd) extends Module {
    val io = IO(new Bundle { /* ... */ })

    val add1 = Module(adder(32))    // Create a 32-bit adder
    val add2 = Module(adder(16))    // Create a 16-bit adder
    val add3 = Module(adder(16))    // Create a 16-bit adder
    val add4 = Module(adder(8))     // Create a 8-bit  adder

    // Some other logic
}
```

Now we can instantiate this module as below:
```scala
// Create an instance that uses Ripper Adder as UIntAdd implemention.  
// This is ideal for low-area designs.
val mod1 = Module(new ReallyComplexModule(w => new UIntRippleAdd(w)))

// Create an instance that uses Macro as UIntAdd implemention. 
// This is ideal for FPGA.
val mod2 = Module(new ReallyComplexModule(w => new UIntMacroAdd(w)))
```

### ⚡ PPA Included

Harith provides the PPA analysis of **every module** implemented, which is benefitial especially when you're designing compute-bound circuits.  

Both FPGA and ASIC analysis is provided.