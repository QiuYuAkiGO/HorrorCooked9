# ADR-0001: 通过 ClientRuntimeBridge 解耦服务端与客户端 UI

## 状态

已采纳（0.1.2）

## 背景

`common/` 和 `network/` 层的方块与网络包直接调用 `client.ClientHelper` 或 `client.DataPackUploadClient` 来打开客户端 UI，导致服务端环境加载时存在类缺失风险，也违反了"非客户端包不应依赖客户端实现"的分层原则。

## 决策

引入 `common.ClientRuntimeBridge` 接口（默认 no-op），由 `client.ClientRuntimeBridgeImpl` 在 `FMLClientSetupEvent` 期间注入实现。所有跨层 UI 打开调用统一转为 bridge 方法。

## 后果

- `common/`、`network/`、`blocks/custom/` 不再直接 import `client.*`。
- 服务端环境下 bridge 保持 no-op，无类加载风险。
- 新增客户端 UI 入口时需在 bridge 接口和实现中各加一个方法。
