# PPA

This file provides instruction on PPA analysis

For every DSP module that is exposed for other project to use, PPA analysis is **ALWAYS** required so that users could perform in-depth optimization.


## Analysis

The analysis procedure starts with:

```shell
make fpga TARGET=<package>.<class>
make asic TARGET=<package>.<class>
```


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