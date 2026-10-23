# FpSqrt

4 modules.

## Comparison

Representative condition per module (`fp32`).

| Module | Condition | Max · FPGA | Area · FPGA | Max · 55 nm | Area · 55 nm |
| --- | --- | --- | --- | --- | --- |
| [FpRestoringSqrt](./FpRestoringSqrt.md) | `fp32-26cyc` | 18.073 ns | 810 LUTs + 141 FF | 5.1646 ns | 4,199.44 µm² |
| [FpSrt4Sqrt](./FpSrt4Sqrt.md) | `fp32-13cyc` | 19.971 ns | 883 LUTs + 144 FF | 5.9918 ns | 5,274.36 µm² |
| [FpNewtonSqrt](./FpNewtonSqrt.md) | `fp32` | 0.881 ns | 1,463 LUTs + 36 FF | 42.9883 ns | 112,291.20 µm² |
| [FpPipelinedNewtonSqrt](./FpPipelinedNewtonSqrt.md) | `fp32-12cyc` | 21.793 ns | 1,568 LUTs + 320 FF | 5.9647 ns | 94,503.08 µm² |

## Modules

- **[FpRestoringSqrt](./FpRestoringSqrt.md)** — A radix-2 restoring square root.
- **[FpSrt4Sqrt](./FpSrt4Sqrt.md)** — A radix-4 square root.
- **[FpNewtonSqrt](./FpNewtonSqrt.md)** — A Newton-Raphson square root with a piecewise-constant seed.
- **[FpPipelinedNewtonSqrt](./FpPipelinedNewtonSqrt.md)** — A pipelined Newton-Raphson square root with a fixed-depth pipeline.
