# FpSrt4Sqrt

radix-4 开方。

操作数归一化到 `[1, 4)` 且指数为偶数，每周期用精确 radix-4 数字递推产生两位根。最终余数给出粘滞位，结果为正确舍入。

::: info 源代码
`src/main/scala/fp/sqrt/FpSrt4Sqrt.scala`
:::

## 参数

| 名称 | 类型 | 说明 |
| --- | --- | --- |
| `inFmt` | `FpFormat` | 操作数格式 |
| `outFmt` | `FpFormat` | 结果格式 |
| `policy` | `FpPolicy` | 数值策略 |

**延迟** = `rootBits / 2` 周期（`rootBits` 为偶数根位宽）

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
| `fp64-28cyc` | FPGA | 6.175 | 1.415 | 22.011 | 1,773 LUTs + 271 FF |
| `fp64-28cyc` | 55 nm | 5.2306 | 0.8862 | 8.8452 | 10,170.72 µm² |
| `fp32-13cyc` | FPGA | 5.140 | 1.400 | 19.971 | 883 LUTs + 144 FF |
| `fp32-13cyc` | 55 nm | 1.8460 | 0.8856 | 5.9918 | 5,274.36 µm² |
| `fp16-7cyc` | FPGA | 3.605 | 1.376 | 19.074 | 518 LUTs + 87 FF |
| `fp16-7cyc` | 55 nm | 1.8003 | 0.8856 | 4.9940 | 2,850.12 µm² |
| `bf16-5cyc` | FPGA | 4.126 | 1.471 | 16.515 | 409 LUTs + 79 FF |
| `bf16-5cyc` | 55 nm | 1.8209 | 0.8856 | 4.8894 | 2,471.00 µm² |
