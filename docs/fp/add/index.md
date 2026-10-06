# Fp · Add

8 modules.

## Comparison

Representative condition per module · `FPGA` = Vivado `xc7a200t`, `55 nm` = icsprout55.

| Module | Condition | Max · FPGA | Area · FPGA | Max · 55 nm | Area · 55 nm |
| --- | --- | --- | --- | --- | --- |
| [FpCarryLookaheadAdd](./FpCarryLookaheadAdd.md) | `fp32` | 42.789 ns | 4,312 LUTs + 0 FF | 13.6956 ns | 6,375.88 µm² |
| [FpCarrySelectAdd](./FpCarrySelectAdd.md) | `fp32` | 45.991 ns | 4,226 LUTs + 0 FF | 14.6117 ns | 5,542.88 µm² |
| [FpMacroAdd](./FpMacroAdd.md) | `fp32` | 40.688 ns | 3,925 LUTs + 0 FF | 14.5881 ns | 5,899.04 µm² |
| [FpPipelinedMacroAdd](./FpPipelinedMacroAdd.md) | `fp32-2cyc` | 25.203 ns | 4,120 LUTs + 383 FF | 31.2895 ns | 15,043.56 µm² |
| [FpPipelinedPrefixAdd](./FpPipelinedPrefixAdd.md) | `fp32-2cyc` | 25.022 ns | 4,952 LUTs + 627 FF | 21.8746 ns | 17,788.96 µm² |
| [FpPipelinedRippleAdd](./FpPipelinedRippleAdd.md) | `fp32-2cyc` | 26.054 ns | 4,115 LUTs + 426 FF | 22.5578 ns | 15,058.68 µm² |
| [FpPrefixAdd](./FpPrefixAdd.md) | `fp32` | 42.413 ns | 4,837 LUTs + 0 FF | 13.1978 ns | 6,136.48 µm² |
| [FpRippleAdd](./FpRippleAdd.md) | `fp32` | 51.462 ns | 4,107 LUTs + 0 FF | 14.2377 ns | 5,554.36 µm² |

## Modules

- **[FpCarryLookaheadAdd](./FpCarryLookaheadAdd.md)** — A floating-point adder with a hierarchical carry lookahead alignment adder.
- **[FpCarrySelectAdd](./FpCarrySelectAdd.md)** — A floating-point adder with a block carry select alignment adder.
- **[FpMacroAdd](./FpMacroAdd.md)** — A floating-point adder with an inferred alignment adder.
- **[FpPipelinedMacroAdd](./FpPipelinedMacroAdd.md)** — A pipelined floating-point adder with an inferred alignment adder.
- **[FpPipelinedPrefixAdd](./FpPipelinedPrefixAdd.md)** — A pipelined floating-point adder with a parallel prefix alignment adder.
- **[FpPipelinedRippleAdd](./FpPipelinedRippleAdd.md)** — A pipelined floating-point adder with a pipelined ripple carry alignment adder.
- **[FpPrefixAdd](./FpPrefixAdd.md)** — A floating-point adder with a parallel prefix alignment adder, the fast option.
- **[FpRippleAdd](./FpRippleAdd.md)** — A floating-point adder with a ripple carry alignment adder, the cheapest option.
