# 401 Unauthorized 错误修复指南

## 问题描述

当调用 Dify API 时出现 `401 Unauthorized` 错误，表示认证失败。

## 可能的原因

1. **API 密钥未配置或为空**
2. **API 密钥无效或已过期**
3. **API 密钥格式错误**
4. **配置文件未正确加载**

## 解决步骤

### 1. 检查配置文件

打开 `backend/src/main/resources/application.yml`，确认配置：

```yaml
dify:
  api:
    api-key: app-xxxxxxxxxxxxx  # 确保这里填写了正确的API密钥
    base-url: https://api.dify.ai/v1
```

**注意：**
- 配置项名称是 `api-key`（带连字符）
- API 密钥应该以 `app-` 开头
- 不要有多余的空格或引号

### 2. 验证 API 密钥

#### 方法1：查看启动日志

重启后端服务，查看控制台输出，应该看到：

```
✅ Dify API配置已加载: apiKey=app-BoKb..., baseUrl=https://api.dify.ai/v1
```

如果看到警告信息，说明配置未正确加载。

#### 方法2：使用 curl 测试 API 密钥

```bash
curl -X POST 'https://api.dify.ai/v1/workflows/run' \
  --header 'Authorization: Bearer app-你的API密钥' \
  --header 'Content-Type: application/json' \
  --data-raw '{
    "inputs": {},
    "response_mode": "blocking",
    "user": "test-user"
  }'
```

如果返回 401，说明 API 密钥无效。

### 3. 获取正确的 API 密钥

1. 登录 Dify 平台：https://dify.ai
2. 进入 **设置** → **API 密钥**
3. 创建新的 API 密钥或使用现有的
4. 确保 API 密钥有工作流调用权限

### 4. 检查 API 密钥格式

正确的 API 密钥格式：
- ✅ `app-xxxxxxxxxxxxx`（以 `app-` 开头）
- ❌ `app-xxxxxxxxxxxxx `（末尾有空格）
- ❌ `"app-xxxxxxxxxxxxx"`（有引号）
- ❌ `app-xxxxxxxxxxxxx\n`（有换行符）

### 5. 重启服务

修改配置后，必须重启后端服务：

```bash
# 停止当前服务（Ctrl+C）
# 然后重新启动
cd backend
mvn spring-boot:run
```

### 6. 查看详细日志

如果问题仍然存在，查看后端日志：

```bash
# 查看日志中的错误信息
# 应该能看到：
# - API密钥的前8位（用于确认是否正确加载）
# - 请求URL
# - 错误详情
```

## 常见错误

### 错误1：配置未加载

**症状：** 日志显示 "API密钥未配置"

**解决：**
1. 检查 `application.yml` 文件路径是否正确
2. 检查 YAML 格式是否正确（缩进、冒号等）
3. 确认配置项名称是 `api-key`（不是 `apiKey`）

### 错误2：API 密钥无效

**症状：** curl 测试也返回 401

**解决：**
1. 在 Dify 平台重新生成 API 密钥
2. 确保 API 密钥有工作流调用权限
3. 检查 API 密钥是否已过期

### 错误3：配置格式错误

**症状：** 配置加载但值为空

**解决：**
```yaml
# ✅ 正确
api-key: app-BoKbGaBooRzT7SH28lgYbskI

# ❌ 错误（有引号）
api-key: "app-BoKbGaBooRzT7SH28lgYbskI"

# ❌ 错误（有空格）
api-key: app-BoKbGaBooRzT7SH28lgYbskI 
```

## 验证修复

修复后，重新测试：

1. 访问前端页面：`http://localhost:3000/ai-assistant`
2. 输入一个问题，例如："我想去南京玩3天"
3. 查看浏览器控制台，应该不再有 401 错误
4. 查看后端日志，应该能看到成功的请求

## 仍然无法解决？

如果按照以上步骤仍然无法解决，请检查：

1. **网络连接**：确保能访问 `https://api.dify.ai`
2. **防火墙/代理**：检查是否有防火墙阻止请求
3. **Dify 平台状态**：确认 Dify 服务是否正常运行
4. **API 配额**：检查是否超出 API 调用限制

## 联系支持

如果问题仍然存在，请提供以下信息：

1. 后端启动日志（包含配置加载信息）
2. 错误堆栈信息
3. API 密钥的前8位（不要提供完整密钥）
4. curl 测试结果

