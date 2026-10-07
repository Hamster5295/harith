# FpFlags

The IEEE-754 exception flags, matching the RISC-V `fflags` field. Every `fp` module reports its exceptions through an `fflags` output.

The flags are sticky at the system level; an operation only reports the exceptions it raises.

::: info Source
`src/main/scala/fp/FpFlags.scala`
:::

## Fields

| Name | Type | Description |
| --- | --- | --- |
| `nx` | `Bool` | Inexact result |
| `uf` | `Bool` | Underflow |
| `of` | `Bool` | Overflow |
| `dz` | `Bool` | Divide by zero |
| `nv` | `Bool` | Invalid operation |

## Members

| Name | Type | Description |
| --- | --- | --- |
| `bits` | `UInt` | The flags packed in the RISC-V `fcsr` layout `{NV, DZ, OF, UF, NX}` |

`FpFlags.apply(nx, uf, of, dz, nv)` builds a flag bundle from the individual flags.
