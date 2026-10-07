# UIntSrt2Div

迭代式 radix-2 SRT 除法器。

每次迭代通过将部分余数的两倍与 `+-divisor` 比较，产生一个来自 `{-1, 0, 1}` 的冗余带符号商位，这些商位被累加成带符号商，并在最后修正一次。它无需恢复步骤，每个操作数位一个周期完成。

::: info 源代码
`src/main/scala/uint/div/UIntSrt2Div.scala`
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
| `32bit-32cyc` | FPGA | 2.467 | 1.498 | 13.153 | 324 LUTs + 236 FF |
| `32bit-32cyc` | 55 nm | 6.6244 | 0.8858 | 6.6244 | 5,332.88 µm² |
