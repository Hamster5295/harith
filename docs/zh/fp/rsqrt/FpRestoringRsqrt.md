# FpRestoringRsqrt

radix-2 恢复余数倒数开方。

操作数归一化到 `[1, 4)` 且指数为偶数。残差 `x * y^2` 与乘积 `x * y` 以定点形式维护，每周期追加一位结果，选择使 `x * y^2 <= 1` 的数字。最终残差给出粘滞位，结果为正确舍入。

::: info 源代码
`src/main/scala/fp/rsqrt/FpRestoringRsqrt.scala`
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
| `fp64-55cyc` | FPGA | 5.968 | 1.453 | 21.189 | 3,044 LUTs + 443 FF |
| `fp64-55cyc` | 55 nm | 8.1365 | 0.8862 | 15.6430 | 19,876.08 µm² |
| `fp32-26cyc` | FPGA | 5.059 | 1.436 | 19.099 | 1,419 LUTs + 234 FF |
| `fp32-26cyc` | 55 nm | 4.3361 | 0.8862 | 10.8285 | 10,578.40 µm² |
| `fp16-13cyc` | FPGA | 3.558 | 1.354 | 16.442 | 746 LUTs + 128 FF |
| `fp16-13cyc` | 55 nm | 2.1229 | 0.8855 | 6.5856 | 5,530.84 µm² |
| `bf16-10cyc` | FPGA | 4.577 | 1.351 | 15.732 | 589 LUTs + 115 FF |
| `bf16-10cyc` | 55 nm | 2.7075 | 0.8858 | 5.6220 | 4,684.12 µm² |
