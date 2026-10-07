# UIntNonRestoringDiv

迭代式不恢复余数除法器。

部分余数保持为带符号形式，根据其符号加上或减去除数，因此无需恢复步骤。最后一次修正处理负余数。

::: info 源代码
`src/main/scala/uint/div/UIntNonRestoringDiv.scala`
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
| `32bit-32cyc` | FPGA | 2.467 | 1.644 | 4.340 | 141 LUTs + 205 FF |
| `32bit-32cyc` | 55 nm | 5.9947 | 1.7030 | 5.9947 | 3,450.16 µm² |
