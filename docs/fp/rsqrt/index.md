# FpRsqrt

4 modules.

## Comparison

Representative condition per module (`fp32`).

| Module | Condition | Max · FPGA | Area · FPGA | Max · 55 nm | Area · 55 nm |
| --- | --- | --- | --- | --- | --- |
| [FpRestoringRsqrt](./FpRestoringRsqrt.md) | `fp32-26cyc` | 19.099 ns | 1,419 LUTs + 234 FF | 10.8285 ns | 10,578.40 µm² |
| [FpSrt4Rsqrt](./FpSrt4Rsqrt.md) | `fp32-13cyc` | 24.933 ns | 1,987 LUTs + 233 FF | 16.6951 ns | 15,535.52 µm² |
| [FpNewtonRsqrt](./FpNewtonRsqrt.md) | `fp32` | 0.881 ns | 1,452 LUTs + 38 FF | 39.7161 ns | 101,573.36 µm² |
| [FpPipelinedNewtonRsqrt](./FpPipelinedNewtonRsqrt.md) | `fp32-11cyc` | 21.524 ns | 1,531 LUTs + 271 FF | 5.8993 ns | 85,531.04 µm² |

## Modules

- **[FpRestoringRsqrt](./FpRestoringRsqrt.md)** — A radix-2 restoring reciprocal square root.
- **[FpSrt4Rsqrt](./FpSrt4Rsqrt.md)** — A radix-4 reciprocal square root.
- **[FpNewtonRsqrt](./FpNewtonRsqrt.md)** — A Newton-Raphson reciprocal square root with a piecewise-constant seed.
- **[FpPipelinedNewtonRsqrt](./FpPipelinedNewtonRsqrt.md)** — A pipelined Newton-Raphson reciprocal square root with a fixed-depth pipeline.
