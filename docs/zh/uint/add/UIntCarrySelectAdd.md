# UIntCarrySelectAdd

块进位选择加法器。

每个块针对两种可能的输入进位预先计算结果，并在真实进位到达后选出正确的那个。重复的逻辑将关键路径缩短为每块一次进位选择。

::: info 源代码
`src/main/scala/uint/add/UIntCarrySelectAdd.scala`
:::

## 参数

| 名称 | 类型 | 说明 |
| --- | --- | --- |
| `width` | `Int` | 操作数位宽 |
| `blockSize` | `Int` | 每个选择块的位数 |

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
| `32bit` | FPGA | 4.104 | 4.676 | 4.104 | 57 LUTs + 0 FF |
| `32bit` | 55 nm | 2.8304 | 2.8304 | 2.8304 | 416.64 µm² |
