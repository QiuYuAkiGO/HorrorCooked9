# 自动化说明

本文用于说明当前自动化能力与质量门配置。发布流程详见 [release-process.md](release-process.md)。

## 1. 当前已存在自动化

| 名称 | 位置 | 作用 |
| --- | --- | --- |
| PR 合并自动构建并发布 | `.github/workflows/release-on-pr-merge.yml` | 合并后构建并上传 Release |
| PR 质量门 | `.github/workflows/pr-quality-gate.yml` | PR 提交时自动构建、跨层 import 检查、gameplay 文件一致性校验 |

## 2. PR 质量门说明（0.1.3c 新增）

`.github/workflows/pr-quality-gate.yml` 包含三个并行 job：

- **build**：`gradlew build`，确保编译与测试通过。
- **boundary-check**：扫描 `common/`、`network/gameplay/`、`network/datapack/`、`blocks/custom/` 是否存在对 `client.*` 的非法 import（当前为 warning，稳定后升级为 error）。
- **gameplay-consistency**：检查已落地的 gameplay JSON 文件是否存在于 `data/horrorcooked9/gameplay/`（当前为 warning）。

## 3. 后续计划

- 稳定运行后将 boundary-check 与 gameplay-consistency 从 warning 升级为 error。
- 增加文档索引与资源清单的 manifest 校验。

## 4. 责任分工建议

- 功能开发者：提交前自检文档与资源路径。
- 评审者：关注跨层引用与文档漂移。
- 维护者：维护 workflow 与检查阈值。
