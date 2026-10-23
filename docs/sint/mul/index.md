# SIntMul

7 modules.

## Comparison

Representative condition per module.

| Module | Condition | Max · FPGA | Area · FPGA | Max · 55 nm | Area · 55 nm |
| --- | --- | --- | --- | --- | --- |
| [SIntMacroMul](./SIntMacroMul.md) | `32bit` | 7.721 ns | 47 LUTs + 0 FF | 4.0572 ns | 18,430.16 µm² |
| [SIntMagnitudeMul](./SIntMagnitudeMul.md) | `32bit` | 11.896 ns | 300 LUTs + 0 FF | 5.0521 ns | 17,607.24 µm² |
| [SIntArrayMul](./SIntArrayMul.md) | `32bit` | 18.743 ns | 2,731 LUTs + 0 FF | 8.1454 ns | 16,828.56 µm² |
| [SIntBoothMul](./SIntBoothMul.md) | `32bit` | 10.235 ns | 3,030 LUTs + 0 FF | 5.0887 ns | 15,983.52 µm² |
| [SIntTreeMul](./SIntTreeMul.md) | `32bit` | 10.016 ns | 1,622 LUTs + 0 FF | 4.6843 ns | 10,562.44 µm² |
| [SIntPipelinedArrayMul](./SIntPipelinedArrayMul.md) | `32bit-2cyc` | 7.332 ns | 2,177 LUTs + 220 FF | 3.4569 ns | 19,392.24 µm² |
| [SIntPipelinedTreeMul](./SIntPipelinedTreeMul.md) | `32bit-2cyc` | 4.534 ns | 1,611 LUTs + 208 FF | 2.4505 ns | 11,618.60 µm² |

## Modules

- **[SIntMacroMul](./SIntMacroMul.md)** — The signed multiplier implemented with the `*` operator.
- **[SIntMagnitudeMul](./SIntMagnitudeMul.md)** — A signed multiplier built from an unsigned magnitude multiplier and sign correction.
- **[SIntArrayMul](./SIntArrayMul.md)** — A signed carry save array multiplier using Baugh-Wooley partial products.
- **[SIntBoothMul](./SIntBoothMul.md)** — A signed modified Booth radix-4 tree multiplier.
- **[SIntTreeMul](./SIntTreeMul.md)** — A signed carry save tree multiplier using Baugh-Wooley partial products.
- **[SIntPipelinedArrayMul](./SIntPipelinedArrayMul.md)** — A pipelined signed carry save array multiplier using Baugh-Wooley partial products.
- **[SIntPipelinedTreeMul](./SIntPipelinedTreeMul.md)** — A pipelined signed carry save tree multiplier using Baugh-Wooley partial products.
