# FpRestoringSqrt

radix-2 恢复余数开方。

操作数归一化到 `[1, 4)` 且指数为偶数，逐周期用恢复余数递推产生一位根。最终余数给出粘滞位，结果为正确舍入。

::: info 源代码
`src/main/scala/fp/sqrt/FpRestoringSqrt.scala`
:::

## 参数

| 名称 | 类型 | 说明 |
| --- | --- | --- |
| `inFmt` | `FpFormat` | 操作数格式 |
| `outFmt` | `FpFormat` | 结果格式 |
| `policy` | `FpPolicy` | 数值策略 |

**延迟** = `outFmt.manWidth + 3` 周期

## IO

| 名称 | 方向 | 类型 | 说明 |
| --- | --- | --- | --- |
| `in` | 输入 | `Decoupled(FpUnaryReq)` | 解耦请求通道 |
| `in.valid` | 输入 | `Bool` | 请求有效 |
| `in.ready` | 输出 | `Bool` | 请求被接受 |
| `in.bits.src` | 输入 | `UInt(inFmt.width.W)` | 操作数 |
| `in.bits.rm` | 输入 | `UInt(3.W)` | 舍入模式 |
| `out` | 输出 | `Valid(FpUnaryResp)` | 有效响应通道 |
| `out.valid` | 输出 | `Bool` | 响应有效 |
| `out.bits.output` | 输出 | `UInt(outFmt.width.W)` | 结果 |
| `out.bits.fflags` | 输出 | `FpFlags` | IEEE-754 标志 |
| `flush` | 输入 | `Bool` | 中止进行中的运算 |

## PPA

> 另见[分析条件](/zh/guide/analysis-condition)

| 条件 | 平台 | In (ns) | Out (ns) | Max (ns) | 面积 |
| --- | --- | --- | --- | --- | --- |
| `fp64-55cyc` | FPGA | 5.915 | 1.416 | 20.888 | 1,596 LUTs + 264 FF |
| `fp64-55cyc` | 55 nm | 5.2639 | 0.8858 | 8.2514 | 8,034.04 µm² |
| `fp32-26cyc` | FPGA | 5.011 | 1.505 | 18.073 | 810 LUTs + 141 FF |
| `fp32-26cyc` | 55 nm | 1.8587 | 0.8798 | 5.1646 | 4,199.44 µm² |
| `fp16-13cyc` | FPGA | 3.789 | 1.389 | 16.265 | 446 LUTs + 80 FF |
| `fp16-13cyc` | 55 nm | 1.8282 | 0.8856 | 3.9789 | 2,201.64 µm² |
| `bf16-10cyc` | FPGA | 4.195 | 1.493 | 14.461 | 359 LUTs + 74 FF |
| `bf16-10cyc` | 55 nm | 1.7902 | 0.8855 | 4.0429 | 2,177.84 µm² |
