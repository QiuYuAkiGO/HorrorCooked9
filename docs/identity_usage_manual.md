# identity give 使用手册

## 1. 功能说明

`/identity give <tag名称> <JSON字符串>` 用于把所有带有指定玩家 tag 的在线玩家，按配置规则随机分配身份。

- 只处理在线玩家
- 需要权限等级 `>= 2`
- 分配结果会以玩家 tag 形式写回（例如 `captain`、`chef`）

---

## 2. 命令格式

`/identity give <玩家筛选tag> <JSON字符串>`

示例：

`/identity give game_player {"identity_name":{"normal":10,"rich":2,"captain":3,"chef":5},"limit_num":{"captain":"..1","chef":"1..3","normal":"2..","rich":"0..2"}}`

---

## 3. JSON 配置说明

### 3.1 `identity_name`（必填）

格式：`{ "身份名": 权重 }`

- 权重必须是正整数
- 权重越大，被随机到的概率越高

示例：

```json
{
  "identity_name": {
    "normal": 10,
    "rich": 2,
    "captain": 3,
    "chef": 5
  }
}
```

### 3.2 `limit_num`（可选）

格式：`{ "身份名": "区间字符串" }`

区间支持：

- `"..n"`：最少 0，最多 n
- `"m.."`：最少 m，最多无限（实际会按玩家人数截断）
- `"m..n"`：最少 m，最多 n
- `"n"`：固定 n 人

示例：

```json
{
  "limit_num": {
    "captain": "..1",
    "chef": "1..3",
    "normal": "2..",
    "rich": "0..2"
  }
}
```

注意：

- `limit_num` 中出现的身份，必须先在 `identity_name` 里定义
- 不写在 `limit_num` 的身份，默认范围是 `0..玩家总数`

---

## 4. 分配规则（实际行为）

1. 先筛选所有带 `<tag名称>` 的在线玩家
2. 读取 `identity_name` 和 `limit_num`
3. 先满足每个身份的下限人数
4. 剩余玩家在未达到上限的身份中按权重随机分配
5. 给每位目标玩家写入最终身份 tag

写回时会清理本次身份集合中的旧 tag（即 `identity_name` 中列出的身份），避免身份残留。

---

## 5. 快速使用流程

1. 给参与玩家打同一个筛选 tag（例如 `game_player`）
2. 准备好 JSON（建议先在 `identity_sample.txt` 调整）
3. 执行 `/identity give <tag> <json>`
4. 用 `/tag <player> list` 检查玩家身份 tag

---

## 6. 常见报错与处理

### 6.1 `未找到带有tag 'xxx' 的在线玩家`

原因：没有在线玩家带该 tag。  
处理：先给玩家添加正确 tag，或确认玩家在线。

### 6.2 `JSON解析失败，请检查语法`

原因：JSON 语法错误（漏逗号、引号不配对等）。  
处理：先用 JSON 校验工具检查，再粘贴到命令中。

### 6.3 `缺少必填字段 'identity_name'`

原因：未提供 `identity_name`。  
处理：补上身份权重配置。

### 6.4 `字段 'identity_name.xxx' 必须为正整数`

原因：权重不是正整数。  
处理：把该值改成大于 0 的整数。

### 6.5 `字段 'limit_num.xxx' 未在 'identity_name' 中定义`

原因：`limit_num` 里用了未声明身份。  
处理：在 `identity_name` 增加该身份，或移除该限制项。

### 6.6 `字段 'limit_num.xxx' 格式错误`

原因：区间写法不合法。  
处理：改为 `"..n"`、`"m.."`、`"m..n"` 或 `"n"` 之一。

### 6.7 `配置不可满足：所有身份下限之和...`

原因：下限总和超过玩家数。  
处理：降低某些身份下限，或增加玩家人数。

### 6.8 `配置不可满足：所有身份上限之和...`

原因：上限总和小于玩家数。  
处理：提高某些身份上限，或减少玩家人数。

---

## 7. 推荐实践

- 身份名建议使用短小、全小写、无空格 tag（如 `captain`、`chef`）
- 先用小规模玩家测试 2-3 次，确认概率与上下限符合预期
- 正式开局前，先清理无关 tag，避免和其他系统冲突

---

## 8. 最小可用模板

```json
{"identity_name":{"captain":1,"chef":3}}
```

不写 `limit_num` 时，系统仅按权重随机分配。

