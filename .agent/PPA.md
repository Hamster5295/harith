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
  * <platform>@<condition>: delay = <delay>  area = <area>
  * <This line should leave empty>
  * <platform>@<condition>: delay = <delay>  area = <area>
  * <This line should leave empty>
  * <platform>@<condition>: delay = <delay>  area = <area>
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
- `<delay>` is the delay in human-friendly unit, e.g. `1ns`, `3ms`
- `<area>` is the area / utility of the module
  - For FPGA, this is the resources cost, e.g. `100luts + 30ff`
  - For ASIC, this is the area cost, e.g. `100um²`


## Conditions

Every module should **AT LEAST** do 2 analysis, fpga & asic each.

For complex modules, it's encouraged to analyze under different parameter set.

e.g.  
- A pipelined module can be analyzed with different delay cycles, which shows its trade of performance and area.
- A Fp module can be analyzed with different input / output datatypes, which shows the influence on datatype adaption.