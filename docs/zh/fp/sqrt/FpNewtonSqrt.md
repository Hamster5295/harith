# FpNewtonSqrt

带分段常数初值的 Newton-Raphson 开方。

先用定点 Newton-Raphson 迭代计算倒数开方，再与操作数相乘。结果为忠实舍入（误差在一个 ulp 以内），不是正确舍入。

::: info 源代码
`src/main/scala/fp/sqrt/FpNewtonSqrt.scala`
:::

## 参数

| 名称 | 类型 | 说明 |
| --- | --- | --- |
| `inFmt` | `FpFormat` | 操作数格式 |
| `outFmt` | `FpFormat` | 结果格式 |
| `mul` | `Int => UIntMul` | 组合无符号乘法器工厂 |
| `policy` | `FpPolicy` | 数值策略 |

**延迟** = 1 周期

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
| `fp64` | FPGA | 134.717 | 0.912 | 0.881 | 3,438 LUTs + 68 FF |
| `fp64` | 55 nm | 73.8453 | 0.4557 | 73.8453 | 367,311.00 µm² |
| `fp32` | FPGA | 95.495 | 0.912 | 0.881 | 1,463 LUTs + 36 FF |
| `fp32` | 55 nm | 42.9883 | 0.4557 | 42.9883 | 112,291.20 µm² |
| `fp16` | FPGA | 60.256 | 0.912 | 0.881 | 973 LUTs + 20 FF |
| `fp16` | 55 nm | 25.5042 | 0.4560 | 25.5042 | 29,868.44 µm² |
| `bf16` | FPGA | 48.873 | 0.912 | 0.881 | 519 LUTs + 20 FF |
| `bf16` | 55 nm | 14.7767 | 0.4560 | 14.7767 | 13,866.44 µm² |
