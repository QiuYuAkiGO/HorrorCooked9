# 配置索引

本文件用于集中说明工程内配置入口、作用范围与生效方式。更细的玩法与类映射见 [developer-gameplay.md](developer-gameplay.md)。

## 1. Forge 服务端配置

生成位置随 Forge 版本而定（通常为 `config/horrorcooked9-server.toml` 或等价路径）。

| 配置项 | 代码定义 | 作用 |
| --- | --- | --- |
| `datapackUpload.enabled` | `ModServerConfig.ENABLE_DATAPACK_UPLOAD` | 是否允许 `/datapack upload` 与 `/datapack delete`（关闭后两项均不可用） |
| `datapackUpload.maxUploadSizeMb` | `ModServerConfig.MAX_UPLOAD_SIZE_MB` | 上传 zip/目录流的最大体积（MB） |

## 2. 数据包 Gameplay JSON（当前基线）

路径均相对于 `src/main/resources/data/horrorcooked9/gameplay/`（或同名数据包覆盖路径）。

| 文件 | 用途 | 当前状态 |
| --- | --- | --- |
| `stir_tool_balance.json` | 沙拉搅拌小游戏与工具平衡 | 已存在 |
| `sharpening_stones.json` | 磨刀石与菜刀修理规则 | 已存在 |
| `item_foods.json` | 物品饱食/饱和/食用次数/事件参数（含腹泻事件全局参数） | 已落地 |
| `juicing.json` | 榨汁机容量、水果条目、副产物等 | 暂未落地（文档预留） |
| `butchery.json` | 屠宰武器、实体→尸体、尸体收割掉落表 | 暂未落地（文档预留） |
| `clear_water_boiling.json` | 过滤水在各类加热容器中的煮沸规则 | 暂未落地（文档预留） |

### `item_foods.json` 字段说明（新增）

路径：`data/horrorcooked9/gameplay/item_foods.json`

- `items.<item_id>.nutrition`：目标饱食度（0-20）。
- `items.<item_id>.saturation_mod`：目标饱和系数（0.0-2.0）。
- `items.<item_id>.uses`：多次食用物品总可食用次数（当前接入 `far_realm_gem_bowl`）。
- `items.<item_id>.bar_color`：多次食用条颜色，支持十六进制字符串（如 `#4CD3FF`）或整数。
- `items.<item_id>.inventory_consume_effect`：背包内触发型效果参数（当前接入 `parasitic_bean_sprouts`）。
- `diarrhea_events`：腹泻事件触发间隔、概率、减速时长、掉落位移/速度参数（当前接入 `DiarrheaEffectEvents`）。

### 生效与降级策略（新增）

- 服务器资源重载会刷新 `item_foods/stir_tool_balance/sharpening_stones` 的内存快照。
- `/datapack upload` 完成后会自动触发一次服务端重载流程。
- JSON 字段缺失或非法时会按内置默认值降级，并输出告警日志（包含键名或条目 ID）。

## 3. 其他运行期数据

| 配置项 | 文件位置 | 作用域 | 生效方式 |
| --- | --- | --- | --- |
| 物品 Tag（砧板可放置、搅拌工具、屠宰/收割等） | `data/horrorcooked9/tags/items/*.json` | 数据包/配方与判定 | 数据包加载时生效 |
| 流体 Tag | `data/horrorcooked9/tags/fluids/*.json` | 过滤与煮沸判定 | 数据包加载时生效 |
| 沙拉、切割、榨汁等配方 | `data/horrorcooked9/recipes/**/*.json` | 数据包 | 数据包加载时生效 |
| Patchouli 书与条目 | `data/.../patchouli_books/` 与 `assets/.../patchouli_books/` | 客户端展示 | 资源重载或重启后生效 |

## 4. 构建与元数据配置

| 配置项 | 文件位置 | 作用域 | 生效方式 |
| --- | --- | --- | --- |
| 版本、构建参数 | `gradle.properties` | 构建系统 | Gradle 构建时读取 |
| Mod 元信息 | `src/main/resources/META-INF/mods.toml` | 运行时元数据 | 打包后由 Forge 读取 |

## 5. 使用建议

- 修改配置前，先确认其属于 Forge 配置还是数据包资源，避免改错入口。
- 对平衡相关项优先放在 `data/horrorcooked9/gameplay/` 或 `recipes/`，对安全与上传限制优先放在服务端 config。
- 变更后至少执行一次本地编译与资源校验，确保命名与路径未断链。

## 6. 相关文档

| 主题 | 文档 |
| --- | --- |
| 玩家向玩法总览 | [player-guide.md](player-guide.md) |
| 沙拉成品食用后返还盛装容器（不返还搅拌工具） | [salad-eat-return.md](salad-eat-return.md) |
| 身份分配命令 | [identity_usage_manual.md](identity_usage_manual.md) |
| 文档目录索引 | [README.md](README.md) |
