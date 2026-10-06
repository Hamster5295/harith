# Fp · Mul

5 modules.

## Comparison

Representative condition per module · `FPGA` = Vivado `xc7a200t`, `55 nm` = icsprout55.

| Module | Condition | Max · FPGA | Area · FPGA | Max · 55 nm | Area · 55 nm |
| --- | --- | --- | --- | --- | --- |
| [FpArrayMul](./FpArrayMul.md) | `fp32` | 33.146 ns | 2,341 LUTs + 0 FF | 12.6329 ns | 11,450.88 µm² |
| [FpBoothMul](./FpBoothMul.md) | `fp32` | 27.629 ns | 2,370 LUTs + 0 FF | 11.8575 ns | 12,906.60 µm² |
| [FpMacroMul](./FpMacroMul.md) | `fp32` | 24.908 ns | 984 LUTs + 0 FF | 9.4441 ns | 10,452.12 µm² |
| [FpPipelinedMul](./FpPipelinedMul.md) | `fp32-2cyc` | 22.210 ns | 1,889 LUTs + 203 FF | 8.0414 ns | 9,518.32 µm² |
| [FpTreeMul](./FpTreeMul.md) | `fp32` | 26.628 ns | 1,864 LUTs + 0 FF | 11.0121 ns | 8,316.00 µm² |

## Modules

- **[FpArrayMul](./FpArrayMul.md)** — A floating-point multiplier with a carry save array significand multiplier, the cheapest option.
- **[FpBoothMul](./FpBoothMul.md)** — A floating-point multiplier with a modified Booth radix-4 tree significand multiplier.
- **[FpMacroMul](./FpMacroMul.md)** — A floating-point multiplier with an inferred significand multiplier.
- **[FpPipelinedMul](./FpPipelinedMul.md)** — A pipelined floating-point multiplier.
- **[FpTreeMul](./FpTreeMul.md)** — A floating-point multiplier with an AND partial product carry save tree significand multiplier.
