# PrefixStyle

[[UIntPrefixAdd]]、[[UIntPipelinedPrefixAdd]] 与 [[UIntCarryLookaheadAdd]] 共用的并行前缀网络风格。

这些风格在逻辑深度与面积、布线之间权衡，覆盖从高性能到资源受限的前缀加法器范围。`n` 表示网络的（补齐到 2 的幂的）位宽。

::: info 来源
`src/main/scala/uint/add/utils/PrefixStyle.scala`
:::

## 风格

| 名称 | 深度 | 说明 |
| --- | --- | --- |
| `KoggeStone` | `log2(n)` | 深度最小，但面积与扇出最大。 |
| `BrentKung` | `2 * log2(n) - 1` | 面积与扇出最小。 |
| `Sklansky` | `log2(n)` | 深度最小，面积小于 `KoggeStone`，但扇出较大。 |
| `HanCarlson` | `log2(n) + 1` | 稀疏的 Kogge-Stone 混合结构，在面积与布线之间取得平衡。 |

## 用法

`style` 是 [[UIntPrefixAdd]] 与 [[UIntPipelinedPrefixAdd]] 的构造参数。[[UIntCarryLookaheadAdd]] 在内部固定使用 `Sklansky`，而浮点前缀变体使用 `KoggeStone`。
