# UIntMul

6 modules.

## Comparison

Representative condition per module.

| Module | Condition | Max · FPGA | Area · FPGA | Max · 55 nm | Area · 55 nm |
| --- | --- | --- | --- | --- | --- |
| [UIntArrayMul](./UIntArrayMul.md) | `32bit` | 20.305 ns | 2,589 LUTs + 0 FF | 7.9127 ns | 16,088.52 µm² |
| [UIntBoothMul](./UIntBoothMul.md) | `32bit` | 10.234 ns | 2,269 LUTs + 0 FF | 5.3502 ns | 16,288.16 µm² |
| [UIntMacroMul](./UIntMacroMul.md) | `32bit` | 7.721 ns | 47 LUTs + 0 FF | 3.2678 ns | 16,313.36 µm² |
| [UIntPipelinedArrayMul](./UIntPipelinedArrayMul.md) | `32bit-2cyc` | 7.545 ns | 2,124 LUTs + 220 FF | 2.6341 ns | 18,446.40 µm² |
| [UIntPipelinedTreeMul](./UIntPipelinedTreeMul.md) | `32bit-2cyc` | 4.947 ns | 1,597 LUTs + 207 FF | 2.6226 ns | 11,401.88 µm² |
| [UIntTreeMul](./UIntTreeMul.md) | `32bit` | 10.316 ns | 1,616 LUTs + 0 FF | 4.8125 ns | 10,564.68 µm² |

## Modules

- **[UIntArrayMul](./UIntArrayMul.md)** — A structural carry save array multiplier.
- **[UIntBoothMul](./UIntBoothMul.md)** — A modified Booth radix-4 carry save tree multiplier.
- **[UIntMacroMul](./UIntMacroMul.md)** — The unsigned multiplier implemented with the `*` operator.
- **[UIntPipelinedArrayMul](./UIntPipelinedArrayMul.md)** — A pipelined carry save array multiplier.
- **[UIntPipelinedTreeMul](./UIntPipelinedTreeMul.md)** — A pipelined carry save tree multiplier using AND partial products.
- **[UIntTreeMul](./UIntTreeMul.md)** — A carry save tree multiplier using AND partial products.
