# UIntCarrySkipAdd

块进位跳过（进位旁路）加法器。

每个块内部行波进位，而所有位都传播的块允许输入进位跳过它，以很小的面积代价缩短最坏情况关键路径。

::: info 来源
`src/main/scala/uint/add/UIntCarrySkipAdd.scala`
:::

## 参数

| 名称 | 类型 | 说明 |
| --- | --- | --- |
| `width` | `Int` | 操作数位宽 |
| `blockSize` | `Int` | 每个跳过块的位数 |

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
| `32bit` | FPGA | 6.359 | 6.931 | 6.359 | 55 LUTs + 0 FF |
| `32bit` | 55 nm | 2.6155 | 2.6155 | 2.6155 | 308.00 µm² |
