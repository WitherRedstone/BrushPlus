# Brush Plus

为 Minecraft 的刷子添加更多功能！

## 功能介绍

Brush Plus 扩展了原版刷子的用途，引入了**方块转化**系统：用刷子持续刷一个方块，经过一定时间后，方块会转化为另一个方块，并可能掉落物品。

### 新增刷子

- 铁刷子、金刷子、钴石刷子、下界合金刷子

### 方块转化配方

方块转化配方定义了用刷子刷某个方块后会发生什么：

- **ingredient** — 匹配的方块原料（支持标签）
- **result_blocks** — 转化后的结果方块列表，每个方块可设置概率
- **drops** — 掉落物列表，每个物品可设置掉落概率
- **total_ticks** — 需要持续刷的总刻数

如果 `result_blocks` 为空，则方块保持不变（仅掉落物品）。

---

## KubeJS 联动

Brush Plus 提供了 KubeJS 配方 Schema 支持，你可以在 KubeJS 脚本中自定义方块转化配方。

### 配方类型

`brush_plus:block_transmutation`

### 构造函数参数

```
event.recipes.brush_plus.block_transmutation(ingredient, result_blocks, drops, total_ticks)
```

- `ingredient` — Ingredient，**必需**，匹配的方块原料
- `result_blocks` — 结果方块，可选，默认 `[]`。支持多种写法：
  - 字符串：`'minecraft:cobblestone'`（单个方块，chance 默认 1.0）
  - 对象数组：`[{ block: 'minecraft:iron_block', chance: 0.25 }]`
  - 空数组：`[]`（不转化方块）
- `drops` — 掉落物，可选，默认 `[]`。支持多种写法：
  - 字符串：`'minecraft:netherite_ingot'`（单个物品，chance 默认 1.0）
  - 对象数组：`[{ item: Item.of('minecraft:coal', 2), chance: 1.0 }]`
  - 空数组：`[]`（不掉落物品）
- `total_ticks` — 整数，可选，默认 `80`，需要持续刷的总刻数

### 脚本示例

```javascript
ServerEvents.recipes(event => {
    // 简化写法：直接传字符串
    event.recipes.brush_plus.block_transmutation(
        Ingredient.of('#minecraft:logs'),
        'minecraft:cobblestone',
        'minecraft:netherite_ingot',
        40
    ).id('brush_plus:example_1')

    // 完整写法：result_blocks 和 drops 都用对象数组
    event.recipes.brush_plus.block_transmutation(
        Ingredient.of('minecraft:diamond_block'),
        [
            { block: 'minecraft:iron_block', chance: 0.25 }
        ],
        [
            { item: Item.of('minecraft:netherite_ingot', 2), chance: 1.0 }
        ],
        40
    ).id('brush_plus:example_2')

    // 混合写法：result_blocks 传字符串，drops 传对象数组
    event.recipes.brush_plus.block_transmutation(
        Ingredient.of('minecraft:stone'),
        'minecraft:cobblestone',
        [
            { item: Item.of('minecraft:coal', 2), chance: 1.0 },
            { item: Item.of('minecraft:netherite_ingot', 1), chance: 0.2 }
        ],
        80
    ).id('brush_plus:example_3')

    // 混合写法：result_blocks 传对象数组，drops 传字符串
    event.recipes.brush_plus.block_transmutation(
        Ingredient.of('minecraft:bone_block'),
        [
            { block: 'minecraft:cobblestone', chance: 0.01 }
        ],
        'minecraft:netherite_ingot',
        80
    ).id('brush_plus:example_4')

    // 仅转化方块，不掉落物品
    event.recipes.brush_plus.block_transmutation(
        Ingredient.of('minecraft:sand'),
        'minecraft:cobblestone',
        [],
        80
    ).id('brush_plus:example_5')

    // 仅掉落物品，不转化方块
    event.recipes.brush_plus.block_transmutation(
        Ingredient.of('minecraft:iron_block'),
        [],
        'minecraft:diamond',
        40
    ).id('brush_plus:example_6')

    // 多个结果方块概率 + 多个掉落物
    event.recipes.brush_plus.block_transmutation(
        Ingredient.of('minecraft:deepslate'),
        [
            { block: 'minecraft:cobblestone', chance: 0.05 },
            { block: 'minecraft:stone', chance: 0.01 }
        ],
        [
            { item: Item.of('minecraft:coal', 2), chance: 1.0 },
            { item: Item.of('minecraft:netherite_ingot', 1), chance: 0.2 }
        ],
        40
    ).id('brush_plus:example_7')
})
```

### JSON 配方格式

如果你更习惯用 JSON 数据包定义配方，格式如下：

```json
{
    "type": "brush_plus:block_transmutation",
    "ingredient": {
        "tag": "c:ores/coal"
    },
    "result_blocks": [
        { "block": "minecraft:cobblestone", "chance": 0.25 },
        { "block": "minecraft:deepslate", "chance": 0.75 }
    ],
    "drops": [
        { "item": { "id": "minecraft:coal", "count": 2 }, "chance": 1.0 },
        { "item": { "id": "minecraft:coal", "count": 1 }, "chance": 0.5 }
    ],
    "total_ticks": 40
}
```