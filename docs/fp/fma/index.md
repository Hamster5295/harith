# FpFma

7 modules.

## Comparison

Representative condition per module.

| Module | Condition | Max · FPGA | Area · FPGA | Max · 55 nm | Area · 55 nm |
| --- | --- | --- | --- | --- | --- |
| [FpArrayFma](./FpArrayFma.md) | `fp32` | 57.147 ns | 6,559 LUTs + 0 FF | 34.1085 ns | 25,570.16 µm² |
| [FpBoothFma](./FpBoothFma.md) | `fp32` | 52.331 ns | 6,531 LUTs + 0 FF | 37.0841 ns | 30,194.92 µm² |
| [FpMacroFma](./FpMacroFma.md) | `fp32` | 49.853 ns | 5,107 LUTs + 0 FF | 36.9288 ns | 28,061.32 µm² |
| [FpPipelinedFma](./FpPipelinedFma.md) | `fp32-2cyc` | 46.992 ns | 6,038 LUTs + 327 FF | 21.9171 ns | 17,519.04 µm² |
| [FpPrefixFma](./FpPrefixFma.md) | `fp32` | 51.516 ns | 6,090 LUTs + 0 FF | 35.9554 ns | 24,540.60 µm² |
| [FpRippleFma](./FpRippleFma.md) | `fp32` | 63.167 ns | 5,256 LUTs + 0 FF | 41.1512 ns | 28,605.08 µm² |
| [FpTreeFma](./FpTreeFma.md) | `fp32` | 51.426 ns | 6,007 LUTs + 0 FF | 41.0896 ns | 26,301.24 µm² |

## Modules

- **[FpArrayFma](./FpArrayFma.md)** — A floating-point fused multiply-adder with an array significand multiplier.
- **[FpBoothFma](./FpBoothFma.md)** — A floating-point fused multiply-adder with a modified Booth radix-4 tree significand multiplier.
- **[FpMacroFma](./FpMacroFma.md)** — A floating-point fused multiply-adder with inferred significand and alignment datapaths.
- **[FpPipelinedFma](./FpPipelinedFma.md)** — A pipelined floating-point fused multiply-adder.
- **[FpPrefixFma](./FpPrefixFma.md)** — A floating-point fused multiply-adder with a parallel prefix alignment adder.
- **[FpRippleFma](./FpRippleFma.md)** — A floating-point fused multiply-adder with a ripple carry alignment adder.
- **[FpTreeFma](./FpTreeFma.md)** — A floating-point fused multiply-adder with an AND partial product tree significand multiplier.
