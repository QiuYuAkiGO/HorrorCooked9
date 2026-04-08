# 运维手册（最小版）

本文是 Phase 1 的最小运维入口，覆盖"能启动、能配置、能排障"的基础路径。配置字段细节见 [config-index.md](config-index.md)。

## 1. 服务端关键配置

- Forge 配置文件：`config/horrorcooked9-server.toml`
- 重点项：
  - `datapackUpload.enabled`
  - `datapackUpload.maxUploadSizeMb`

建议变更流程：

1. 先在测试服改配置并重启验证。
2. 再同步到正式服。
3. 记录变更时间、操作者、回滚参数。

## 2. 数据包上传运维约束

- 关闭 `datapackUpload.enabled` 后，`/datapack upload` 与 `/datapack delete` 不可用。
- 上传体积受 `maxUploadSizeMb` 限制。
- 上传内容需包含 `pack.mcmeta`。

## 3. 常见排障

### 上传失败

- 检查服务端配置是否开启上传。
- 检查文件是否超限。
- 检查 zip 根目录是否包含 `pack.mcmeta`。

### 配置改动不生效

- 区分 Forge 配置与数据包资源配置。
- 确认改动路径是否在 `data/horrorcooked9/`。
- 重新加载资源或重启服务端后复测。

## 4. 变更记录模板（建议）

- 变更项：
- 变更原因：
- 影响范围：
- 回滚方式：
