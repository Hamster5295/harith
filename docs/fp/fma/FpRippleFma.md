# FpRippleFma

A floating-point fused multiply-adder with a ripple carry alignment adder.

::: info Source
`src/main/scala/fp/fma/FpRippleFma.scala`
:::

## Parameters

| Name | Type | Description |
| --- | --- | --- |
| `aFmt` | `FpFormat` | The format of the multiplicand |
| `bFmt` | `FpFormat` | The format of the multiplier |
| `cFmt` | `FpFormat` | The format of the addend |
| `outFmt` | `FpFormat` | The format of the result |
| `policy` | `FpPolicy` | The numeric policy |

**Delay** = 0 cycles

## IO

| Name | Direction | Type | Description |
| --- | --- | --- | --- |
| `src1` | Input | `UInt(aFmt.width.W)` | Multiplicand |
| `src2` | Input | `UInt(bFmt.width.W)` | Multiplier |
| `add` | Input | `UInt(cFmt.width.W)` | Addend |
| `rm` | Input | `UInt(3.W)` | RISC-V rounding mode |
| `output` | Output | `UInt(outFmt.width.W)` | Rounded result |
| `fflags` | Output | `FpFlags` | IEEE-754 exception flags |
| `fflags.nx` | Output | `Bool` | Inexact |
| `fflags.uf` | Output | `Bool` | Underflow |
| `fflags.of` | Output | `Bool` | Overflow |
| `fflags.dz` | Output | `Bool` | Divide by zero |
| `fflags.nv` | Output | `Bool` | Invalid operation |

## PPA

> See also [Analysis Condition](/guide/analysis-condition) 

| Condition | Platform | In (ns) | Out (ns) | Max (ns) | Area |
| --- | --- | --- | --- | --- | --- |
| `fp64` | FPGA | 91.884 | 92.864 | 91.884 | 11,240 LUTs + 0 FF |
| `fp64` | 55 nm | 64.2359 | 64.2359 | 64.2329 | 72,953.16 µm² |
| `fp32` | FPGA | 63.167 | 63.738 | 63.167 | 5,256 LUTs + 0 FF |
| `fp32` | 55 nm | 41.1512 | 41.1512 | 41.1512 | 28,605.08 µm² |
| `fp16` | FPGA | 49.255 | 50.235 | 49.255 | 2,535 LUTs + 0 FF |
| `fp16` | 55 nm | 23.2811 | 23.2811 | 23.2811 | 10,918.32 µm² |
| `bf16` | FPGA | 46.132 | 47.104 | 46.132 | 2,345 LUTs + 0 FF |
| `bf16` | 55 nm | 13.2128 | 13.2128 | 13.2128 | 5,802.16 µm² |
