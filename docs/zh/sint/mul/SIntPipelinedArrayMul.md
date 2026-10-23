# SIntPipelinedArrayMul

使用 Baugh-Wooley 部分积的流水有符号进位保存阵列乘法器。

::: info 源代码
`src/main/scala/sint/mul/SIntPipelinedArrayMul.scala`
:::

## 参数

| 名称 | 类型 | 说明 |
| --- | --- | --- |
| `width` | `Int` | The width of the operands |
| `stages` | `Int` | The number of pipeline register layers, which is also the latency |

**延迟** = `stages` 周期

## IO

| 名称 | 方向 | 类型 | 说明 |
| --- | --- | --- | --- |
| `src1` | 输入 | `SInt(width.W)` | 第一个操作数 |
| `src2` | 输入 | `SInt(width.W)` | 第二个操作数 |
| `output` | 输出 | `SInt((2 * width).W)` | 乘积 `src1 * src2` |

## PPA

> 另见[分析条件](/zh/guide/analysis-condition)

| 条件 | 平台 | In (ns) | Out (ns) | Max (ns) | 面积 |
| --- | --- | --- | --- | --- | --- |
| `32bit-2cyc` | FPGA | 7.452 | 6.329 | 7.332 | 2,177 LUTs + 220 FF |
| `32bit-2cyc` | 55 nm | 3.4569 | 2.4716 | 3.4569 | 19,392.24 µm² |
