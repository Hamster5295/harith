# Rounding Modes

Every floating-point unit takes a 3-bit RISC-V `rm` input and rounds the result with it. The encodings follow the RISC-V `frm` field.

::: info Source
`src/main/scala/fp/FpUtils.scala`
:::

## Encodings

| `rm` | Name | Behavior |
| --- | --- | --- |
| 0 | RNE | Round to nearest, ties to even |
| 1 | RTZ | Round toward zero (truncate) |
| 2 | RDN | Round down (toward negative infinity) |
| 3 | RUP | Round up (toward positive infinity) |
| 4 | RMM | Round to nearest, ties to maximum magnitude |
| 5, 6 | — | Reserved |
| 7 | DYN | Dynamic (read `fcsr`); not supported by these units |

## Notes

The units decode `0..4`; values `5..7` fall through to truncation. `rm` is a runtime port on every unit, so it is not part of [`FpPolicy`](/fp/misc/FpPolicy).
