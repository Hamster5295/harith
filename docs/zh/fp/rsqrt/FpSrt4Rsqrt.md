# FpSrt4Rsqrt

radix-4 倒数开方。

操作数归一化到 `[1, 4)` 且指数为偶数。残差 `x * y^2` 与乘积 `x * y` 以定点形式维护，每周期追加两位结果，选择使 `x * y^2 <= 1` 的 radix-4 数字。最终残差给出粘滞位，结果为正确舍入。

::: info 源代码
`src/main/scala/fp/rsqrt/FpSrt4Rsqrt.scala`
:::

## 参数

| 名称 | 类型 | 说明 |
| --- | --- | --- |
| `inFmt` | `FpFormat` | 操作数格式 |
| `outFmt` | `FpFormat` | 结果格式 |
| `policy` | `FpPolicy` | 数值策略 |

**延迟** = `ceil((outFmt.manWidth + 3) / 2)` 周期

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
| `fp64-28cyc` | FPGA | 6.722 | 1.442 | 30.759 | 4,341 LUTs + 457 FF |
| `fp64-28cyc` | 55 nm | 7.6004 | 0.8862 | 28.8763 | 32,988.76 µm² |
| `fp32-13cyc` | FPGA | 4.848 | 1.456 | 24.933 | 1,987 LUTs + 233 FF |
| `fp32-13cyc` | 55 nm | 2.8003 | 0.8798 | 16.6951 | 15,535.52 µm² |
| `fp16-7cyc` | FPGA | 3.561 | 1.456 | 23.851 | 990 LUTs + 133 FF |
| `fp16-7cyc` | 55 nm | 2.0890 | 0.8864 | 10.7366 | 8,750.84 µm² |
| `bf16-5cyc` | FPGA | 4.125 | 1.453 | 21.229 | 831 LUTs + 118 FF |
| `bf16-5cyc` | 55 nm | 1.9298 | 0.8855 | 8.1891 | 6,666.24 µm² |
