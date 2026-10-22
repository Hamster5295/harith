# FpSqrt

3 个模块。

## 对比

每个模块的代表性条件（`fp32`）。

| 模块 | 条件 | Max · FPGA | 面积 · FPGA | Max · 55 nm | 面积 · 55 nm |
| --- | --- | --- | --- | --- | --- |
| [FpRestoringSqrt](./FpRestoringSqrt.md) | `fp32-26cyc` | 18.073 ns | 810 LUTs + 141 FF | 5.1646 ns | 4,199.44 µm² |
| [FpSrt4Sqrt](./FpSrt4Sqrt.md) | `fp32-13cyc` | 19.971 ns | 883 LUTs + 144 FF | 5.9918 ns | 5,274.36 µm² |
| [FpNewtonSqrt](./FpNewtonSqrt.md) | `fp32` | 0.881 ns | 1,463 LUTs + 36 FF | 42.9883 ns | 112,291.20 µm² |

## 模块

- **[FpRestoringSqrt](./FpRestoringSqrt.md)** — radix-2 恢复余数开方。
- **[FpSrt4Sqrt](./FpSrt4Sqrt.md)** — radix-4 开方。
- **[FpNewtonSqrt](./FpNewtonSqrt.md)** — 带分段常数初值的 Newton-Raphson 开方。
