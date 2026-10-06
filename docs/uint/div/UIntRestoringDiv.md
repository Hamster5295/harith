# UIntRestoringDiv

An iterative restoring divider.

The remainder is shifted in one dividend bit per cycle and the divisor is subtracted; when the subtraction underflows the remainder is restored and the quotient bit is zero. It is the smallest divider at the cost of one cycle per operand bit.

::: info Source
`src/main/scala/uint/div/UIntRestoringDiv.scala`
:::

## Parameters

- **`width`** — The width of the operands

## PPA

> See also [Analysis Condition](/guide/analysis-condition) 

| Condition | Platform | In (ns) | Out (ns) | Max (ns) | Area |
| --- | --- | --- | --- | --- | --- |
| `32bit-32cyc` | FPGA | 2.467 | 1.644 | 2.678 | 122 LUTs + 201 FF |
| `32bit-32cyc` | 55 nm | 6.6857 | 1.6885 | 6.6857 | 2,589.44 µm² |
