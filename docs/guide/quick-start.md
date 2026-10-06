# Quick Start

This page will guide you through the usage and development of Harith.


## Usage

Harith is published to Maven Central under `io.github.hamster5295`. Add it to your `build.mill`
alongside Chisel:

```scala
override def mvnDeps = Seq(
  mvn"org.chipsalliance::chisel:7.15.0",
  mvn"io.github.hamster5295::harith:<version>",
)
```

After `mill` resolves, you're able to use every Harith module in your design.  

For `mill 1.x` below or `sbt` users, change the dependency line accordingly.


## Development

For development and contribution, follow the steps below: 

### Prerequisites

- **JDK 21** and the bundled `mill` launcher (no separate install needed).
- Optional: **Vivado** (FPGA) and **OpenSTA** plus the `icsprout55` PDK (ASIC).

::: info
The `icsprout55` is way too large and as a result not bundled as a git submodule.  

You can get it from https://github.com/openecos-projects/icsprout55-pdk
:::


### Testing

Clone the repository and run the test suite:

```shell
git clone https://codeberg.org/hamster5295/harith
cd harith
make test-all
```

This will run test suite for every calculation module.


### Standalone analysis

Every module ships with a standalone entry point (`object ... extends App`) that emits a
SystemVerilog wrapper with all outputs registered for analysis. Run the FPGA or ASIC flow for a
single module with:

```shell
make verilog TARGET=harith.fp.FpRippleAddFp32
make fpga    TARGET=harith.fp.FpRippleAddFp32
make asic    TARGET=harith.fp.FpRippleAddFp32
```

The floating-point modules expose one entry point per format — e.g. `FpRippleAddFp64`,
`FpRippleAddFp32`, `FpRippleAddFp16` and `FpRippleAddBf16` — while the integer modules expose a
single entry point each.

### Repository commands

| Command | Description |
| --- | --- |
| `make test-all` | Run the full test suite. |
| `make test TARGET=harith.uint.UIntRippleAddSpec` | Run a single test spec. |
| `make format` | Format all sources with scalafmt. |
| `make verilog TARGET=<object>` | Export one entry point as SystemVerilog. |
| `make fpga TARGET=<object>` | Run the Vivado FPGA PPA flow. |
| `make asic TARGET=<object>` | Run the OpenSTA 55 nm PPA flow. |
| `make lib` | Publish the library to a local Maven repository. |
