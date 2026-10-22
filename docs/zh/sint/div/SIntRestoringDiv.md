# SIntRestoringDiv

由无符号恢复除法器和符号修正构成的有符号除法器。

操作数先转换为幅值并送入 `UIntRestoringDiv`，再恢复商与余数的符号。商向零截断，余数取被除数的符号。除零与 `min / -1` 溢出遵循 RISC-V 语义。

::: info 源代码
`src/main/scala/sint/div/SIntRestoringDiv.scala`
:::

## 参数

| 名称 | 类型 | 说明 |
| --- | --- | --- |
| `width` | `Int` | 操作数位宽 |

**延迟** = `width` 周期

## IO

| 名称 | 方向 | 类型 | 说明 |
| --- | --- | --- | --- |
| `in` | 输入 | `Decoupled(SIntDivReq)` | 解耦请求通道 |
| `in.valid` | 输入 | `Bool` | 请求有效 |
| `in.ready` | 输出 | `Bool` | 请求被接受 |
| `in.bits.dividend` | 输入 | `SInt(width.W)` | 被除数 |
| `in.bits.divisor` | 输入 | `SInt(width.W)` | 除数 |
| `out` | 输出 | `Valid(SIntDivResp)` | 有效响应通道 |
| `out.valid` | 输出 | `Bool` | 响应有效 |
| `out.bits.quotient` | 输出 | `SInt(width.W)` | 商 |
| `out.bits.remainder` | 输出 | `SInt(width.W)` | 余数 |
| `out.bits.divideByZero` | 输出 | `Bool` | 除零标志 |
| `flush` | 输入 | `Bool` | 中止进行中的除法 |

## PPA

> 另见[分析条件](/zh/guide/analysis-condition)

| 条件 | 平台 | In (ns) | Out (ns) | Max (ns) | 面积 |
| --- | --- | --- | --- | --- | --- |
| `32bit-32cyc` | FPGA | 4.487 | 3.775 | 3.204 | 345 LUTs + 235 FF |
| `32bit-32cyc` | 55 nm | 4.9881 | 3.6132 | 4.9881 | 3,597.16 µm² |
