# 工程修复计划 v3

本版只保留高收益、低争议、可直接落地的修改项，目标是先封住安全与状态问题，再做最小范围的结构收口。

## 目标

- 优先封堵可作弊、可越权和可导致服务端异常的路径。
- 只保留短期可落地且收益明确的修改。
- 将高成本重构移出本轮，避免过度设计。

## 本轮必做

### P0 协议硬化

涉及文件：

- `src/main/java/net/qiuyu/horrorcooked9/network/gameplay/ChopResultPacket.java`
- `src/main/java/net/qiuyu/horrorcooked9/network/gameplay/StirResultPacket.java`

关键修改：

- 为切菜与搅拌结果包补齐 `level.isLoaded(pos)` 与玩家交互距离校验。
- 切菜结果包必须校验主手仍为 `Cleaver`，否则直接拒绝处理。
- 搅拌结果包必须校验当前搅拌工具、结果数量与配方要求一致。
- 对搅拌结果包的结果列表长度做上限校验；非法包直接拒绝，不做截断兼容。
- 对异常状态直接返回，例如目标方块实体不存在、当前状态不允许结算、结果数量不匹配。

### P0.5 逻辑收口

涉及文件：

- `src/main/java/net/qiuyu/horrorcooked9/gameplay/chopping/`
- `src/main/java/net/qiuyu/horrorcooked9/gameplay/salad/SaladRecipeMatcher.java`
- `src/main/java/net/qiuyu/horrorcooked9/blocks/custom/SaladBowlBlock.java`
- `src/main/java/net/qiuyu/horrorcooked9/network/gameplay/StirResultPacket.java`

关键修改：

- 抽出 `ChopGameService`，把 `ChopResultPacket.handle()` 中的结算逻辑下沉为服务层。
- 将 `resolveStirRecipe(...)` 收敛到 `SaladRecipeMatcher`，避免 `SaladBowlBlock` 与 `StirResultPacket` 维护两份相同逻辑。
- 网络层只保留反序列化、基础校验和服务调用，不再直接承载完整结算流程。

### P1 状态治理

涉及文件：

- `src/main/java/net/qiuyu/horrorcooked9/network/datapack/DataPackUploadManager.java`
- `src/main/java/net/qiuyu/horrorcooked9/config/ModServerConfig.java`

关键修改：

- 为上传会话增加 TTL。
- 单玩家只允许一个活跃上传会话。
- 在玩家退出时清理其上传会话。
- 删除未实际生效的 `Shelter9Support` 配置，避免双源配置误导。

## 暂不纳入本轮

- 将小游戏完全改为服务端权威结算：改动面过大，单独立项更合适。
- 拆分 `HorrorCooked9.java` 中的客户端注册逻辑：收益有限，后置处理。
- 合并 `isFoil()` 样板物品子类：属于整理项，不影响当前风险闭环。
- 修改 `colorful_palette` 的 registry id：兼容性风险高，不纳入本轮。
- 全量统一 `FoodProperties` 构建链路：先不扩大范围，后续视维护痛点再做。

## 建议执行顺序

1. 完成 `P0` 协议硬化。
2. 完成 `P0.5` 逻辑收口。
3. 完成 `P1` 上传会话治理与死配置清理。

## 完成标准

- 玩家不能空手或超距离提交切菜与搅拌结果。
- 非法或畸形网络包会被明确拒绝。
- `ChopResultPacket` 不再直接承载完整结算逻辑。
- 上传会话不会因为断线或超时长期滞留。
