# FpPipelinedNewtonSqrt

固定深度流水的 Newton-Raphson 开方。

将组合 Newton-Raphson 链在每个乘法器和每个结果级之后切开，因此每周期可接收一个新操作数。倒数开方与最后的乘法与 `FpNewtonSqrt` 相同；结果为忠实舍入（误差在一个 ulp 以内）。

::: info 源代码
`src/main/scala/fp/sqrt/FpPipelinedNewtonSqrt.scala`
:::

## 参数

| 名称 | 类型 | 说明 |
| --- | --- | --- |
| `inFmt` | `FpFormat` | 操作数格式 |
| `outFmt` | `FpFormat` | 结果格式 |
| `mul` | `Int => UIntMul` | 组合无符号乘法器工厂 |
| `policy` | `FpPolicy` | 数值策略 |

**延迟** = `3 + 3 * iters` 周期

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
| `fp64-12cyc` | FPGA | 8.445 | 0.912 | 25.022 | 3,554 LUTs + 733 FF |
| `fp64-12cyc` | 55 nm | 5.6980 | 0.4508 | 8.0459 | 305,500.72 µm² |
| `fp32-12cyc` | FPGA | 7.048 | 0.912 | 21.793 | 1,568 LUTs + 320 FF |
| `fp32-12cyc` | 55 nm | 3.8242 | 0.4508 | 5.9647 | 94,503.08 µm² |
| `fp16-9cyc` | FPGA | 5.260 | 0.912 | 16.906 | 1,049 LUTs + 250 FF |
| `fp16-9cyc` | 55 nm | 2.9117 | 0.4508 | 4.1158 | 25,272.24 µm² |
| `bf16-9cyc` | FPGA | 6.139 | 0.912 | 16.529 | 601 LUTs + 56 FF |
| `bf16-9cyc` | 55 nm | 2.5437 | 0.4508 | 4.5610 | 18,198.60 µm² |
