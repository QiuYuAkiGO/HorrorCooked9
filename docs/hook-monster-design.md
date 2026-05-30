# 钩爪怪物设计与实现说明

> 版本: 1.0 · 状态: 已对齐当前实现 · 日期: 2026-04-25

本文记录 `horrorcooked9:hook_monster`、`horrorcooked9:hook` 与 `horrorcooked9:excrement` 的玩法目标、当前代码入口、配置项、已知缺口与验证清单。它是维护钩爪怪物相关代码时的基准文档。

## 一、玩法目标

钩爪怪物是一个仅通过指令或刷怪蛋生成的自定义敌对实体，不参与自然生成。

核心行为如下：

1. **排泄行为**：怪物周期性一次抛射生成 2-4 个排泄物实体，散布半径相对配置值放大。排泄物可被玩家手动击杀以阻止爆炸；如果存活倒计时结束，会触发不破坏方块的范围伤害与虚弱效果。
2. **钩爪攻击**：怪物锁定玩家后，按冷却发射无重力钩爪。钩爪命中玩家时，将玩家快速拉向怪物并造成最大生命百分比伤害。
3. **自伤晕厥**：钩爪命中该怪物自己生成的排泄物时，怪物进入晕厥状态，暂停后续排泄与钩爪攻击。
4. **服务端可配置**：战斗数值由 Forge Server Config 提供默认值，并预留运行时指令覆盖入口。

## 二、当前实现入口

| 主题 | 文件 | 当前状态 |
| --- | --- | --- |
| 怪物实体 | `src/main/java/net/qiuyu/horrorcooked9/entity/custom/HookMonsterEntity.java` | 已实现基础 AI、排泄、钩爪发射、晕厥状态、GeckoLib 动画控制 |
| 钩爪实体 | `src/main/java/net/qiuyu/horrorcooked9/entity/custom/HookEntity.java` | 已实现无重力投射、命中玩家拉拽与伤害、命中自身排泄物晕厥、命中方块消失 |
| 排泄物实体 | `src/main/java/net/qiuyu/horrorcooked9/entity/custom/ExcrementEntity.java` | 已实现可攻击、碰撞、倒计时、范围伤害、虚弱效果、NBT 保存 |
| 实体注册 | `src/main/java/net/qiuyu/horrorcooked9/register/ModEntities.java` | 已注册 `hook_monster`、`hook`、`excrement` |
| 刷怪蛋 | `src/main/java/net/qiuyu/horrorcooked9/register/ModItems.java` | 已注册 `hook_monster_spawn_egg` |
| 属性与渲染注册 | `src/main/java/net/qiuyu/horrorcooked9/HorrorCooked9.java` | 已注册属性、客户端渲染器、GeckoLib 相关资源 |
| 配置项 | `src/main/java/net/qiuyu/horrorcooked9/config/ModServerConfig.java` | 已加入钩爪怪物与排泄物配置项 |
| 运行时配置访问器 | `src/main/java/net/qiuyu/horrorcooked9/config/HookMonsterRuntimeConfig.java` | 已统一 Forge Server Config 与运行时覆盖读取入口 |
| 运行时配置指令 | `src/main/java/net/qiuyu/horrorcooked9/commands/MonsterConfigCommand.java` | 已在 `ModCommands` 中注册，实体会读取运行时覆盖值 |
| 钩爪伤害源 | `src/main/java/net/qiuyu/horrorcooked9/register/ModDamageSources.java` | 已注册专用 DamageType 资源键，并通过数据标签绕过护甲与盾牌 |
| 客户端模型 | `src/main/java/net/qiuyu/horrorcooked9/client/model/HookMonsterModel.java` | 已指向 GeckoLib geo、贴图、动画资源 |
| 客户端渲染 | `src/main/java/net/qiuyu/horrorcooked9/client/renderer/` | 已实现怪物晕厥抖动、排泄物方块渲染、钩爪与钩索线渲染 |

## 三、实体规格

### 3.1 HookMonsterEntity

当前继承关系：

```text
HookMonsterEntity extends Monster implements GeoEntity
```

主要状态：

| 字段 | 类型 | 用途 |
| --- | --- | --- |
| `excreteTimer` | `int` | 排泄倒计时，归零时生成 `ExcrementEntity` |
| `hookCooldown` | `int` | 钩爪冷却倒计时 |
| `isStunned` | `boolean` | 是否处于晕厥状态 |
| `stunTickRemaining` | `int` | 晕厥剩余 tick |
| `ownedExcrement` | `WeakReference<ExcrementEntity>` | 最近生成的排泄物弱引用，当前主要作为状态记录 |

基础目标：

- `FloatGoal`
- `MeleeAttackGoal`（主动追击并近战攻击玩家）
- `WaterAvoidingRandomStrollGoal`
- `LookAtPlayerGoal`
- `RandomLookAroundGoal`
- `HurtByTargetGoal`
- `NearestAttackableTargetGoal<Player>`

当前没有单独的 `ExcreteGoal` 或 `HookAttackGoal` 类；排泄与钩爪发射逻辑直接在 `HookMonsterEntity#tick()` 中按服务端 tick 驱动，近战行为由 `MeleeAttackGoal` 处理。

### 3.2 ExcrementEntity

当前继承关系：

```text
ExcrementEntity extends LivingEntity
```

主要状态：

| 字段 | 类型 | 用途 |
| --- | --- | --- |
| `ownerUUID` | `UUID` | 生成该排泄物的怪物 UUID，用于钩爪命中时判断是否属于自身 |
| `lifetime` | `int` | 剩余存活 tick |
| `maxLifetime` | `int` | 最大存活 tick |
| `maxHealth` | `float` | 最大生命值 |
| `explosionPower` | `float` | 爆炸范围配置来源 |
| `explosionDamage` | `float` | 到期伤害 |
| `weaknessDuration` | `int` | 到期施加虚弱效果的 tick |

行为约束：

- 只在服务端减少 `lifetime`。
- 玩家可手动击杀排泄物，命中后会立即移除，不触发爆炸。
- 玩家以外的伤害源仍按生命值扣减，生命归零时直接移除，不触发爆炸。
- 倒计时归零时播放爆炸音效与粒子，遍历范围内存活 `LivingEntity`，造成魔法伤害并施加 `MobEffects.WEAKNESS`。
- 当前实现不调用破坏方块的爆炸 API，因此不会破坏地形。
- 当前范围计算为 `explosionPower * 2` 后用于 AABB 与距离判断；如果希望配置值严格等于半径，需要调整 `ExcrementEntity#explode()`。

### 3.3 HookEntity

当前继承关系：

```text
HookEntity extends Projectile
```

主要状态：

| 字段 | 类型 | 用途 |
| --- | --- | --- |
| `pullStrength` | `float` | 命中玩家时的拉拽倍率 |
| `damagePercent` | `float` | 命中玩家时按最大生命计算的伤害百分比 |
| `DATA_FLIGHT_SPEED` | `float` | 同步的飞行速度数据 |

命中规则：

- 命中玩家：按怪物位置计算方向，设置玩家 `deltaMovement`、`hurtMarked`、`hasImpulse`，随后造成 `player.getMaxHealth() * damagePercent` 的伤害。
- 命中排泄物：若排泄物 `ownerUUID` 等于钩爪 owner 怪物 UUID，则调用 `monster.setStunned(true, stunDuration)`。
- 命中方块：服务端直接 `discard()`，不触发额外效果。
- 超时保护：服务端 `tickCount > 100` 时自动移除。

注意：当前伤害源使用 `mobAttack(monster)`，并非绕过护甲的自定义真实伤害；“穿甲百分比伤害”仍属于待实现项。

## 四、行为流程

### 4.1 服务端 tick

```text
HookMonsterEntity#tick()
  ├─ 客户端直接返回
  ├─ 若 isStunned:
  │    ├─ stunTickRemaining--
  │    ├─ 到期后解除晕厥
  │    ├─ 发送 CRIT 粒子
  │    └─ 本 tick 不排泄、不发射钩爪
  ├─ excreteTimer--
  │    └─ 归零: performExcrete()，重置排泄间隔
  ├─ hookCooldown > 0: 递减冷却
  └─ 若有 target:
       └─ 冷却归零、目标在可发射距离区间内且有视线: fireHook(target)，重置钩爪冷却并触发挥手动画
```

### 4.2 排泄生成

`performExcrete()` 每次会生成 2-4 个 `ExcrementEntity`，写入怪物 UUID，并记录最近生成的实例到 `ownedExcrement`。排泄物从怪物身体附近生成并带有向外上抛的初速度，形成抛射散布；散布半径相对 `excrementScatterRadius` 做了放大，以便生成点距离怪物更远。

### 4.3 钩爪发射

`fireHook(target)` 在怪物眼部前方创建 `HookEntity`（避免与自身碰撞），设置 owner 后朝目标胸口附近发射。飞行速度来自 `HookMonsterRuntimeConfig` 的 `hookFlightSpeed` 有效值，默认 `1.0` 格/tick；发射前会检查目标距离位于可发射区间并具有视线。

## 五、配置项

Forge 服务端配置路径：

```text
config/horrorcooked9-server.toml
```

当前配置分组为 `hookMonster`：

| 键 | 默认值 | 范围 | 说明 |
| --- | --- | --- | --- |
| `maxHealth` | `80.0` | `10.0..1000.0` | 怪物最大生命 |
| `armor` | `6.0` | `0.0..30.0` | 怪物护甲 |
| `moveSpeed` | `0.25` | `0.05..1.0` | 怪物移动速度 |
| `excreteInterval` | `200` | `20..72000` | 排泄间隔 tick |
| `hookCooldown` | `60` | `10..72000` | 钩爪冷却 tick |
| `hookMaxRange` | `16.0` | `1.0..64.0` | 跟随距离属性来源 |
| `pullStrength` | `1.5` | `0.1..10.0` | 命中玩家的拉拽倍率 |
| `damagePercent` | `0.15` | `0.01..1.0` | 按最大生命计算的伤害百分比 |
| `stunDuration` | `60` | `10..72000` | 钩中自身排泄物后的晕厥 tick |
| `excrementLifetime` | `200` | `20..72000` | 排泄物存活 tick |
| `excrementScatterRadius` | `2.0` | `0.5..8.0` | 排泄物随机散布半径来源 |
| `excrementMaxHealth` | `10.0` | `1.0..100.0` | 排泄物最大生命 |
| `excrementExplosionPower` | `4.0` | `2.0..16.0` | 排泄物到期范围计算来源 |
| `excrementExplosionDamage` | `8.0` | `1.0..40.0` | 排泄物到期伤害 |
| `excrementWeaknessDuration` | `600` | `20..72000` | 虚弱持续 tick |
| `hookFlightSpeed` | `1.0` | `0.1..5.0` | 钩爪飞行速度，单位为格/tick |

当前实体代码通过 `HookMonsterRuntimeConfig` 读取有效值：无运行时覆盖时返回 `ModServerConfig`，有覆盖时返回指令写入的内存值。怪物实体会在服务端 tick 中刷新最大生命、护甲、移速与跟随距离属性；排泄物和钩爪在创建时读取当时的有效配置。

## 六、运行时指令

`MonsterConfigCommand` 已实现以下节点：

```text
/horrorCooked monster config query
/horrorCooked monster config query <key>
/horrorCooked monster config set <key> <value>
/horrorCooked monster config reset
```

权限要求：

```text
source.hasPermission(2)
```

支持的 key：

```text
maxHealth, armor, moveSpeed,
excreteInterval, hookCooldown, hookMaxRange,
pullStrength, damagePercent, stunDuration,
excrementLifetime, excrementScatterRadius,
excrementMaxHealth, excrementExplosionPower,
excrementExplosionDamage, excrementWeaknessDuration,
hookFlightSpeed
```

运行时覆盖只存在内存中，服务端重启后恢复 Forge Server Config 默认值；这符合当前类的提示文案。
指令写入前会按 `ModServerConfig` 中相同的上下限做范围校验。

## 七、注册与资源

### 7.1 实体注册

| 资源 ID | EntityType | 分类 | 尺寸 | 跟踪范围 |
| --- | --- | --- | --- | --- |
| `horrorcooked9:hook_monster` | `HookMonsterEntity` | `MobCategory.MONSTER` | `1.4 x 2.4` | `10` |
| `horrorcooked9:excrement` | `ExcrementEntity` | `MobCategory.MISC` | `1.0 x 1.0` | `4` |
| `horrorcooked9:hook` | `HookEntity` | `MobCategory.MISC` | `0.3 x 0.3` | `4` |

### 7.2 客户端资源

| 资源 | 路径 |
| --- | --- |
| 怪物模型 | `src/main/resources/assets/horrorcooked9/geo/hook_monster.geo.json` |
| 怪物动画 | `src/main/resources/assets/horrorcooked9/animations/hook_monster.animation.json` |
| 怪物贴图 | `src/main/resources/assets/horrorcooked9/textures/entity/hook_monster.png` |
| 钩爪贴图 | `src/main/resources/assets/horrorcooked9/textures/entity/hook.png` |

### 7.3 渲染策略

- `HookMonsterRenderer` 继承 GeckoLib `GeoEntityRenderer`，晕厥时加入轻微横向抖动。
- `ExcrementRenderer` 使用 `Blocks.DIRT` 的方块模型作为临时表现，按剩余生命周期缩放，并按实体 ID 做固定旋转。
- `HookRenderer` 渲染一个小型面片作为钩爪，并在 owner 为 `HookMonsterEntity` 时绘制分段贝塞尔钩索线。

## 八、与原设计相比的已知缺口

以下项目不应在玩家说明中承诺为“已完成”：

| 缺口 | 当前情况 | 建议处理 |
| --- | --- | --- |
| 客户端特殊拉拽表现未实现 | 当前由服务端连续覆盖玩家速度完成短时控制 | 仅在需要本地屏幕特效或预测时添加 S2C 包 |
| 排泄物落地修正未实现 | 生成时使用怪物当前 `Y` 坐标 | 如出现悬空/卡方块，再补地面搜索 |
| 自定义网络同步未实现 | 没有 `MonsterConfigSyncPacket` 或 `HookPullEffectPacket` | 仅在需要客户端特殊表现时添加 |
| 自然生成未实现 | 当前符合“仅指令/刷怪蛋”决策 | 无需处理，除非设计变更 |

## 九、测试清单

### 9.1 手动验证

1. 启动开发客户端，创建测试世界。
2. 使用 `/summon horrorcooked9:hook_monster` 生成怪物。
3. 靠近怪物，确认会主动追击玩家并触发近战攻击。
4. 观察钩爪命中玩家后是否产生拉拽和百分比伤害。
5. 等待排泄物生成，攻击排泄物，确认生命归零后移除且不爆炸。
6. 不清理排泄物，等待倒计时结束，确认范围伤害与虚弱效果生效且不破坏方块。
7. 引导钩爪命中该怪物自己的排泄物，确认怪物进入晕厥、播放粒子并暂停排泄/钩爪。
8. 使用刷怪蛋生成，确认物品可见且实体渲染正常。

### 9.2 回归验证

建议在修改实体、配置或注册后至少运行：

```powershell
.\gradlew build
```

如果只修改文档，不需要运行构建；如果接入指令注册或改实体逻辑，应运行构建并进游戏验证命令树。

## 十、后续可选增强

当前文档中的核心功能已落地。后续可按实际游玩反馈选择增强：

1. 若多人视觉反馈不足，补充 `HookPullEffectPacket` 或类似 S2C 包。
2. 若排泄物生成偶发悬空/卡方块，给 `performExcrete()` 增加落地与碰撞空间搜索。
3. 若需要更强的配置持久化，将运行时覆盖写入世界存档能力，而不是仅保存在内存中。

---

本文遵循项目现有约定：`mod_id` 为 `horrorcooked9`，Java 根包为 `net.qiuyu.horrorcooked9`，资源 ID 使用 `lower_snake_case`，客户端渲染代码保留在 `client/` 树下。
