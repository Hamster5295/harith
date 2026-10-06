# FpConvert

2 个模块。

## 对比

每个模块的代表性条件。

| 模块 | 条件 | Max · FPGA | 面积 · FPGA | Max · 55 nm | 面积 · 55 nm |
| --- | --- | --- | --- | --- | --- |
| [FpGenericConvert](./FpGenericConvert.md) | `fp32->fp64` | 19.632 ns | 707 LUTs + 0 FF | 3.5764 ns | 565.32 µm² |
| [FpPipelinedConvert](./FpPipelinedConvert.md) | `fp32->fp64-2cyc` | 19.132 ns | 639 LUTs + 68 FF | 4.1978 ns | 959.00 µm² |

## 模块

- **[FpGenericConvert](./FpGenericConvert.md)** —— 浮点格式转换器。
- **[FpPipelinedConvert](./FpPipelinedConvert.md)** —— 流水化浮点格式转换器。
