# HorrorCooked9 文档索引

本目录包含面向**玩家**与**开发者/服主**的说明，按主题拆分；与身份分配、命名规范等专项文档并列。

> **提示**：实现功能或改代码时，以开发者与配置类文档为准；[player-guide.md](player-guide.md) 是面向玩家的说明文档，不应视为实现规格。

## 按读者分类

| 读者 | 建议阅读顺序 |
| --- | --- |
| 玩家 | [玩家指南](player-guide.md) → 需要时查阅 [沙拉食用返还](salad-eat-return.md) |
| 服主 / 联机管理员 | [玩家指南](player-guide.md) 中的「数据包与服务器」→ [配置索引](config-index.md) → [运维手册](operations-runbook.md) |
| 开发者 / 数据包作者 | [配置索引](config-index.md) → [开发者：玩法与数据驱动](developer-gameplay.md) → [命名规范](naming-conventions.md) |
| 发布维护者 | [发布流程](release-process.md) → [自动化说明](automation-overview.md) |
| 身份系统（命令与 JSON） | [identity_usage_manual.md](identity_usage_manual.md)（示例见 [identity_sample.txt](identity_sample.txt)） |
| AI / Cursor 助手 | 本文 → [developer-gameplay.md](developer-gameplay.md) → [naming-conventions.md](naming-conventions.md)；改代码前建议也读 [developer-onboarding.md](developer-onboarding.md) |

## 文档一览

| 文档 | 内容摘要 |
| --- | --- |
| [player-guide.md](player-guide.md) | 烹饪流程：沙拉碗、砧板与切割小游戏、磨刀石、榨汁与过滤水、屠宰与可配置食物、指南书、冲刺砧板等 |
| [config-index.md](config-index.md) | Forge 服务端配置、数据包资源路径、玩法 JSON 入口一览 |
| [developer-gameplay.md](developer-gameplay.md) | 各玩法对应的配置类、事件、命令与扩展方式 |
| [salad-eat-return.md](salad-eat-return.md) | 沙拉成品吃完后返还盛装容器的规则与实现位置 |
| [identity_usage_manual.md](identity_usage_manual.md) | `/identity give` 命令与 JSON 配置说明 |
| [naming-conventions.md](naming-conventions.md) | 工程内命名与目录约定 |
| [operations-runbook.md](operations-runbook.md) | 服务端配置、数据包上传与常见排障最小手册 |
| [release-process.md](release-process.md) | PR 合并后的构建发布流程与版本约束 |
| [automation-overview.md](automation-overview.md) | 当前自动化能力、缺口与 Phase 1 建议 |
| [architecture-baseline-audit.md](architecture-baseline-audit.md) | 当前跨层依赖、目录异味与资源漂移审计 |
| [hook-monster-design.md](hook-monster-design.md) | 钩爪怪物实体、配置、渲染、已知缺口与验证清单 |
| [changelog.md](changelog.md) | 版本变更记录 |
| [developer-onboarding.md](developer-onboarding.md) | Java 包与资源目录一页速查地图 |
| [phase1-boundary-guard.md](phase1-boundary-guard.md) | Phase 1 边界约束与文档补齐执行方案 |
| [testing.md](testing.md) | 测试策略：最低要求、推荐类型与目录约定 |
| [adr/](adr/) | 架构决策记录（ADR），按编号递增 |
