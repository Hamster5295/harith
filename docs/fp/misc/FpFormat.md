# FpFormat

An IEEE-754 binary floating-point format. It is the operand and result type of every `fp` module, so mixed-precision operations need no extra code.

A value has `1 + expWidth + manWidth` bits. The exponent bias is the IEEE binary interchange default `2^(expWidth - 1) - 1`, as it is derived rather than stored.

::: info Source
`src/main/scala/fp/FpFormat.scala`
:::

## Construction

| Name | Type | Description |
| --- | --- | --- |
| `expWidth` | `Int` | The number of exponent bits (at least 2) |
| `manWidth` | `Int` | The number of explicit stored mantissa bits (at least 1) |

## Predefined formats

| Name | `expWidth` | `manWidth` | Width |
| --- | --- | --- | --- |
| `Fp64` | 11 | 52 | 64 |
| `Fp32` | 8 | 23 | 32 |
| `Tf32` | 8 | 10 | 19 |
| `Fp16` | 5 | 10 | 16 |
| `Bf16` | 8 | 7 | 16 |

## Derived members

| Name | Type | Description |
| --- | --- | --- |
| `width` | `Int` | The total number of bits, `1 + expWidth + manWidth` |
| `expMask` / `manMask` | `BigInt` | The all-ones exponent / mantissa field |
| `signBit` | `Int` | The index of the sign bit |
| `bias` | `BigInt` | The exponent bias `2^(expWidth - 1) - 1` |
| `maxExp` / `minExp` | `BigInt` | The largest / smallest unbiased normal exponent |
| `minSubExp` | `BigInt` | The smallest unbiased subnormal exponent |
| `zero(sign)` | `UInt` | The signed zero bit pattern |
| `infinity` / `infinityMag` | `UInt` | The infinity bit pattern / unsigned infinity magnitude |
| `canonicalNaN` | `UInt` | The canonical quiet NaN bit pattern mandated by RISC-V |
| `maxFinite` / `maxFiniteMag` | `UInt` | The largest finite magnitude, with positive sign / alone |
