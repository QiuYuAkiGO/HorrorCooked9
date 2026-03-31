# 开发者参考：玩法与数据驱动

本文面向维护本模组或编写数据包扩展的开发者，概括近期玩法对应的**配置路径**、**加载类**与**扩展点**。与命名、网络域划分等全局约定见 [naming-conventions.md](naming-conventions.md)。

## 1. 数据包Gameplay JSON（classpath 默认 + 可覆盖）

以下文件默认位于 `src/main/resources/data/horrorcooked9/gameplay/`，打包后由对应 `*Config` 类读取；数据包可通过同名路径覆盖。

| 文件 | 用途 | 主要加载类 | 当前状态 |
| --- | --- | --- | --- |
| `stir_tool_balance.json` | 沙拉搅拌工具平衡 | `StirToolBalanceConfig` | 已落地 |
| `sharpening_stones.json` | 磨刀石与菜刀交互 | `SharpeningStoneConfig` | 已落地 |
| `juicing.json` | 榨汁机默认参数与水果条目 | `JuicingConfig` | 规划中（未落地） |
| `butchery.json` | 屠宰武器、实体掉落、尸体收割 | `ButcheryConfig` | 规划中（未落地） |
| `item_foods.json` | 物品是否可食及营养、效果覆盖 | `ItemFoodConfig` | 规划中（未落地） |
| `clear_water_boiling.json` | 过滤水煮沸与炼药锅加热等规则 | `ClearWaterBoilingConfig` | 规划中（未落地） |

实现上多在 `HorrorCooked9` 模组初始化阶段注册到游戏内，具体静态字段见各类中 `CLASSPATH_CONFIG_PATH`。

## 2. 配方与标签

| 类型 | 路径模式 | 说明 |
| --- | --- | --- |
| 沙拉碗配方 | `data/horrorcooked9/recipes/salad_bowl/*.json` | 类型 `horrorcooked9:salad_bowl` |
| 切割小游戏 | `data/horrorcooked9/recipes/chopper_minigame/*.json` | 与 `ChopperRecipeMatcher` / `ChopperMinigameRecipe` 配合 |
| 物品标签 | `data/horrorcooked9/tags/items/*.json` | 如 `chopper_placeable`、`slaughter_weapons`、`harvest_tools`、水相关容器等 |
| 流体标签 | `data/horrorcooked9/tags/fluids/*.json` | 如 `clear_water`、`filtered_water` |

## 3. 方块实体与 UI 注册（当前基线）

| 模块 | 说明 |
| --- | --- |
| `ChoppingBoardBlock` / `ChoppingBoardBlockEntity` | 切割放置与小游戏入口 |
| `SaladBowlBlock` / `SaladBowlBlockEntity` | 沙拉配方序列、搅拌完成与出餐逻辑 |
| `StirMinigameScreen` / `ChopMinigameScreen` | 客户端小游戏界面 |

注：榨汁/过滤/屠宰等内容仍以规划为主，待对应代码与资源落地后再提升为“当前基线”。

## 4. 客户端与网络

| 主题 | 入口 |
| --- | --- |
| 切割/搅拌小游戏结果 | `network/gameplay` 下各 `*Packet`，由 `GameplayNetworkRegistrar` 注册 |
| 数据包上传 | `network/datapack/*`，命令见 `UploadDataPackCommand` |
| 客户端桥接边界 | `common/ClientRuntimeBridge` + `client/ClientRuntimeBridgeImpl`（Phase 1） |

## 5. 命令一览（Brigadier 注册）

| 命令节点 | 类 | 备注 |
| --- | --- | --- |
| `/datapack upload`、`/datapack delete` | `UploadDataPackCommand` | 依赖 `ModServerConfig` 与专用服检测 |
| `/identity give` | `IdentityCommand` | JSON 身份分配 |
| `/random value` | `RandomValueCommand` | 区间解析见源码 `parseRange` |

## 6. Patchouli 指南

- 书元数据：`data/horrorcooked9/patchouli_books/guide/book.json`
- 条目与分类：`assets/horrorcooked9/patchouli_books/guide/<语言>/` 下 `categories`、`entries`
- 构建依赖见 `build.gradle` 中 Patchouli 声明

## 7. 测试与 CI 提示

- Linux CI 需保证 `gradlew` 可执行（历史提交曾修复权限问题）。
- 修改 `gameplay/*.json` 后建议本地运行数据生成或至少加载一次世界，避免 JSON 键名与代码反序列化字段不一致。
