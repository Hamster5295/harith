# ReductionStyle

[`UIntTreeMul`](/zh/uint/mul/UIntTreeMul)、[`UIntBoothMul`](/zh/uint/mul/UIntBoothMul)、[`UIntPipelinedTreeMul`](/zh/uint/mul/UIntPipelinedTreeMul)、[`UIntTreeFma`](/zh/uint/fma/UIntTreeFma)、[`UIntBoothFma`](/zh/uint/fma/UIntBoothFma)、[`UIntPipelinedTreeFma`](/zh/uint/fma/UIntPipelinedTreeFma) 与 [`UIntPipelinedBoothFma`](/zh/uint/fma/UIntPipelinedBoothFma) 共用的部分积归约风格。

这些风格在进位保存压缩器的数量与归约布线之间权衡，覆盖从最快到最小的树乘法器范围。

::: info 源代码
`src/main/scala/uint/mul/utils/ReductionStyle.scala`
:::

## 风格

| 名称 | 说明 |
| --- | --- |
| `Wallace` | 每一级对所有至少三位的列进行压缩；最快的归约，使用最多压缩器。 |
| `Dadda` | 每一级仅压缩高于 Dadda 目标高度的列；压缩器少于 `Wallace`，但网络规整性略差。 |

## 用法

`reductionStyle` 是树/booth 乘法器与 FMA 变体的构造参数。浮点树/booth 变体在内部固定使用 `Dadda`。

## PPA

使用 `UIntTreeMul(32, style, ...)` 测量。

| 风格 | 条件 | Max · FPGA | 面积 · FPGA | Max · 55 nm | 面积 · 55 nm |
| --- | --- | --- | --- | --- | --- |
| `Dadda` | `32bit-dadda` | 10.316 ns | 1,616 LUTs + 0 FF | 4.8125 ns | 10,564.68 µm² |
| `Wallace` | `32bit-wallace` | 10.274 ns | 1,568 LUTs + 0 FF | 4.7953 ns | 10,695.44 µm² |
