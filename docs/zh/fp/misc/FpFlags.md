# FpFlags

IEEE-754 异常标志，与 RISC-V 的 `fflags` 字段一致。每个 `fp` 模块都通过 `fflags` 输出报告其异常。

这些标志在系统层面是粘滞的；一次运算只报告它自身引发的异常。

::: info 源代码
`src/main/scala/fp/FpFlags.scala`
:::

## 字段

| 名称 | 类型 | 说明 |
| --- | --- | --- |
| `nx` | `Bool` | 不精确结果 |
| `uf` | `Bool` | 下溢 |
| `of` | `Bool` | 上溢 |
| `dz` | `Bool` | 除零 |
| `nv` | `Bool` | 非法操作 |

## 成员

| 名称 | 类型 | 说明 |
| --- | --- | --- |
| `bits` | `UInt` | 按 RISC-V `fcsr` 布局 `{NV, DZ, OF, UF, NX}` 打包的标志 |

`FpFlags.apply(nx, uf, of, dz, nv)` 由各个标志构建标志 bundle。
