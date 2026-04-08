# 自动化说明（最小版）

本文用于说明当前自动化能力与 Phase 1 的补充建议。发布流程详见 [release-process.md](release-process.md)。

## 1. 当前已存在自动化

| 名称 | 位置 | 作用 |
| --- | --- | --- |
| PR 合并自动构建并发布 | `.github/workflows/release-on-pr-merge.yml` | 合并后构建并上传 Release |

## 2. 当前缺口

- 缺少文档与资源一致性校验。
- 缺少跨层依赖约束检查（`common/network -> client`）。

## 3. Phase 1 建议（低风险，规划中 — 尚未落地）

- 在 CI 中新增“只读检查”步骤：
  - 检查 `docs/config-index.md` 声明的 gameplay JSON 是否存在。
  - 检查非客户端层是否直接 import `net.qiuyu.horrorcooked9.client.*`。
- 初期可先做告警（warning），稳定后再升级为失败（error）。

## 4. 责任分工建议

- 功能开发者：提交前自检文档与资源路径。
- 评审者：关注跨层引用与文档漂移。
- 维护者：维护 workflow 与检查阈值。
