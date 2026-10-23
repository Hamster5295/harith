# UIntPrefixAdd

A fully parallel prefix adder.

All carries are computed by a single prefix network, giving a logarithmic critical path. The [`PrefixStyle`](/uint/misc/PrefixStyle) selects the network shape and therefore the area/performance point.

::: info Source
`src/main/scala/uint/add/UIntPrefixAdd.scala`
:::

## Parameters

| Name | Type | Description |
| --- | --- | --- |
| `width` | `Int` | The width of the operands |
| `style` | `PrefixStyle` | The parallel prefix network style |

**Delay** = 0 cycles

## IO

| Name | Direction | Type | Description |
| --- | --- | --- | --- |
| `src1` | Input | `UInt(width.W)` | First operand |
| `src2` | Input | `UInt(width.W)` | Second operand |
| `carry` | Input | `Bool` | Carry in |
| `output` | Output | `UInt((width + 1).W)` | Sum `src1 + src2 + carry` |

## PPA

> See also [Analysis Condition](/guide/analysis-condition) 

| Condition | Platform | In (ns) | Out (ns) | Max (ns) | Area |
| --- | --- | --- | --- | --- | --- |
| `32bit-kogge-stone` | FPGA | 3.816 | 4.388 | 3.816 | 159 LUTs + 0 FF |
| `32bit-kogge-stone` | 55 nm | 1.3195 | 1.3195 | 1.3195 | 945.84 µm² |
| `32bit-brent-kung` | FPGA | 4.169 | 4.741 | 4.169 | 68 LUTs + 0 FF |
| `32bit-brent-kung` | 55 nm | 1.3866 | 1.3866 | 1.3866 | 509.32 µm² |
| `32bit-sklansky` | FPGA | 3.867 | 4.439 | 3.867 | 81 LUTs + 0 FF |
| `32bit-sklansky` | 55 nm | 1.5115 | 1.5115 | 1.5115 | 656.32 µm² |
| `32bit-han-carlson` | FPGA | 4.142 | 4.714 | 4.142 | 97 LUTs + 0 FF |
| `32bit-han-carlson` | 55 nm | 1.7993 | 1.7993 | 1.7993 | 715.12 µm² |
