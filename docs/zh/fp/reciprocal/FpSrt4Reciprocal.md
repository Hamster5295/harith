# FpSrt4Reciprocal

radix-4 倒数。

操作数归一化到 `[1, 2)`，每周期用精确 radix-4 数字递推（数字 `0..3`）产生两位商。最终余数给出粘滞位，结果为正确舍入。

::: info 源代码
`src/main/scala/fp/reciprocal/FpSrt4Reciprocal.scala`
:::

## 参数

| 名称 | 类型 | 说明 |
| --- | --- | --- |
| `inFmt` | `FpFormat` | 操作数格式 |
| `outFmt` | `FpFormat` | 结果格式 |
| `policy` | `FpPolicy` | 数值策略 |

**延迟** = `ceil((outFmt.manWidth + 4) / 2)` 周期

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
| `fp64-28cyc` | FPGA | 5.642 | 1.365 | 22.867 | 1,844 LUTs + 259 FF |
| `fp64-28cyc` | 55 nm | 3.0128 | 0.8798 | 9.5054 | 9,808.96 µm² |
| `fp32-14cyc` | FPGA | 4.334 | 1.362 | 19.950 | 912 LUTs + 135 FF |
| `fp32-14cyc` | 55 nm | 2.3968 | 0.8913 | 6.0630 | 4,629.80 µm² |
| `fp16-7cyc` | FPGA | 3.590 | 1.373 | 19.557 | 514 LUTs + 75 FF |
| `fp16-7cyc` | 55 nm | 1.9617 | 0.8856 | 4.0160 | 2,563.96 µm² |
| `bf16-6cyc` | FPGA | 3.643 | 1.485 | 17.876 | 455 LUTs + 70 FF |
| `bf16-6cyc` | 55 nm | 1.8763 | 0.8856 | 4.2271 | 2,255.68 µm² |
