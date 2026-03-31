# Phase 1 边界约束与最小文档补齐方案

本文件用于落实 `phase1-boundary-guard`：定义“低风险、可当周落地”的边界规则与文档最小闭环。

## 1. 边界约束（代码）

## 1.1 目标

- `common` / `network` 不直接依赖 `client` 具体实现。
- 客户端行为通过中间桥接层访问，服务端环境默认无副作用。

## 1.2 本次已落地方案

- 新增 `common.ClientRuntimeBridge`（默认 no-op）。
- 新增 `client.ClientRuntimeBridgeImpl`（仅客户端安装）。
- 在 `HorrorCooked9.ClientModEvents#onClientSetup` 注入 bridge 实现。
- 将以下调用改为 bridge：
  - `ChoppingBoardBlock` 打开切割 UI
  - `SaladBowlBlock` 打开搅拌 UI
  - `OpenDataPackPickerPacket` 打开上传选择器

## 1.3 规则定义（Phase 1）

- 允许：`client -> common/network/register`。
- 禁止：`common`、`network`、`blocks.custom` 直接 import `net.qiuyu.horrorcooked9.client.*`。
- 例外：`@Mod.EventBusSubscriber(value = Dist.CLIENT)` 下的客户端注册类可引用客户端实现。

## 2. 最小文档补齐（文档）

## 2.1 必备入口

- 运维：服务器配置、数据包上传开关、排障入口。
- 发布：版本号、构建、PR 合并发布流程。
- 自动化：当前 workflow、建议新增检查、执行责任人。

## 2.2 本次已新增

- `docs/operations-runbook.md`
- `docs/release-process.md`
- `docs/automation-overview.md`

并已在 `docs/README.md` 建立统一索引入口。

## 3. 执行检查清单

- [x] 首批跨层依赖已切断（3 处）
- [x] 客户端行为桥接层已注入
- [x] 运维/发布/自动化文档入口已补齐
- [ ] 追加 manifest + 脚本检查（Phase 2/3）
- [ ] 将 `register` 做子域拆分（Phase 2）
