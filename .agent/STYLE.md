# Style

This file provides instruction on coding style


## Format

The format of the project is controlled by `scalafmt`, a tool for format checking and formating.  

`scalafmt` **must** be called from Makefile: `make format`.

You should **always** format the code **before** commiting to git.


## Naming

This project follows the naming of Java, e.g. class names should be `PascalCase`, unless direct integration with existing RTL projects (which should follow the naming of the RTL project).  

All the Initialisms should be `PascalCase`ed, e.g. `Axi`, `Dma`, etc, except the cases below: 
- `XxxIO` is a fixed naming, the `IO` should be capitalized. `XxxIO` should only be used as the ports of `Xxx` module
- `XxxOH` is a fixed naming, where the `OH` is short of "One Hot", should be capitalized
- `UInt` / `SInt` are fixed namings, which are short of `Unsigned Int` and `Signed Int`


## Chisel Specific

Chisel is a DSL for writing Hardware modules. Thus, there're specifal rules for it.

1. Module definition

A major module (where the module's name matches the file name) should always be defined as below:

```scala
import chisel3._
import chisel3.util._

// The module's ports, always an independent class, instead of an anoymous Bundle
class XxxIO extends Bundle {
    // Some ports
}

class Xxx extends Module {
    val io = IO(new XxxIO)  // The port declaration should always on the first line

    // Logic
}

// Sometimes the major module requires submodules. 
// These modules are allowed to use anoymous Bundles as ports.
class SomeModuleInsideXxx extends Module {
    val io = IO(new Bundle {
        // Some ports
    })

    // Logic
}
```

2. Unit Test

Unit tests should be implemented in a **per-module** basis for better parallelism and independent bug discovery.  

Utility lib `hammer` provides `Sim` and `Test` API for performant unit tests, where `Sim` invokes `verilator` compilation in ChiselSim and `Test` does an extra reset for reusing the existing `verilator` model.

```scala
import chisel3._
import chisel3.experimental.BundleLiterals._
import chisel3.simulator.scalatest.ChiselSim
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import hammer.test._

class UIntAdderSpec extends AnyFreeSpec with Matchers with ChiselSim {
    "Some Module" should "do correct things" in Sim(new Xxx()) { dut => 

        Test("Some Behaviour", dut) { dut => 
            // Do some real tests here
            // Test(xxx) will reset the dut so that we don't need another verilator compilation    
        }

        Test("Another Behaviour", dut) { dut => 
            // Fresh test here
            // Note: We expect the dut to recover its initial state after reset
            //       Anything that's not properly reset should be viewed as BUG
        }
    }
}
```

3. Doc Comments

Doc Comments should present at any Module / API that is meant to expose to referer. 

Doc Comments should follow the example below: 

```scala
/** <This line should leave empty>
  * Header Line, introduce the module / api breifly
  * <This line should leave empty>
  * Descriptions, can cross multiple lines
  * 
  * @param something Introduce the params (if there are any)
  */


```