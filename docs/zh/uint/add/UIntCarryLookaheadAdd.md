# UIntCarryLookaheadAdd

分层先行进位加法器。

各位被划分成组。每组用一个先行进位网络解析其内部进位，暴露一个组生成/传播对，组间进位由第二级先行进位解析。这使关键路径保持对数级，同时使用的逻辑远少于全并行前缀加法器。

::: info 源代码
`src/main/scala/uint/add/UIntCarryLookaheadAdd.scala`
:::

## 参数

| 名称 | 类型 | 说明 |
| --- | --- | --- |
| `width` | `Int` | 操作数位宽 |
| `groupSize` | `Int` | 每个先行进位组的位数 |

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
| `32bit` | FPGA | 3.879 | 4.451 | 3.879 | 70 LUTs + 0 FF |
| `32bit` | 55 nm | 1.4466 | 1.4466 | 1.4466 | 493.08 µm² |
