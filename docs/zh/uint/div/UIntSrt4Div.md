# UIntSrt4Div

迭代式 radix-4 除法器。

每次迭代消耗两位被除数，并通过将移位后的部分余数与除数及其倍数 `2 * divisor`、`3 * divisor` 比较，产生一个来自 `{0, 1, 2, 3}` 的商位。商位被精确选择，因此部分余数始终小于除数，无需恢复或最终修正步骤。这使迭代次数比 radix-2 除法器减半。

::: info 源代码
`src/main/scala/uint/div/UIntSrt4Div.scala`
:::

## 参数

| 名称 | 类型 | 说明 |
| --- | --- | --- |
| `width` | `Int` | 操作数位宽 |

**延迟** = `(width + 1) / 2` 周期

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
| `32bit-16cyc` | FPGA | 2.467 | 1.528 | 4.338 | 328 LUTs + 202 FF |
| `32bit-16cyc` | 55 nm | 5.8867 | 0.8927 | 5.8867 | 3,832.64 µm² |
