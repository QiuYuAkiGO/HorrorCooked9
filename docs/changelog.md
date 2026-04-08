# HorrorCooked9 更新日志（开发者向）

本文件记录模组从项目创建至今的版本变更，按 `mod_version` 递增维护。

## 维护约定（供后续累计更新）

- 新增版本时，在文档顶部插入新小节（最新版本在前）。
- 小节标题格式：`## [版本号] - YYYY-MM-DD`
- 每个版本建议包含：`新增`、`变更`、`修复`、`开发者提示`（无内容可省略）。
- 版本号以 `gradle.properties` 中的 `mod_version` 为准。
- 如存在预发布后缀（如 `-beta`、`a`），按项目实际发布语义保留。

---

## [0.1.3c] - 2026-04-08

项目结构与规范化改进，无玩法功能变更。

### 变更

- **跨层边界规则统一**：对齐 `project-conventions.mdc` 与 `phase1-boundary-guard.md` 的客户端 import 规则，明确 `Dist.CLIENT` 场景例外，消除文档间矛盾。
- **客户端代码归位**：将 `ChopMinigameScreen`、`StirMinigameScreen` 从 `gameplay/` 迁入 `client/screen/`；将 `ChoppingBoardRenderer`、`SaladBowlRenderer`、`CaptainHatRenderer` 从 `blocks/renderer/`、`armor/renderer/` 迁入 `client/renderer/`，统一"凡依赖 client API 均归入 client 树"的分层原则。
- **命名规范完善**：注册入口命名从"二选一"改为按层定义（`register/` 用 `Mod*`，`network/` 用 `*Registrar`）。
- **架构审计更新**：修正文档异味表中已过时的"入口缺口"结论，补充结构异味的当前处理状态，标记 `Untitled` 资源为已清理。

### 新增

- **PR 质量门 CI**：新增 `.github/workflows/pr-quality-gate.yml`，在 PR 提交时自动运行构建、跨层 import 检查与 gameplay 文件一致性校验（当前为 warning 级别）。
- **测试策略文档**：`docs/testing.md`，定义最低测试要求与逐步引入计划。
- **ADR 机制**：`docs/adr/` 目录，记录三项关键架构决策（ClientRuntimeBridge、网络域拆分、客户端代码归位）。

### 修复

- 删除语义不明的 `assets/horrorcooked9/Untitled` 文件。
- 删除迁移后的空目录 `blocks/renderer/`、`armor/renderer/`。

### 开发者提示

- 新增客户端 Screen 或 Renderer 时，请直接创建在 `client/screen/` 或 `client/renderer/` 下。
- PR 提交后 CI 会自动检查跨层 import 与 gameplay 文件一致性，当前为 warning 级别。

## [0.1.3b] - 2026-04-08

自 `bde13f8` 起至当前分支的累计说明如下（含玩法、版本号与文档）。

### 新增

- **Shelter9 联动**：新增游戏规则 `enableShelter9Support`（默认关闭），并在 `ModServerConfig` 中增加 `shelter9.Shelter9Support` 作为配置键缺失时的默认值；`ModGameRules.bootstrap()` 在初始化阶段完成规则注册。启用后，`DiarrheaEffectEvents` 会根据玩家是否持有腹泻效果，同步实体标签 `shit`，供与 Shelter9 侧逻辑衔接。
- **物品「金屎」**（`GoldenShitItem`）：食用后在服务端延长或施加生命恢复 II 与伤害吸收；物品附魔闪光、可始终食用且为快速食物；已注册创造模式标签页、物品模型数据生成与中英本地化。
- **文档**：仓库根目录 `README.md`（简介、环境与构建、`docs/` 入口）；`docs/developer-onboarding.md`（Java 包职责、资源与数据包路径、跨层边界与模组主类速查）。

### 变更

- **版本号**：`gradle.properties` 中 `mod_version` 自 `0.1.3` 经 `0.1.3a` 调整为 `0.1.3b`（与发布标签一致）。
- `docs/README.md`：明确玩家指南与实现规格的分工；补充 AI/Cursor 推荐阅读路径；文档索引增加 `changelog.md` 与 `developer-onboarding.md`。
- `docs/developer-gameplay.md`：`item_foods.json` 的加载类与「当前状态」改为已落地（`FoodRuntimeConfigs`）；物品/流体标签说明与仓库现状一致。
- `docs/config-index.md`：物品 Tag 描述（盛装容器、磨刀石等）与文档互链更新；补充指向玩法文档与 onboarding 的链接。
- `docs/architecture-baseline-audit.md`：`gameplay` 目录审计更新为已包含 `item_foods.json`，并注明由 `FoodRuntimeConfigs` 加载。
- `docs/automation-overview.md`：指向发布流程；标明 Phase 1 自动化建议为规划中、尚未落地。
- `docs/operations-runbook.md`：运维入口增加指向 `config-index.md` 的配置字段说明。
- `docs/release-process.md`：与自动化说明互链；发布前检查项增加核对 `changelog.md` 与 `mod_version` 一致。

### 提交记录（`bde13f8` -> `HEAD`）

- `bde13f8` feat: add Shelter9 support via gamerule and server config
- `416a9ac` feat: add Golden Shit item with custom effects and localization
- `26e3404` chore: bump mod version to 0.1.3b in gradle properties
- `d8f70a8` docs: add root README and developer onboarding, sync gameplay docs

### 开发者提示

- Shelter9 相关行为以游戏规则为准，服务端配置仅作默认值；扩展腹泻/标签联动时注意仅在服务端逻辑中修改实体标签。
- 改代码或扩展数据驱动字段时，优先以 `developer-gameplay.md` 与 `config-index.md` 为准，并用 `developer-onboarding.md` 定位包与资源路径。

## [0.1.3] - 2026-04-04

### 新增

- 新增水晶番茄加工线：糖渍水晶番茄切片、烤水晶番茄、水晶番茄酱，以及干豆芽；接入 `item_foods.json`、物品注册与自定义物品类。
- 新增异界宝石碗（Far Realm Gem Bowl）及相关物品模型、纹理与运行期食物参数（饱食度、多次食用与条颜色等）。
- 新增菠萝炒饭基底与油炸基底、宁静基底与油炸基底、羽衣甘蓝泥、烤菠萝块；新增沙拉碗配方数据，支持从油炸基底搅拌得到菠萝炒饭与宁静料理。
- 新增 `pineapple_power` I 级效果图标纹理；补充数据生成（物品模型与中英本地化）与创造模式标签页条目。

### 变更

- 模组版本号：`0.1.2` -> `0.1.3`。
- 沙拉碗：潜行（Shift）对方块使用可一次性拾取成品（按剩余份数掉落）或倒出未完成原料并清空方块实体，向玩家返还空沙拉碗（背包满则掉落）。
- 切菜小游戏结算：主手为菜刀且砧板原料为水晶番茄时，一次性耗尽菜刀剩余耐久（否则仍为每次 -1）；仅对可损坏菜刀生效。

### 提交记录（`0.1.2` -> `0.1.3`）

- `38494c2` feat: crystal tomato processing, Far Realm gem bowl, fried bases & salad recipes (v0.1.3)

### 开发者提示

- 本版本以新食材链与沙拉碗配方扩展为主，食物数值可在 `item_foods.json` 中继续数据驱动调整。

## [0.1.2] - 2026-04-01

### 新增

- 新增 `item_foods.json` 与 `FoodRuntimeConfigs`，将食物饱食度、饱和系数、多次食用次数、显示条颜色、背包内触发效果与腹泻事件参数统一改为数据驱动。
- 新增 `FoodConsumeRuntimeEvents` 与 `GameplayDataReloadEvents`，在服务端食用完成后应用运行期数值，并在资源重载时刷新 food/stir/sharpen 三类玩法配置。
- 新增 `ClientRuntimeBridge`（含客户端实现），将公共侧与客户端专有 UI 打开逻辑解耦，统一转发切菜/搅拌小游戏与数据包选择器入口。
- 新增开发文档体系（`README`、玩家指南、开发玩法文档、运维与发布流程、自动化与阶段审计文档），补齐玩法与工程基线说明。
- 沙拉碗搅拌改为分段推进：配方支持 `stir_checkpoints`，方块实体持久化已完成搅拌阶段（`CompletedStirPhases`），在需搅拌的节点锁定继续投料；配方解析支持精确匹配与前缀追踪，搅拌结果网络包按阶段结算，避免多步流程错乱。
- 新增多条料理与中间物：蕨芽碗、调色板、宁静基底、宁静、羽衣甘蓝泥、烤菠萝块、菠萝炒饭基底、菠萝炒饭、宁静拌饭、双尸寄生沙拉等；移除 `ocean_salad` 并配套更新模型、配方与本地化。
- 新增效果「菠萝力量 I」：提升最大生命值，受直接攻击时对来源造成反击伤害；菠萝炒饭会授予该效果。

### 变更

- `ModItems` 中可食用物品注册改为优先读取运行期 JSON 配置，保留默认值兜底，便于后续通过数据包进行平衡调优。
- `FarRealmGemBowlItem` 接入运行期配置：可食用次数与条颜色可调。
- `ParasiticBeanSproutsItem`：接入运行期负面效果参数；玩法上由「按堆叠一次性结算」改为「主/副手持续持有时周期触发」（周期性魔法自伤并刷新腹泻等），与数据驱动字段共同约束。
- `DiarrheaEffectEvents` 的周期、概率与掉落运动参数改为读取运行期 profile，避免硬编码常量。
- `StirToolBalanceConfig` 与 `SharpeningStoneConfig` 增加运行期快照与 classpath 缓存回退机制，支持更稳定的数据包热替换流程。
- `SaladBowlBlock` / `SaladBowlItem` / `SaladBowlRecipe` / `StirResultPacket` 联动调整，以支持分段搅拌、待搅拌锁与确定性配方追踪。
- `/datapack upload` 完成后会自动触发服务端资源重载，并向执行者回传成功/失败状态消息。
- 更新 `config-index.md`，补充 `item_foods.json` 字段说明、生效与降级策略，以及相关文档索引。
- 更新创造模式页签、物品/效果注册与中英文本地化，同步新料理体系。

### 提交记录（`0.1.1` -> `0.1.2`）

- `1819721` 新增：数据驱动玩法运行期配置管线与文档基线
- `f3d6bc6` 新增：沙拉碗分段搅拌推进与菠萝主题料理线

### 开发者提示

- 食物与部分玩法参数已外置化，扩展新字段时建议保持 JSON 兼容并沿用同一资源重载入口。
- 沙拉碗多步配方依赖 `stir_checkpoints` 与阶段状态，修改配方或网络同步路径时需一并校验「精确/前缀」追踪与阶段持久化。

## [0.1.1] - 2026-03-30

### 变更

- 升级模组版本号：`0.1.0 -> 0.1.1`。

### 提交记录（`0.1.0` -> `0.1.1`）

- `bf33a1b` 修复（CI）：使 `gradlew` 在 Linux Runner 上可执行
- `9ae1ff4` 新增：加入磨刀石机制、新沙拉物品、指南书与配方改进
- `c9239e1` 重构：通过渲染时驱动更新优化搅拌与切菜小游戏
- `70e7fe0` 新增：加入新效果、新物品与配方，并细化机制表现
- `ab8edb6` 杂项：在 `gradle.properties` 中将模组版本提升到 `0.1.1`

### 开发者提示

- 本次为版本号推进，便于后续继续累计功能/修复变更。

## [0.1.0] - 2026-03-27

### 新增

- 引入切菜小游戏机制，支持难度与产出流程。
- 增加多种食材/中间物品与对应资源（纹理、本地化、配方数据）。
- 增加沙拉碗渲染相关能力，支持成品与配料展示状态。

### 变更

- 版本号由 `0.0.5-beta` 升级为 `0.1.0`，进入 0.1 主线。

### 提交记录（`0.0.5-beta` -> `0.1.0`）

- `83e4a37` refactor: network 模块重构并统一本地化命名
- `2f7ef4d` ci: 自动化 PR 合并后发布流程
- `db761c5` 新增：加入 datapack 移除命令
- `791b93c` 重构：迁移 `identity_sample.txt` 并补充详细身份使用手册
- `7ba4bf7` 重构：迁移 `identity_sample.txt` 并补充详细身份使用手册
- `9ed051f` 新增：引入切菜小游戏、新物品与沙拉碗渲染

### 开发者提示

- 此版本是玩法规模扩展节点，建议以其为基线评估后续兼容与存档行为。

## [0.0.5-beta] - 2026-03-27

### 新增

- 新增沙拉碗方块/方块实体与搅拌玩法流程。
- 新增搅拌结果网络同步与数据驱动沙拉配方管线。
- 新增工具平衡配置能力，完善相关内容注册与资源接入。

### 变更

- 重构切菜玩法代码分层，迁移至独立包结构。
- 版本号由 `0.0.4a` 升级为 `0.0.5-beta`。

### 提交记录（`0.0.4a` -> `0.0.5-beta`）

- `5d048f2` 新增：加入沙拉碗搅拌玩法与配方处理管线

### 开发者提示

- 属于预发布版本（`beta`），API 与数据格式仍可能调整。

## [0.0.4a] - 2026-03-26

### 变更

- 调整/整理身份系统示例资源位置并补充使用手册文档。
- 刷新部分物品纹理资源。
- 版本号由 `0.0.4` 升级为 `0.0.4a`。

### 提交记录（`0.0.4` -> `0.0.4a`）

- `8a01f9f` 重构：迁移 `identity_sample.txt` 并新增详细身份使用手册

### 开发者提示

- `a` 后缀表示同主版本下的补充迭代，适合小步快跑修正文档与资源组织。

## [0.0.4] - 2026-03-26

### 新增

- 新增 `identity give` 指令，支持基于 JSON 配置进行身份分配（权重/数量约束）。

### 变更

- 指令已接入命令注册流程。
- 版本号由 `0.0.3` 升级为 `0.0.4`。

### 提交记录（`0.0.3` -> `0.0.4`）

- `2822536` 新增：加入 `identity give` 指令，支持基于 JSON 配置进行身份分配

### 开发者提示

- 身份分配逻辑开始数据驱动化，建议后续保持配置字段兼容策略。

## [0.0.3] - 2026-03-26

### 修复

- 修复命令可用性问题，使相关命令可正常调用。

### 变更

- 版本号由 `0.0.2` 升级为 `0.0.3`。

### 提交记录（`0.0.2` -> `0.0.3`）

- `d1eb751` 新增：加入开发用 datapack 上传流程，并重构网络注册
- `2730e33` 修复：使命令可用，并将版本提升到 `0.0.3`

## [0.0.2] - 2026-03-24

### 变更

- 升级模组版本号：`0.0.1 -> 0.0.2`。

### 提交记录（`0.0.1` -> `0.0.2`）

- `a7d6235` feat: 新增船长帽装备、灵感效果体系与相关资源/命令接入
- `4922229` 新增：加入船长帽装备与灵感效果系统
- `37dbdc5` 合并：合并远程跟踪分支 `origin/master`
- `6927c95` 新增：加入支持灵活区间解析的随机值命令
- `adf1430` 杂项：将模组版本提升到 `0.0.2`

### 开发者提示

- 该版本主要用于早期迭代节奏建立。

## [0.0.1] - 2026-03-22

### 新增

- 项目初始化提交（Initial commit）。
- 建立 Forge 1.20.1 开发基线与基础构建配置。

### 提交记录（项目创建）

- `f5bf008` 初始提交

### 开发者提示

- 作为项目起始版本，后续版本沿用 `gradle.properties` 的 `mod_version` 递增。

