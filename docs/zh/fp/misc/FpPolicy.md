# FpPolicy

浮点单元的编译期数值策略。

舍入模式是每个单元上的运行时端口（`rm`），因此不属于策略的一部分。策略只携带会改变数据通路结构的选项。

::: info 来源
`src/main/scala/fp/FpPolicy.scala`
:::

## 构造

| 名称 | 类型 | 默认值 | 说明 |
| --- | --- | --- | --- |
| `ftz` | `Boolean` | `false` | 将非规格化结果刷零 |
| `daz` | `Boolean` | `false` | 将非规格化操作数视为零 |

每个 `fp` 模块都接受一个 `policy` 参数，两个选项均默认由 `FpPolicy()` 提供。
