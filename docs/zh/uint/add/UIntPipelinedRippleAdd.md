# UIntPipelinedRippleAdd

流水化行波进位加法器。

进位链在每个块边界被切断，进位、操作数切片以及累加的和每个周期前进一个块。这样得到短的、以块为单位的临界路径，吞吐率为每周期一次加法，固定延迟等于块的数量。

::: info 源代码
`src/main/scala/uint/add/UIntPipelinedRippleAdd.scala`
:::

## 参数

| 名称 | 类型 | 说明 |
| --- | --- | --- |
| `width` | `Int` | 操作数位宽 |
| `blockSize` | `Int` | 每个流水级的位数 |

**延迟** = `ceil(width / blockSize)` 周期

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
| `32bit-8cyc` | FPGA | 2.115 | 0.912 | 1.791 | 88 LUTs + 156 FF |
| `32bit-8cyc` | 55 nm | 0.2891 | 0.3929 | 0.2891 | 2,615.76 µm² |
