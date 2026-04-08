# ADR-0002: 网络包按业务域拆分子包

## 状态

已采纳（0.0.3）

## 背景

早期所有网络包集中在 `network/` 根目录，随着玩法网络包（切割、搅拌）和数据包上传网络包同时存在，职责边界模糊，扩展时容易相互干扰。

## 决策

将 `network/` 拆分为：

- `network/gameplay/`：玩法结果包（`ChopResultPacket`、`StirResultPacket`），由 `GameplayNetworkRegistrar` 注册。
- `network/datapack/`：数据包上传通信（`OpenDataPackPickerPacket` 等），由 `DatapackNetworkRegistrar` 注册。

公共注册入口 `ModNetworking` 保留在 `network/` 根目录，负责 channel 初始化与子域 registrar 的统一调用。

## 后果

- 新增玩法网络包时只需修改 `network/gameplay/` 及其 registrar。
- 域命名统一为 `datapack`（禁用 `develop`），与命名规范一致。
