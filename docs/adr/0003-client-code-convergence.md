# ADR-0003: 客户端代码归入 client/ 树下

## 状态

已采纳（0.1.3c）

## 背景

GUI Screen 类（`ChopMinigameScreen`、`StirMinigameScreen`）原位于 `gameplay/` 子包，方块实体渲染器位于 `blocks/renderer/`，盔甲渲染器位于 `armor/renderer/`。这些类全部依赖 `net.minecraft.client.*`，但分散在非客户端包中，导致：

- 新人难以建立"改 UI/渲染去哪"的心智模型。
- 无法通过包路径机械判断某个类是否为客户端专属。
- 与"凡依赖 client API 均归入 client 树"的原则不一致。

## 决策

将上述类统一迁移到 `client/` 子包：

- `client/screen/`：小游戏 Screen。
- `client/renderer/`：方块实体渲染器与盔甲渲染层。

原目录中的非客户端类（如 `ChopResult`、`StirResult`、配方匹配器）保留在 `gameplay/` 下。

## 后果

- `client/` 树成为所有客户端专属代码的唯一入口。
- 跨层 import 检查可用简单规则实现：非 `client/` 包不应 import `client.*`。
- 迁移后需更新 `ClientHelper`、`HorrorCooked9`、`CaptainHat` 中的 import。
