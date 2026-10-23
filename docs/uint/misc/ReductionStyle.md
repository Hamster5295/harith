# ReductionStyle

The partial product reduction styles shared by [`UIntTreeMul`](/uint/mul/UIntTreeMul), [`UIntBoothMul`](/uint/mul/UIntBoothMul), [`UIntPipelinedTreeMul`](/uint/mul/UIntPipelinedTreeMul), [`UIntTreeFma`](/uint/fma/UIntTreeFma), [`UIntBoothFma`](/uint/fma/UIntBoothFma), [`UIntPipelinedTreeFma`](/uint/fma/UIntPipelinedTreeFma) and [`UIntPipelinedBoothFma`](/uint/fma/UIntPipelinedBoothFma).

The styles trade the number of carry save compressors against the reduction wiring, spanning the fastest to the smallest tree multiplier.

::: info Source
`src/main/scala/uint/mul/utils/ReductionStyle.scala`
:::

## Styles

| Name | Description |
| --- | --- |
| `Wallace` | Compress every column with at least three bits at each level. This is the fastest reduction and uses the most compressors. |
| `Dadda` | Compress only the columns above the Dadda target height at each level. This uses fewer compressors than `Wallace` at the cost of a slightly less regular network. |

## Usage

`reductionStyle` is a constructor argument of the tree/booth multiplier and FMA variants. The floating-point tree/booth variants fix it to `Dadda` internally.

## PPA

Measured with `UIntTreeMul(32, style, ...)`.

| Style | Condition | Max · FPGA | Area · FPGA | Max · 55 nm | Area · 55 nm |
| --- | --- | --- | --- | --- | --- |
| `Dadda` | `32bit-dadda` | 10.316 ns | 1,616 LUTs + 0 FF | 4.8125 ns | 10,564.68 µm² |
| `Wallace` | `32bit-wallace` | 10.274 ns | 1,568 LUTs + 0 FF | 4.7953 ns | 10,695.44 µm² |
