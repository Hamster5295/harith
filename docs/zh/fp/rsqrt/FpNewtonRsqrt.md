# FpNewtonRsqrt

带分段常数初值的 Newton-Raphson 倒数开方。

初值来自以高位尾数为索引的查找表；定点 Newton-Raphson 迭代平方收敛到 `1 / sqrt(有效数)`。结果为忠实舍入（误差在一个 ulp 以内），不是正确舍入。

::: info 源代码
`src/main/scala/fp/rsqrt/FpNewtonRsqrt.scala`
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
| `fp64` | FPGA | 124.845 | 0.912 | 0.881 | 3,276 LUTs + 70 FF |
| `fp64` | 55 nm | 64.1853 | 0.4560 | 64.1853 | 318,050.60 µm² |
| `fp32` | FPGA | 90.303 | 0.912 | 0.881 | 1,452 LUTs + 38 FF |
| `fp32` | 55 nm | 39.7161 | 0.4560 | 39.7161 | 101,573.36 µm² |
| `fp16` | FPGA | 55.913 | 0.912 | 0.881 | 919 LUTs + 22 FF |
| `fp16` | 55 nm | 23.8437 | 0.4560 | 23.8437 | 26,401.48 µm² |
| `bf16` | FPGA | 45.327 | 0.912 | 0.881 | 510 LUTs + 22 FF |
| `bf16` | 55 nm | 13.1586 | 0.4560 | 13.1586 | 12,145.28 µm² |
