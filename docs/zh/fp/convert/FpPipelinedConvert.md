# FpPipelinedConvert

流水化浮点格式转换器。

操作数被延迟 `stages` 个周期，重新舍入对延迟后的操作数组合进行，因此吞吐率为每周期一次转换。`stages = 0` 使转换器变为组合逻辑，等价于 [`FpGenericConvert`](/zh/fp/convert/FpGenericConvert)。按照 RISC-V，NaN 为规范 NaN。

注意：`stages` 层寄存器是作用于操作数的普通延迟队列，而非放置在内部切分点的寄存器；它们之间的转换逻辑保持组合逻辑，只有当 EDA 工具将该队列重定时进逻辑后，预期的流水线深度才真正实现。

::: info 来源
`src/main/scala/fp/convert/FpPipelinedConvert.scala`
:::

## 参数

| 名称 | 类型 | 说明 |
| --- | --- | --- |
| `inFmt` | `FpFormat` | 输入格式 |
| `outFmt` | `FpFormat` | 输出格式 |
| `stages` | `Int` | 流水寄存器层数，也即延迟 |
| `policy` | `FpPolicy` | 数值策略 |

**延迟** = `stages` 周期

## IO

| 名称 | 方向 | 类型 | 说明 |
| --- | --- | --- | --- |
| `src` | 输入 | `UInt(inFmt.width.W)` | 输入值 |
| `rm` | 输入 | `UInt(3.W)` | RISC-V 舍入模式 |
| `output` | 输出 | `UInt(outFmt.width.W)` | 转换后的结果 |
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
| `fp64->fp32-2cyc` | FPGA | 1.364 | 21.083 | 20.512 | 1,073 LUTs + 132 FF |
| `fp64->fp32-2cyc` | 55 nm | 0.0053 | 6.8752 | 6.8752 | 2,601.76 µm² |
| `fp64->fp16-2cyc` | FPGA | 1.364 | 20.703 | 19.435 | 931 LUTs + 132 FF |
| `fp64->fp16-2cyc` | 55 nm | 0.0053 | 5.6125 | 5.6125 | 1,907.36 µm² |
| `fp64->bf16-2cyc` | FPGA | 1.364 | 20.170 | 19.506 | 952 LUTs + 132 FF |
| `fp64->bf16-2cyc` | 55 nm | 0.0053 | 5.0410 | 5.0410 | 1,961.40 µm² |
| `fp32->fp64-2cyc` | FPGA | 1.364 | 19.703 | 19.132 | 639 LUTs + 68 FF |
| `fp32->fp64-2cyc` | 55 nm | 0.0053 | 4.1978 | 4.1978 | 959.00 µm² |
| `fp32->fp16-2cyc` | FPGA | 1.364 | 19.160 | 18.172 | 504 LUTs + 68 FF |
| `fp32->fp16-2cyc` | 55 nm | 0.0053 | 4.8233 | 4.8233 | 1,248.52 µm² |
| `fp32->bf16-2cyc` | FPGA | 1.364 | 18.759 | 17.771 | 487 LUTs + 68 FF |
| `fp32->bf16-2cyc` | 55 nm | 0.0053 | 2.8789 | 2.8789 | 785.96 µm² |
| `fp16->fp64-2cyc` | FPGA | 1.364 | 17.633 | 16.653 | 506 LUTs + 36 FF |
| `fp16->fp64-2cyc` | 55 nm | 0.0053 | 2.5191 | 2.5191 | 413.56 µm² |
| `fp16->fp32-2cyc` | FPGA | 1.364 | 14.354 | 13.783 | 293 LUTs + 35 FF |
| `fp16->fp32-2cyc` | 55 nm | 0.0053 | 2.3150 | 2.3150 | 423.08 µm² |
| `fp16->bf16-2cyc` | FPGA | 1.364 | 13.677 | 13.106 | 242 LUTs + 35 FF |
| `fp16->bf16-2cyc` | 55 nm | 0.0053 | 2.5211 | 2.5211 | 557.48 µm² |
| `bf16->fp64-2cyc` | FPGA | 1.364 | 16.641 | 16.070 | 380 LUTs + 36 FF |
| `bf16->fp64-2cyc` | 55 nm | 0.0053 | 2.8729 | 2.8729 | 445.20 µm² |
| `bf16->fp32-2cyc` | FPGA | 1.364 | 15.865 | 15.294 | 304 LUTs + 36 FF |
| `bf16->fp32-2cyc` | 55 nm | 0.0053 | 1.4419 | 1.4419 | 271.60 µm² |
| `bf16->fp16-2cyc` | FPGA | 1.364 | 16.082 | 15.510 | 280 LUTs + 36 FF |
| `bf16->fp16-2cyc` | 55 nm | 0.0053 | 4.6696 | 4.6696 | 827.12 µm² |
