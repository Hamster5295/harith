# SIntFma

6 modules.

## Comparison

Representative condition per module.

| Module | Condition | Max · FPGA | Area · FPGA | Max · 55 nm | Area · 55 nm |
| --- | --- | --- | --- | --- | --- |
| [SIntMacroFma](./SIntMacroFma.md) | `32bit` | 8.869 ns | 111 LUTs + 0 FF | 4.3248 ns | 20,068.16 µm² |
| [SIntComposedFma](./SIntComposedFma.md) | `32bit` | 11.330 ns | 239 LUTs + 0 FF | 5.8455 ns | 18,356.80 µm² |
| [SIntBoothFma](./SIntBoothFma.md) | `32bit` | 10.316 ns | 3,311 LUTs + 0 FF | 5.1612 ns | 18,097.52 µm² |
| [SIntTreeFma](./SIntTreeFma.md) | `32bit` | 10.436 ns | 1,744 LUTs + 0 FF | 4.7786 ns | 11,305.84 µm² |
| [SIntPipelinedBoothFma](./SIntPipelinedBoothFma.md) | `32bit-2cyc` | 3.865 ns | 3,142 LUTs + 459 FF | 3.4471 ns | 18,142.60 µm² |
| [SIntPipelinedTreeFma](./SIntPipelinedTreeFma.md) | `32bit-2cyc` | 4.947 ns | 1,712 LUTs + 215 FF | 2.5513 ns | 12,076.68 µm² |

## Modules

- **[SIntMacroFma](./SIntMacroFma.md)** — The signed FMA implemented with the `*` and `+` operators.
- **[SIntComposedFma](./SIntComposedFma.md)** — A signed FMA composed from an unsigned magnitude multiplier and a signed add.
- **[SIntBoothFma](./SIntBoothFma.md)** — A signed fused multiply-adder using signed modified Booth partial products with the addend merged into the reduction.
- **[SIntTreeFma](./SIntTreeFma.md)** — A signed fused multiply-adder using Baugh-Wooley partial products with the addend merged into the reduction.
- **[SIntPipelinedBoothFma](./SIntPipelinedBoothFma.md)** — A pipelined signed fused multiply-adder using signed modified Booth partial products with the addend merged into the reduction.
- **[SIntPipelinedTreeFma](./SIntPipelinedTreeFma.md)** — A pipelined signed fused multiply-adder using Baugh-Wooley partial products with the addend merged into the reduction.
