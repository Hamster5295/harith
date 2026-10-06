# UIntRippleAdd

结构化行波进位加法器。

这是最节省资源的组合加法器，代价是 O(width) 的关键路径。在 FPGA 上，通常更推荐推断出的 [[UIntMacroAdd]]。

::: info 来源
`src/main/scala/uint/add/UIntRippleAdd.scala`
:::

## 参数

| 名称 | 类型 | 说明 |
| --- | --- | --- |
| `width` | `Int` | 操作数位宽 |

**延迟** = 0 周期

## IO

| 名称 | 方向 | 类型 | 说明 |
| --- | --- | --- | --- |
| `src1` | 输入 | `UInt(width.W)` | 第一个操作数 |
| `src2` | 输入 | `UInt(width.W)` | 第二个操作数 |
| `carry` | 输入 | `Bool` | 进位输入 |
| `output` | 输出 | `UInt((width + 1).W)` | 和 `src1 + src2 + carry` |

## PPA

> 另见[分析条件](/zh/guide/analysis-condition)

| 条件 | 平台 | In (ns) | Out (ns) | Max (ns) | 面积 |
| --- | --- | --- | --- | --- | --- |
| `32bit` | FPGA | 6.366 | 6.938 | 6.366 | 56 LUTs + 0 FF |
| `32bit` | 55 nm | 2.6383 | 2.6383 | 2.6383 | 300.72 µm² |
