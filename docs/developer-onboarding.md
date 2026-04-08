# 开发者速查地图

本页帮助你快速定位「改什么去哪找」。详细玩法配置与扩展点见 [developer-gameplay.md](developer-gameplay.md)，命名约定见 [naming-conventions.md](naming-conventions.md)。

## Java 包职责

根包：`net.qiuyu.horrorcooked9`

| 包 | 职责 |
| --- | --- |
| `register/` | Forge 注册入口（方块、物品、方块实体、配方类型、创造标签等 `Mod*` 类） |
| `gameplay/` | 玩法逻辑与数据驱动配置；按子域拆分：`chopping`、`food`、`salad`、`sharpen`、`stir` |
| `events/` | Forge 事件监听（食物消耗、数据包重载、食物加工台变换、菠萝力量等） |
| `blocks/custom/` | 自定义方块及方块实体（砧板、沙拉碗等） |
| `items/custom/` | 自定义物品（沙拉碗物品、菜刀、特殊食物等） |
| `commands/` | Brigadier 命令（`/identity`、`/randomvalue`、数据包上传） |
| `config/` | Forge 服务端配置（`ModServerConfig`） |
| `network/` | 网络通信，按域拆分 |
| `network/gameplay/` | 切割/搅拌小游戏结果包，由 `GameplayNetworkRegistrar` 注册 |
| `network/datapack/` | 数据包上传通信，由 `DatapackNetworkRegistrar` 注册 |
| `client/` | 客户端专有逻辑（`ClientHelper`、`DataPackUploadClient`、`ClientRuntimeBridgeImpl`） |
| `common/` | 客户端桥接接口（`ClientRuntimeBridge`），服务端不应依赖 `client/` |
| `armor/` | 盔甲渲染（船长帽等） |
| `effects/` | 药水效果（菠萝力量、船长鼓舞） |
| `datagen/` | 数据生成器（物品模型、多语言） |

## 资源与数据包

运行期资源根目录：`src/main/resources/`

| 路径 | 内容 |
| --- | --- |
| `data/horrorcooked9/gameplay/` | Gameplay JSON（搅拌平衡、磨刀石、食物配置等），由各 `*Config` 类在初始化时加载；数据包可覆盖 |
| `data/horrorcooked9/recipes/salad_bowl/` | 沙拉碗配方 JSON |
| `data/horrorcooked9/recipes/chopper_minigame/` | 切割小游戏配方 JSON |
| `data/horrorcooked9/tags/items/` | 物品标签（`chopper_placeable`、`salad_mixing_tools`、`salad_serving_containers`、`sharpening_stones`） |
| `data/horrorcooked9/patchouli_books/` | Patchouli 指南书定义 |

Gameplay JSON 与加载类的对应关系详见 [developer-gameplay.md § 1](developer-gameplay.md#1-数据包gameplay-jsonclasspath-默认--可覆盖)。

## 跨层边界

- `common/` 定义桥接接口，`client/` 提供实现——服务端代码不应 import `client.*`。
- 网络包按业务域拆目录，不混用 `gameplay` 与 `datapack`。
- 详见 [architecture-baseline-audit.md](architecture-baseline-audit.md) 中的跨层审计。

## 入口类

模组主类：`HorrorCooked9.java`（根包下），负责 Forge mod 生命周期注册。
