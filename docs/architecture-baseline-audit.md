# 架构基线审计（代码 / 文档 / 资源）

本清单用于落实 `baseline-audit`：确认当前跨层依赖、目录异味与文档-资源漂移，并给出可执行的 Phase 1 基线。

## 1) 代码层跨层依赖审计

### 已确认且已处理（本次改动）

| 位置 | 问题 | 处理结果 |
| --- | --- | --- |
| `blocks/custom/ChoppingBoardBlock` | `common` 逻辑直接调用 `client.ClientHelper` | 改为调用 `common.ClientRuntimeBridge` |
| `blocks/custom/SaladBowlBlock` | `common` 逻辑直接调用 `client.ClientHelper` | 改为调用 `common.ClientRuntimeBridge` |
| `network/datapack/OpenDataPackPickerPacket` | `network` 直接引用 `client.DataPackUploadClient` | 改为调用 `common.ClientRuntimeBridge` |

### 当前仍存在的结构异味（待 Phase 2 继续处理）

| 位置 | 异味描述 | 风险 |
| --- | --- | --- |
| `HorrorCooked9` 顶层类 | 顶层类混合 common/client 关注点，且包含大量客户端 import | 模组主入口职责过重，后续改动易相互影响 |
| `register/*` | `Mod*` 注册入口集中在单层包下 | 存在 “God package” 趋势，跨功能修改点集中 |
| `gameplay` + `blocks` + `network` | feature 逻辑分散在技术层目录 | 难以按 feature 做整体演进与测试 |

## 2) 文档层审计

### 已有文档

- `docs/README.md`
- `docs/player-guide.md`
- `docs/developer-gameplay.md`
- `docs/config-index.md`
- `docs/salad-eat-return.md`
- `docs/identity_usage_manual.md`
- `docs/naming-conventions.md`

### 本次确认的文档异味

| 类别 | 现象 | 影响 |
| --- | --- | --- |
| 入口缺口 | 缺少运维、发布、自动化说明入口 | 新成员无法快速建立运维/发布心智 |
| 资源漂移 | `config-index` 与 `developer-gameplay` 声明的部分 gameplay JSON 不存在 | 文档可信度下降，排障成本增加 |
| 结构平铺 | 文档均位于 `docs/` 根目录，缺少阶段化信息层次 | 后续扩写时可读性下降 |

## 3) 资源层审计

### 已确认资源异常

| 路径 | 现象 | 建议 |
| --- | --- | --- |
| `assets/horrorcooked9/Untitled` | 无扩展名文件，语义不明确 | 登记来源并决定保留/清理 |
| `data/horrorcooked9/gameplay/` | 当前仅有 `stir_tool_balance.json`、`sharpening_stones.json` | 建立 manifest，避免文档先行漂移 |

### 与文档声明不一致（当前缺失）

- `juicing.json`
- `butchery.json`
- `item_foods.json`
- `clear_water_boiling.json`

## 4) 基线结论

- Phase 1 已完成首批跨层依赖“硬隔离”落地（通过 `ClientRuntimeBridge`）。
- 文档侧需要最小补齐：运维、发布、自动化三类入口。
- 下一步建议将文档索引与资源清单纳入脚本校验（manifest + CI），防止继续漂移。
