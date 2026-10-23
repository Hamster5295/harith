# FpReciprocal

4 个模块。

## 对比

每个模块的代表性条件（`fp32`）。

| 模块 | 条件 | Max · FPGA | 面积 · FPGA | Max · 55 nm | 面积 · 55 nm |
| --- | --- | --- | --- | --- | --- |
| [FpRestoringReciprocal](./FpRestoringReciprocal.md) | `fp32-27cyc` | 18.282 ns | 746 LUTs + 136 FF | 5.5987 ns | 3,828.72 µm² |
| [FpSrt4Reciprocal](./FpSrt4Reciprocal.md) | `fp32-14cyc` | 19.950 ns | 912 LUTs + 135 FF | 6.0630 ns | 4,629.80 µm² |
| [FpNewtonReciprocal](./FpNewtonReciprocal.md) | `fp32` | 1.017 ns | 1,109 LUTs + 36 FF | 24.2199 ns | 41,807.08 µm² |
| [FpPipelinedNewtonReciprocal](./FpPipelinedNewtonReciprocal.md) | `fp32-6cyc` | 20.763 ns | 1,168 LUTs + 148 FF | 5.5111 ns | 37,731.12 µm² |

## 模块

- **[FpRestoringReciprocal](./FpRestoringReciprocal.md)** — radix-2 恢复余数倒数。
- **[FpSrt4Reciprocal](./FpSrt4Reciprocal.md)** — radix-4 倒数。
- **[FpNewtonReciprocal](./FpNewtonReciprocal.md)** — 带分段常数初值的 Newton-Raphson 倒数。
- **[FpPipelinedNewtonReciprocal](./FpPipelinedNewtonReciprocal.md)** — 固定深度流水的 Newton-Raphson 倒数。
