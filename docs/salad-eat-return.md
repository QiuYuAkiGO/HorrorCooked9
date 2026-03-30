# 沙拉成品食用返还

## 行为说明

- 玩家从沙拉碗取餐时，会消耗配方中的 **盛装容器**（`serving_container`，当前标签多为碗）。
- 玩家**吃完**对应的沙拉成品食物后，服务端会按匹配到的 `salad_bowl` 配方，返还 **1 个盛装容器的代表物**（与取餐消耗对应，通常为碗）。
- **不**返还搅拌工具（`mixing_tool`）；搅拌时的耐久等仍按原有逻辑处理。

## 实现位置

| 说明 | 文件 |
| --- | --- |
| 食用完成事件（服务端、非创造模式才返还） | `src/main/java/net/qiuyu/horrorcooked9/events/SaladEatReturnEvents.java` |
| 配方匹配与返还物生成 | `src/main/java/net/qiuyu/horrorcooked9/gameplay/salad/SaladEatReturnHelper.java` |

## 配方与限制

- 通过 `result` 物品匹配 `horrorcooked9:salad_bowl` 配方；若多条配方产出同一物品，按配方 ID 字典序取第一条。
- `serving_container` 若为 **物品标签**，代表物取 `Ingredient.getItems()[0]`，顺序可能与标签 JSON 中列举顺序不一致。
- 若将来需要「与玩家当时手持的盛装容器完全一致」，需在取餐时给沙拉成品写入 NBT 记录配方或容器栈（当前未实现）。
