# UIntTreeMul

使用 AND 部分积的进位保存树乘法器。

部分积矩阵由 Wallace 或 Dadda 网络归约为两行，再由提供的 [`UIntAdd`](/zh/uint/add/) 相加。归约是组合逻辑，因此延迟等于最终加法器的延迟。

::: info 源代码
`src/main/scala/uint/mul/UIntTreeMul.scala`
:::

## 参数

| 名称 | 类型 | 说明 |
| --- | --- | --- |
| `width` | `Int` | 操作数位宽 |
| `reductionStyle` | `ReductionStyle` | 部分积树的归约风格 |
| `adder` | `Int => UIntAdd` | 最终进位传播加法器的工厂，传入 `2 * width` 位宽 |

**延迟** = `adder.latency` 周期

## IO

| 名称 | 方向 | 类型 | 说明 |
| --- | --- | --- | --- |
| `src1` | 输入 | `UInt(width.W)` | 第一个操作数 |
| `src2` | 输入 | `UInt(width.W)` | 第二个操作数 |
| `output` | 输出 | `UInt((2 * width).W)` | 乘积 `src1 * src2` |

## PPA

> 另见[分析条件](/zh/guide/analysis-condition)

| 条件 | 平台 | In (ns) | Out (ns) | Max (ns) | 面积 |
| --- | --- | --- | --- | --- | --- |
| `32bit-dadda` | FPGA | 10.316 | 10.888 | 10.316 | 1,616 LUTs + 0 FF |
| `32bit-dadda` | 55 nm | 4.8125 | 4.8125 | 4.8125 | 10,564.68 µm² |
| `32bit-wallace` | FPGA | 10.274 | 10.846 | 10.274 | 1,568 LUTs + 0 FF |
| `32bit-wallace` | 55 nm | 4.7953 | 4.7953 | 4.7953 | 10,695.44 µm² |
