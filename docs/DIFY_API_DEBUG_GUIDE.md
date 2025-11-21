# Dify API 调用调试指南

## 🔍 已添加的调试功能

### 1. 详细的日志记录

代码中已添加详细的日志输出，帮助诊断问题：

#### 控制器层日志
- 📋 接收到的请求参数：显示前端传递的所有参数
- 📦 构建的工作流输入参数：显示最终发送给Dify的参数及其类型

#### 服务层日志
- ✅ 使用API密钥：显示API密钥前缀（不暴露完整密钥）
- ✅ 请求URL：显示完整的API URL
- 📤 发送请求体到Dify API：显示完整的请求信息
  - URL
  - HTTP方法
  - 请求头
  - 请求体JSON
  - Inputs详情

#### 错误日志
- ❌ 错误状态码和详细错误信息
- 📥 Dify API错误响应体：显示Dify返回的错误详情

### 2. 错误处理改进

针对不同错误码提供详细的诊断信息：

- **401 Unauthorized**: API密钥认证失败
- **400 Bad Request**: 请求参数错误（会显示当前inputs）
- **404 Not Found**: 工作流不存在
- **429 Too Many Requests**: API调用频率超限

## 📋 参数格式检查清单

### 工作流输入参数格式

所有参数必须符合以下格式：

```json
{
  "inputs": {
    "intent_type": "1",      // 字符串类型，必填
    "num": "2",              // 字符串类型，必填
    "date": "2025-02-01",    // 字符串类型，非必填
    "destination": "南京",    // 字符串类型，必填
    "duration": "3",         // 字符串类型，必填
    "special_need": "0",     // 字符串类型，必填
    "instruction": "..."     // 字符串类型，非必填
  },
  "response_mode": "streaming",
  "user": "user-123"
}
```

### 关键检查点

1. ✅ **参数名格式**：使用下划线（`intent_type`, `special_need`）
2. ✅ **参数类型**：所有参数都是字符串类型（String）
3. ✅ **必填字段**：所有必填字段都有默认值 "0"（除了 `intent_type` 默认为 "1"）
4. ✅ **请求头格式**：`Authorization: Bearer {apiKey}`

## 🔧 调试步骤

### 步骤1：查看启动日志

重启后端服务，查看配置加载：

```
✅ Dify API配置已加载: apiKey=app-BoKb..., baseUrl=https://api.dify.ai/v1
```

### 步骤2：测试API调用

在前端输入问题，然后查看后端日志：

```
📋 接收到的请求参数:
   intentType: 1
   num: 2
   date: 0
   destination: 南京
   duration: 3
   specialNeed: 0
   instruction: 我想去南京玩3天

📦 构建的工作流输入参数:
   intent_type = 1 (类型: String)
   num = 2 (类型: String)
   date = 0 (类型: String)
   destination = 南京 (类型: String)
   duration = 3 (类型: String)
   special_need = 0 (类型: String)
   instruction = 我想去南京玩3天 (类型: String)

📤 发送请求体到Dify API:
   URL: https://api.dify.ai/v1/workflows/run
   Method: POST
   Headers: Authorization=Bearer app-BoKb..., Content-Type=application/json, Accept=text/event-stream
   Body: {"inputs":{"intent_type":"1","num":"2",...},"response_mode":"streaming","user":"user-123"}
```

### 步骤3：检查错误信息

如果出现错误，日志会显示：

```
❌ Dify API调用失败: status=400, url=https://api.dify.ai/v1/workflows/run
❌ 400 Bad Request - 请求参数错误！
请检查：
1. 工作流输入参数是否符合要求
2. 必填字段是否都已提供
3. 参数格式是否正确（所有参数应为字符串类型）
当前inputs: {...}

📥 Dify API错误响应体: {...}
```

## 🐛 常见问题排查

### 问题1：400 Bad Request

**可能原因：**
1. 参数名不匹配（工作流期望的参数名与代码中的不一致）
2. 参数类型错误（应该是字符串，但传了数字）
3. 缺少必填字段

**解决方法：**
1. 查看日志中的 "📦 构建的工作流输入参数"，确认参数名和值
2. 查看 "📥 Dify API错误响应体"，了解Dify的具体错误信息
3. 在Dify平台检查工作流的输入变量名，确保与代码中的一致

### 问题2：401 Unauthorized

**可能原因：**
1. API密钥无效或已过期
2. API密钥格式错误

**解决方法：**
1. 在Dify平台验证API密钥
2. 检查日志中的 "✅ 使用API密钥"，确认密钥前缀正确
3. 使用curl测试API密钥是否有效

### 问题3：404 Not Found

**可能原因：**
1. 工作流ID错误
2. 使用了不存在的工作流

**解决方法：**
1. 检查工作流ID是否正确
2. 如果不指定workflowId，确保Dify平台有默认工作流

## 📝 参数映射对照表

| 工作流变量名 | Java字段名 | 前端参数名 | 类型 | 必填 | 默认值 |
|------------|-----------|-----------|------|------|--------|
| `intent_type` | `intentType` | `intentType` | String | ✅ | "1" |
| `num` | `num` | `num` | String | ✅ | "0" |
| `date` | `date` | `date` | String | ❌ | "0" |
| `destination` | `destination` | `destination` | String | ✅ | "0" |
| `duration` | `duration` | `duration` | String | ✅ | "0" |
| `special_need` | `specialNeed` | `specialNeed` | String | ✅ | "0" |
| `instruction` | `instruction` | `instruction` | String | ❌ | "0" |

## ✅ 验证清单

- [x] 所有参数都是字符串类型
- [x] 参数名使用下划线格式（`intent_type`, `special_need`）
- [x] 所有必填字段都有默认值
- [x] 请求头格式正确：`Authorization: Bearer {apiKey}`
- [x] 请求体格式正确：包含 `inputs`, `response_mode`, `user`
- [x] 详细日志已启用
- [x] 错误处理已完善

## 🎯 下一步

1. **重启后端服务**，查看启动日志
2. **测试API调用**，查看详细的请求日志
3. **如果仍有错误**，查看日志中的错误详情，根据错误信息调整参数

所有代码已添加详细的日志和错误处理，可以帮助快速定位问题！

