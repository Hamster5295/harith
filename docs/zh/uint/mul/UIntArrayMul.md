# UIntArrayMul

结构化进位保存阵列乘法器。

每个部分积行用一行全加器以进位保存形式累加，最后由行波进位链产生乘积。规则的结构带来最低成本，代价是 O(width) 关键路径。在 FPGA 上，通常更推荐推断出的 [`UIntMacroMul`](/zh/uint/mul/UIntMacroMul)。

::: info 来源
`src/main/scala/uint/mul/UIntArrayMul.scala`
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
| `output` | 输出 | `UInt((2 * width).W)` | 乘积 `src1 * src2` |

## PPA

> 另见[分析条件](/zh/guide/analysis-condition)

| 条件 | 平台 | In (ns) | Out (ns) | Max (ns) | 面积 |
| --- | --- | --- | --- | --- | --- |
| `32bit` | FPGA | 20.305 | 20.877 | 20.305 | 2,589 LUTs + 0 FF |
| `32bit` | 55 nm | 7.9127 | 7.9127 | 7.9127 | 16,088.52 µm² |
