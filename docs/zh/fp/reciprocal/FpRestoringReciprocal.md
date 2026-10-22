# FpRestoringReciprocal

radix-2 恢复余数倒数。

操作数归一化到 `[1, 2)`，逐周期用恢复余数递推产生 `1 / 有效数` 的商位，最终余数给出粘滞位。结果为正确舍入。

::: info 源代码
`src/main/scala/fp/reciprocal/FpRestoringReciprocal.scala`
:::

## 参数

| 名称 | 类型 | 说明 |
| --- | --- | --- |
| `inFmt` | `FpFormat` | 操作数格式 |
| `outFmt` | `FpFormat` | 结果格式 |
| `policy` | `FpPolicy` | 数值策略 |

**延迟** = `outFmt.manWidth + 4` 周期

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
| `fp64-56cyc` | FPGA | 5.642 | 1.362 | 20.281 | 1,553 LUTs + 259 FF |
| `fp64-56cyc` | 55 nm | 4.2967 | 0.8856 | 8.7567 | 7,825.16 µm² |
| `fp32-27cyc` | FPGA | 4.334 | 1.477 | 18.282 | 746 LUTs + 136 FF |
| `fp32-27cyc` | 55 nm | 2.3376 | 0.8798 | 5.5987 | 3,828.72 µm² |
| `fp16-14cyc` | FPGA | 3.590 | 1.482 | 16.867 | 425 LUTs + 75 FF |
| `fp16-14cyc` | 55 nm | 1.9488 | 0.8917 | 3.7728 | 2,119.04 µm² |
| `bf16-11cyc` | FPGA | 3.643 | 1.373 | 14.442 | 348 LUTs + 69 FF |
| `bf16-11cyc` | 55 nm | 2.1707 | 0.8858 | 3.4835 | 1,924.72 µm² |
