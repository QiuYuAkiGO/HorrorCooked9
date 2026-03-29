# 配置索引

本文件用于集中说明工程内配置入口、作用范围与生效方式。

## 1. 运行期配置

| 配置项 | 文件位置 | 作用域 | 生效方式 |
| --- | --- | --- | --- |
| 数据包上传开关、最大上传大小 | `src/main/java/net/qiuyu/horrorcooked9/config/ModServerConfig.java` | 服务端 | 读取 Forge server config，重启/重载后生效 |
| 搅拌工具平衡参数 | `src/main/resources/data/horrorcooked9/gameplay/stir_tool_balance.json` | 数据包/服务端逻辑 | 作为数据资源加载，随数据包生效 |
| 物品 Tag（砧板可放置、搅拌工具等） | `src/main/resources/data/horrorcooked9/tags/items/*.json` | 数据包/配方与判定 | 数据包加载时生效 |
| 沙拉配方、物品配方 | `src/main/resources/data/horrorcooked9/recipes/**/*.json` | 数据包 | 数据包加载时生效 |

## 2. 构建与元数据配置

| 配置项 | 文件位置 | 作用域 | 生效方式 |
| --- | --- | --- | --- |
| 版本、构建参数 | `gradle.properties` | 构建系统 | Gradle 构建时读取 |
| Mod 元信息 | `src/main/resources/META-INF/mods.toml` | 运行时元数据 | 打包后由 Forge 读取 |

## 3. 使用建议

- 修改配置前，先确认其属于 Forge 配置还是数据包资源，避免改错入口。
- 对平衡相关项优先放在 `data/...`，对安全/权限相关项优先放在 `ModServerConfig`。
- 变更后至少执行一次本地编译与资源校验，确保命名与路径未断链。

## 4. 相关玩法文档

| 主题 | 文档 |
| --- | --- |
| 沙拉成品食用后返还盛装容器（不返还搅拌工具） | [salad-eat-return.md](salad-eat-return.md) |
