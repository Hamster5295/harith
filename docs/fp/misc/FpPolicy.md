# FpPolicy

The compile-time numeric policy of a floating-point unit.

The rounding mode is a runtime port (`rm`) on every unit, so it is not part of the policy. The policy only carries the options that change the datapath structure.

::: info Source
`src/main/scala/fp/FpPolicy.scala`
:::

## Construction

| Name | Type | Default | Description |
| --- | --- | --- | --- |
| `ftz` | `Boolean` | `false` | Flush a subnormal result to zero |
| `daz` | `Boolean` | `false` | Treat a subnormal operand as zero |

Every `fp` module takes a `policy` argument, so both options default to `FpPolicy()`.
