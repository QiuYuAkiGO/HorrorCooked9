# 发布流程（最小版）

本文是 Phase 1 发布入口，覆盖从版本号到自动发布的最小闭环。

## 1. 发布前准备

- 在 `gradle.properties` 更新 `mod_version`。
- 确认关键文档已同步：
  - `docs/config-index.md`
  - `docs/developer-gameplay.md`
  - `docs/README.md`

## 2. 本地验证

- 执行构建：`./gradlew clean build`
- 检查 `build/libs/` 产物是否正确。

## 3. 自动发布触发

- 工作流：`.github/workflows/release-on-pr-merge.yml`
- 触发条件：PR 被合并（`pull_request.closed` 且 `merged == true`）。
- 行为：
  - 读取 `gradle.properties` 的 `mod_version`
  - 构建 jar
  - 创建/更新 GitHub Release（tag `v<mod_version>`）

## 4. 回滚策略（最小建议）

- 若发布产物异常：
  - 先修复后重新提交新的 `mod_version`
  - 避免覆盖同一版本产物
- 若文档与资源不一致：
  - 先修复文档/资源，再发补丁版本
