# FpReciprocal

4 modules.

## Comparison

Representative condition per module (`fp32`).

| Module | Condition | Max · FPGA | Area · FPGA | Max · 55 nm | Area · 55 nm |
| --- | --- | --- | --- | --- | --- |
| [FpRestoringReciprocal](./FpRestoringReciprocal.md) | `fp32-27cyc` | 18.282 ns | 746 LUTs + 136 FF | 5.5987 ns | 3,828.72 µm² |
| [FpSrt4Reciprocal](./FpSrt4Reciprocal.md) | `fp32-14cyc` | 19.950 ns | 912 LUTs + 135 FF | 6.0630 ns | 4,629.80 µm² |
| [FpNewtonReciprocal](./FpNewtonReciprocal.md) | `fp32` | 1.017 ns | 1,109 LUTs + 36 FF | 24.2199 ns | 41,807.08 µm² |
| [FpPipelinedNewtonReciprocal](./FpPipelinedNewtonReciprocal.md) | `fp32-6cyc` | 20.763 ns | 1,168 LUTs + 148 FF | 5.5111 ns | 37,731.12 µm² |

## Modules

- **[FpRestoringReciprocal](./FpRestoringReciprocal.md)** — A radix-2 restoring reciprocal.
- **[FpSrt4Reciprocal](./FpSrt4Reciprocal.md)** — A radix-4 reciprocal.
- **[FpNewtonReciprocal](./FpNewtonReciprocal.md)** — A Newton-Raphson reciprocal with a piecewise-constant seed.
- **[FpPipelinedNewtonReciprocal](./FpPipelinedNewtonReciprocal.md)** — A pipelined Newton-Raphson reciprocal with a fixed-depth pipeline.
