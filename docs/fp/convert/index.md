# Fp · Convert

2 modules.

## Comparison

Representative condition per module · `FPGA` = Vivado `xc7a200t`, `55 nm` = icsprout55.

| Module | Condition | Max · FPGA | Area · FPGA | Max · 55 nm | Area · 55 nm |
| --- | --- | --- | --- | --- | --- |
| [FpGenericConvert](./FpGenericConvert.md) | `fp32->fp64` | 19.632 ns | 707 LUTs + 0 FF | 3.5764 ns | 565.32 µm² |
| [FpPipelinedConvert](./FpPipelinedConvert.md) | `fp32->fp64-2cyc` | 19.132 ns | 639 LUTs + 68 FF | 4.1978 ns | 959.00 µm² |

## Modules

- **[FpGenericConvert](./FpGenericConvert.md)** — A floating-point format converter.
- **[FpPipelinedConvert](./FpPipelinedConvert.md)** — A pipelined floating-point format converter.
