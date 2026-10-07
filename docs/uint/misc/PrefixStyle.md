# PrefixStyle

The parallel prefix network styles shared by [[UIntPrefixAdd]], [[UIntPipelinedPrefixAdd]] and [[UIntCarryLookaheadAdd]].

The styles trade logic depth against area and wiring, spanning the high-performance to resource constrained range of prefix adders. `n` denotes the (power-of-two padded) width of the network.

::: info Source
`src/main/scala/uint/add/utils/PrefixStyle.scala`
:::

## Styles

| Name | Depth | Description |
| --- | --- | --- |
| `KoggeStone` | `log2(n)` | Minimum depth with the largest area and fanout. |
| `BrentKung` | `2 * log2(n) - 1` | Minimum area and fanout. |
| `Sklansky` | `log2(n)` | Minimum depth with lower area than `KoggeStone`, but high fanout. |
| `HanCarlson` | `log2(n) + 1` | A sparse Kogge-Stone hybrid that balances area and wiring. |

## Usage

`style` is a constructor argument of [[UIntPrefixAdd]] and [[UIntPipelinedPrefixAdd]]. [[UIntCarryLookaheadAdd]] fixes it internally to `Sklansky`, and the floating-point prefix variants use `KoggeStone`.
