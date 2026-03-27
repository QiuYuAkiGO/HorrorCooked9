# HorrorCooked9 命名规范

本文档用于统一工程内命名，避免同一概念出现多套写法。

## 1. 通用命名规则

- Java 类、接口、枚举、Record 使用 `PascalCase`。
- Java 方法、变量、参数使用 `camelCase`。
- Java 常量使用 `UPPER_SNAKE_CASE`。
- 包名使用全小写名词，避免动词化目录名。
- 资源 ID、Tag 名、JSON key、文件名统一 `lower_snake_case`。

## 2. 类型后缀规范

- 方块类统一使用 `*Block` 后缀。
- 方块实体类统一使用 `*BlockEntity` 后缀。
- 网络包类统一使用 `*Packet` 后缀。
- 配置类统一使用 `*Config` 后缀。
- 注册入口类统一使用 `*Registrar` 或 `Mod*`（二选一保持一致）。

## 3. 领域词汇白名单

同一领域概念只允许一种英文命名：

- 身份标签统一使用 `chef`（禁用 `cook`）。
- 数据包上传通信域统一使用 `datapack`（禁用 `develop` 作为业务域命名）。

## 4. 禁用词与迁移映射

- `cook` -> `chef`
- `network.develop` -> `network.datapack`
- `ChoppingBoard` -> `ChoppingBoardBlock`
- `ModLangGenEN` -> `ModLangGenEnUs`
- `ModLangGenCN` -> `ModLangGenZhCn`

## 5. 目录职责约束

- `register` 目录仅保留 Forge 注册相关入口。
- 网络装配逻辑按域拆分到 `network/*` 下的注册器类。
- 纯文档存放在 `docs/`，`src/main/resources` 仅保留运行期资源。
