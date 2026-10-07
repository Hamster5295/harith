# UIntRestoringDiv

迭代式恢复余数除法器。

余数每周期移入一位被除数的位并减去除数；当减法下溢时恢复余数，商位为 0。它是最小的除法器，代价是每个操作数位需要一个周期。

::: info 源代码
`src/main/scala/uint/div/UIntRestoringDiv.scala`
:::

## 参数

| 名称 | 类型 | 说明 |
| --- | --- | --- |
| `width` | `Int` | 操作数位宽 |

**延迟** = `width` 周期

## IO

| 名称 | 方向 | 类型 | 说明 |
| --- | --- | --- | --- |
| `in` | 输入 | `Decoupled(UIntDivReq)` | 解耦请求通道 |
| `in.valid` | 输入 | `Bool` | 请求有效 |
| `in.ready` | 输出 | `Bool` | 请求被接受 |
| `in.bits.dividend` | 输入 | `UInt(width.W)` | 被除数 |
| `in.bits.divisor` | 输入 | `UInt(width.W)` | 除数 |
| `out` | 输出 | `Valid(UIntDivResp)` | 有效响应通道 |
| `out.valid` | 输出 | `Bool` | 响应有效 |
| `out.bits.quotient` | 输出 | `UInt(width.W)` | 商 |
| `out.bits.remainder` | 输出 | `UInt(width.W)` | 余数 |
| `out.bits.divideByZero` | 输出 | `Bool` | 除零标志 |
| `flush` | 输入 | `Bool` | 中止进行中的除法 |

## PPA

> 另见[分析条件](/zh/guide/analysis-condition)

| 条件 | 平台 | In (ns) | Out (ns) | Max (ns) | 面积 |
| --- | --- | --- | --- | --- | --- |
| `32bit-32cyc` | FPGA | 2.467 | 1.644 | 2.678 | 122 LUTs + 201 FF |
| `32bit-32cyc` | 55 nm | 6.6857 | 1.6885 | 6.6857 | 2,589.44 µm² |
