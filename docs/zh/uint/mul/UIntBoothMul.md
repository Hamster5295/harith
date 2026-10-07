# UIntBoothMul

改进 Booth radix-4 进位保存树乘法器。

乘数被重新编码，生成的部分积数量约为基于 AND 的 [`UIntTreeMul`](/zh/uint/mul/UIntTreeMul) 的一半。部分积由 Wallace 或 Dadda 网络归约为两行，再由提供的 [`UIntAdd`](/zh/uint/add/) 相加。

::: info 来源
`src/main/scala/uint/mul/UIntBoothMul.scala`
:::

## 参数

| 名称 | 类型 | 说明 |
| --- | --- | --- |
| `width` | `Int` | 操作数位宽 |
| `reductionStyle` | `ReductionStyle` | 部分积树的归约风格 |
| `adder` | `=> UIntAdd` | 最终进位传播加法器，必须为 `2 * width` 位宽 |

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
| `32bit` | FPGA | 10.234 | 10.806 | 10.234 | 2,269 LUTs + 0 FF |
| `32bit` | 55 nm | 5.3502 | 5.3502 | 5.3502 | 16,288.16 µm² |
