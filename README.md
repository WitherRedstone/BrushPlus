# 刷子增强（Brush Plus）功能分类文档

[English](#english) | [中文](#中文)

---

# **English**

## Introduction

Add more functionality to Minecraft's brush!

## Features

Brush Plus expands the uses of the vanilla brush by introducing **Block Transmutation** and **Entity Harvesting** systems:
- Hold right-click on a block with a brush. After some time, the block will transform into another block and may drop items.
- Hold right-click on an entity with a brush. After some time, the entity will drop items.

### New Brushes

- Iron Brush, Gold Brush, Diamond Brush, Netherite Brush

### Block Transmutation Recipe

A block transmutation recipe defines what happens when you brush a block:

- **ingredient** — The matching block ingredient (supports tags)
- **result_blocks** — A list of result blocks after transmutation, each with a chance
- **drops** — A list of item drops, each with a chance
- **total_ticks** — The total number of ticks to hold the brush

If `result_blocks` is empty, the block stays unchanged (items only drop).

### Entity Harvesting Recipe

An entity harvesting recipe defines what happens when you brush an entity:

- **entity** — The matching entity type
- **drops** — A list of item drops, each with a chance
- **total_ticks** — The total number of ticks to hold the brush

Brushing an entity will not remove it, only drop items.

---

## KubeJS Integration

Brush Plus provides KubeJS recipe schema support, allowing you to define custom block transmutation and entity harvesting recipes in KubeJS scripts.

### Recipe Types

- `brush_plus:block_transmutation` — Block Transmutation
- `brush_plus:entity_harvesting` — Entity Harvesting

### Block Transmutation

#### Constructor Parameters

```
event.recipes.brush_plus.block_transmutation(ingredient, result_blocks, drops, total_ticks)
```

- `ingredient` — Ingredient, **required**, the matching block ingredient
- `result_blocks` — Result blocks, optional, default `[]`. Multiple formats supported:
  - String: `'minecraft:cobblestone'` (single block, chance defaults to 1.0)
  - Object array: `[{ block: 'minecraft:iron_block', chance: 0.25 }]`
  - Empty array: `[]` (no block transmutation)
- `drops` — Item drops, optional, default `[]`. Multiple formats supported:
  - String: `'minecraft:netherite_ingot'` (single item, chance defaults to 1.0)
  - Object array: `[{ item: Item.of('minecraft:coal', 2), chance: 1.0 }]`
  - Empty array: `[]` (no item drops)
- `total_ticks` — Integer, optional, default `80`, the total number of ticks to hold

#### Script Examples

```javascript
ServerEvents.recipes(event => {
    // Simplified: pass strings directly
    event.recipes.brush_plus.block_transmutation(
        Ingredient.of('#minecraft:logs'),
        'minecraft:cobblestone',
        'minecraft:netherite_ingot',
        40
    ).id('brush_plus:example_1')

    // Full: result_blocks and drops as object arrays
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

    // Mixed: result_blocks as string, drops as object array
    event.recipes.brush_plus.block_transmutation(
        Ingredient.of('minecraft:stone'),
        'minecraft:cobblestone',
        [
            { item: Item.of('minecraft:coal', 2), chance: 1.0 },
            { item: Item.of('minecraft:netherite_ingot', 1), chance: 0.2 }
        ],
        80
    ).id('brush_plus:example_3')

    // Mixed: result_blocks as object array, drops as string
    event.recipes.brush_plus.block_transmutation(
        Ingredient.of('minecraft:bone_block'),
        [
            { block: 'minecraft:cobblestone', chance: 0.01 }
        ],
        'minecraft:netherite_ingot',
        80
    ).id('brush_plus:example_4')

    // Block transmutation only, no item drops
    event.recipes.brush_plus.block_transmutation(
        Ingredient.of('minecraft:sand'),
        'minecraft:cobblestone',
        [],
        80
    ).id('brush_plus:example_5')

    // Item drops only, no block transmutation
    event.recipes.brush_plus.block_transmutation(
        Ingredient.of('minecraft:iron_block'),
        [],
        'minecraft:diamond',
        40
    ).id('brush_plus:example_6')

    // Multiple result block chances + multiple item drops
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

### Entity Harvesting

#### Constructor Parameters

```
event.recipes.brush_plus.entity_harvesting(entity, drops, total_ticks)
```

- `entity` — String, **required**, the matching entity type ID (e.g. `'minecraft:cow'`)
- `drops` — Item drops, optional, default `[]`. Multiple formats supported:
  - String: `'minecraft:leather'` (single item, chance defaults to 1.0)
  - Object array: `[{ item: Item.of('minecraft:leather', 2), chance: 1.0 }]`
  - Empty array: `[]` (no item drops)
- `total_ticks` — Integer, optional, default `80`, the total number of ticks to hold

#### Script Examples

```javascript
ServerEvents.recipes(event => {
    // Simplified: pass strings directly
    event.recipes.brush_plus.entity_harvesting(
        'minecraft:cow',
        'minecraft:leather',
        40
    ).id('brush_plus:cow_1')

    // Full: drops as object array
    event.recipes.brush_plus.entity_harvesting(
        'minecraft:sheep',
        [
            { item: Item.of('minecraft:white_wool', 1), chance: 1.0 },
            { item: Item.of('minecraft:white_wool', 1), chance: 0.5 }
        ],
        40
    ).id('brush_plus:sheep_1')

    // Multiple item drops
    event.recipes.brush_plus.entity_harvesting(
        'minecraft:chicken',
        [
            { item: Item.of('minecraft:feather', 1), chance: 1.0 },
            { item: Item.of('minecraft:egg', 1), chance: 0.3 }
        ],
        40
    ).id('brush_plus:chicken_1')

    // No item drops
    event.recipes.brush_plus.entity_harvesting(
        'minecraft:pig',
        [],
        40
    ).id('brush_plus:pig_1')
})
```

---

# **中文**

## 简介

为 Minecraft 的刷子添加更多功能！

## 功能介绍

Brush Plus 扩展了原版刷子的用途，引入了**方块转化**和**实体采集**系统：
- 用刷子持续刷一个方块，经过一定时间后，方块会转化为另一个方块，并可能掉落物品；
- 用刷子持续刷一个实体，经过一定时间后，实体会掉落物品。

### 新增刷子

- 铁刷子、金刷子、钴石刷子、下界合金刷子

### 方块转化配方

方块转化配方定义了用刷子刷某个方块后会发生什么：

- **ingredient** — 匹配的方块原料（支持标签）
- **result_blocks** — 转化后的结果方块列表，每个方块可设置概率
- **drops** — 掉落物列表，每个物品可设置掉落概率
- **total_ticks** — 需要持续刷的总刻数

如果 `result_blocks` 为空，则方块保持不变（仅掉落物品）。

### 实体采集配方

实体采集配方定义了用刷子刷某个实体后会发生什么：

- **entity** — 匹配的实体类型
- **drops** — 掉落物列表，每个物品可设置掉落概率
- **total_ticks** — 需要持续刷的总刻数

刷完实体后不会消除实体，只会掉落物品。

---

## KubeJS 联动

Brush Plus 提供了 KubeJS 配方 Schema 支持，你可以在 KubeJS 脚本中自定义方块转化配方和实体采集配方。

### 配方类型

- `brush_plus:block_transmutation` — 方块转化
- `brush_plus:entity_harvesting` — 实体采集

### 方块转化

#### 构造函数参数

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

#### 脚本示例

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

### 实体采集

#### 构造函数参数

```
event.recipes.brush_plus.entity_harvesting(entity, drops, total_ticks)
```

- `entity` — 字符串，**必需**，匹配的实体类型 ID（如 `'minecraft:cow'`）
- `drops` — 掉落物，可选，默认 `[]`。支持多种写法：
  - 字符串：`'minecraft:leather'`（单个物品，chance 默认 1.0）
  - 对象数组：`[{ item: Item.of('minecraft:leather', 2), chance: 1.0 }]`
  - 空数组：`[]`（不掉落物品）
- `total_ticks` — 整数，可选，默认 `80`，需要持续刷的总刻数

#### 脚本示例

```javascript
ServerEvents.recipes(event => {
    // 简化写法：直接传字符串
    event.recipes.brush_plus.entity_harvesting(
        'minecraft:cow',
        'minecraft:leather',
        40
    ).id('brush_plus:cow_1')

    // 完整写法：drops 用对象数组
    event.recipes.brush_plus.entity_harvesting(
        'minecraft:sheep',
        [
            { item: Item.of('minecraft:white_wool', 1), chance: 1.0 },
            { item: Item.of('minecraft:white_wool', 1), chance: 0.5 }
        ],
        40
    ).id('brush_plus:sheep_1')

    // 多个掉落物
    event.recipes.brush_plus.entity_harvesting(
        'minecraft:chicken',
        [
            { item: Item.of('minecraft:feather', 1), chance: 1.0 },
            { item: Item.of('minecraft:egg', 1), chance: 0.3 }
        ],
        40
    ).id('brush_plus:chicken_1')

    // 不掉落物品
    event.recipes.brush_plus.entity_harvesting(
        'minecraft:pig',
        [],
        40
    ).id('brush_plus:pig_1')
})
```