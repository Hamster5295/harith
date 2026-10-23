# FpRsqrt

4 个模块。

## 对比

每个模块的代表性条件（`fp32`）。

| 模块 | 条件 | Max · FPGA | 面积 · FPGA | Max · 55 nm | 面积 · 55 nm |
| --- | --- | --- | --- | --- | --- |
| [FpRestoringRsqrt](./FpRestoringRsqrt.md) | `fp32-26cyc` | 19.099 ns | 1,419 LUTs + 234 FF | 10.8285 ns | 10,578.40 µm² |
| [FpSrt4Rsqrt](./FpSrt4Rsqrt.md) | `fp32-13cyc` | 24.933 ns | 1,987 LUTs + 233 FF | 16.6951 ns | 15,535.52 µm² |
| [FpNewtonRsqrt](./FpNewtonRsqrt.md) | `fp32` | 0.881 ns | 1,452 LUTs + 38 FF | 39.7161 ns | 101,573.36 µm² |
| [FpPipelinedNewtonRsqrt](./FpPipelinedNewtonRsqrt.md) | `fp32-11cyc` | 21.524 ns | 1,531 LUTs + 271 FF | 5.8993 ns | 85,531.04 µm² |

## 模块

- **[FpRestoringRsqrt](./FpRestoringRsqrt.md)** — radix-2 恢复余数倒数开方。
- **[FpSrt4Rsqrt](./FpSrt4Rsqrt.md)** — radix-4 倒数开方。
- **[FpNewtonRsqrt](./FpNewtonRsqrt.md)** — 带分段常数初值的 Newton-Raphson 倒数开方。
- **[FpPipelinedNewtonRsqrt](./FpPipelinedNewtonRsqrt.md)** — 固定深度流水的 Newton-Raphson 倒数开方。
