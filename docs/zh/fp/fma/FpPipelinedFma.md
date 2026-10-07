# FpPipelinedFma

流水化浮点融合乘加器。

有效数乘积由流水化的 [`harith.uint.UIntPipelinedArrayMul`](/zh/uint/mul/UIntPipelinedArrayMul) 计算，其寄存器层数为 `stages`，随后对齐、加法与舍入对延迟后的操作数组合进行。`stages = 0` 使 FMA 变为组合逻辑。按照 RISC-V，NaN 为规范 NaN。

注意：只有有效数乘积由底层 [`harith.uint.UIntPipelinedArrayMul`](/zh/uint/mul/UIntPipelinedArrayMul) 在内部流水化；操作数由一个与乘法器延迟匹配的普通寄存器队列延迟，整个对齐/加法/舍入尾部保持组合逻辑，因此该部分的实际深度取决于 EDA 重定时。

::: info 源代码
`src/main/scala/fp/fma/FpPipelinedFma.scala`
:::

## 参数

| 名称 | 类型 | 说明 |
| --- | --- | --- |
| `aFmt` | `FpFormat` | 被乘数的格式 |
| `bFmt` | `FpFormat` | 乘数的格式 |
| `cFmt` | `FpFormat` | 加数的格式 |
| `outFmt` | `FpFormat` | 结果的格式 |
| `stages` | `Int` | 流水寄存器层数，也即延迟 |
| `policy` | `FpPolicy` | 数值策略 |

**延迟** = `stages` 周期

## IO

| 名称 | 方向 | 类型 | 说明 |
| --- | --- | --- | --- |
| `src1` | 输入 | `UInt(aFmt.width.W)` | 被乘数 |
| `src2` | 输入 | `UInt(bFmt.width.W)` | 乘数 |
| `add` | 输入 | `UInt(cFmt.width.W)` | 加数 |
| `rm` | 输入 | `UInt(3.W)` | RISC-V 舍入模式 |
| `output` | 输出 | `UInt(outFmt.width.W)` | 舍入后的结果 |
| `fflags` | 输出 | `FpFlags` | IEEE-754 异常标志 |
| `fflags.nx` | 输出 | `Bool` | 不精确 |
| `fflags.uf` | 输出 | `Bool` | 下溢 |
| `fflags.of` | 输出 | `Bool` | 上溢 |
| `fflags.dz` | 输出 | `Bool` | 除零 |
| `fflags.nv` | 输出 | `Bool` | 非法操作 |

## PPA

> 另见[分析条件](/zh/guide/analysis-condition)

| 条件 | 平台 | In (ns) | Out (ns) | Max (ns) | 面积 |
| --- | --- | --- | --- | --- | --- |
| `fp64-2cyc` | FPGA | 12.581 | 63.282 | 62.711 | 17,188 LUTs + 696 FF |
| `fp64-2cyc` | 55 nm | 4.8029 | 31.7407 | 31.7407 | 51,201.08 µm² |
| `fp32-2cyc` | FPGA | 6.065 | 47.980 | 46.992 | 6,038 LUTs + 327 FF |
| `fp32-2cyc` | 55 nm | 2.0449 | 21.9171 | 21.9171 | 17,519.04 µm² |
| `fp16-2cyc` | FPGA | 3.137 | 40.049 | 39.478 | 2,632 LUTs + 160 FF |
| `fp16-2cyc` | 55 nm | 0.9560 | 16.4629 | 16.4629 | 7,754.32 µm² |
| `bf16-2cyc` | FPGA | 2.712 | 37.846 | 37.275 | 2,294 LUTs + 143 FF |
| `bf16-2cyc` | 55 nm | 0.5774 | 13.2360 | 13.2360 | 6,961.64 µm² |
