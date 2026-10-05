# PPA

This file provides instruction on PPA analysis

For every DSP module that is exposed for other project to use, PPA analysis is **ALWAYS** required so that users could perform in-depth optimization.


## Analysis

The analysis procedure starts with:

```shell
make fpga TARGET=<package>.<class>
make asic TARGET=<package>.<class>
```


## Policy

Modules should be designed to **eliminate** non-structual overhead.

e.g:   
When designing a high-performance module, unnecessary logic that adds up delay should be eliminated, until the structure reaches its limit.


## Annotation

For every arith module, PPA analysis should be conducted and annoted in the doc comments.

PPA Anotations should follow the format below:

```scala
/** <This line should leave empty>
  * Header Line, introduce the module / api breifly
  * <This line should leave empty>
  * Descriptions, can cross multiple lines
  * 
  * <platform>@<condition>: delay[i/o/max] = <in>/<out>/<max>  area = <area>
  * <This line should leave empty>
  * <platform>@<condition>: delay[i/o/max] = <in>/<out>/<max>  area = <area>
  * <This line should leave empty>
  * <platform>@<condition>: delay[i/o/max] = <in>/<out>/<max>  area = <area>
  * 
  * @param something Introduce the params (if there are any)
  */
```

where
- `<platform>` is one of the option below:
  - `fpga`: analysis by `make fpga TARGET=xxx`
  - `55nm`: analysis by `make asic TARGET=xxx`, which uses icsprout55nm pdk
- `<condition>` is the key parameters when generating rtl
  - For `UInt`, this includes the width of the oprands, e.g. `32bit`
  - For `Fp`, this includes the type of the oprand, e.g. `fp32`
  - For general, this includes the delay cycle count (if pipelined or staged) 
- `<delay>` is the triple of delays separated by `/`, in human-friendly unit, e.g. `1ns/2ns/3ns`
  - `<in>`: the **input delay**: from a data input port to the first register (or to an output port, for a combin module)
  - `<out>`: the **output delay**: from the last register (or from an input port, for a combin module) to an output port, excluding the output buffer's own delay
  - `<max>`: the **largest delay**: the worst register-to-register delay, which fixes the achievable frequency
  - all three are **pure logic** delays: the external input/output delay modeled by the constraint is excluded
- `<area>` is the area / utility of the module
  - For FPGA, this is the resources cost, e.g. `100luts + 30ff`
  - For ASIC, this is the area cost, e.g. `100um²`
  - The analysis output register inserted by `ExportForAnalysis` is **not** counted towards the module:
    subtract its bit count (`reg_bits`, the total width of the `*_reg` output ports) from the FPGA `ff`,
    and `reg_bits * 6.16um²` (one `DFFQX0P5H7L` in the icsprout55 PDK) from the ASIC area.


## Conditions

Every module should **AT LEAST** do 2 analysis, fpga & asic each.

For complex modules, it's encouraged to analyze under different parameter set.

e.g.  
- A pipelined module can be analyzed with different delay cycles, which shows its trade of performance and area.
- A Fp module can be analyzed with different input / output datatypes, which shows the influence on datatype adaption.