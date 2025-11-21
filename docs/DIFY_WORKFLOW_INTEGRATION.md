# Dify 工作流集成说明

本文档说明如何在项目中使用 Dify 工作流 API 生成旅游计划。

## 目录

1. [配置说明](#配置说明)
2. [后端API接口](#后端api接口)
3. [前端使用方法](#前端使用方法)
4. [工作流输入参数](#工作流输入参数)
5. [流式响应处理](#流式响应处理)
6. [错误处理](#错误处理)
7. [示例代码](#示例代码)

## 配置说明

### 后端配置

在 `application.yml` 中配置 Dify API 信息：

```yaml
dify:
  api:
    # Dify API密钥（必填，从Dify平台获取）
    api-key: app-xxxxxxxxxxxxx
    # Dify API基础URL
    base-url: https://api.dify.ai/v1
    # 请求超时时间（毫秒）
    timeout: 30000
```

### 前端配置

前端使用代理转发请求，配置在 `vite.config.js` 中：

```javascript
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true,
      secure: false
    }
  }
}
```

## 后端API接口

### 1. 生成旅游计划（阻塞模式）

**接口地址：** `POST /api/travel/plan/generate`

**请求体：**
```json
{
  "destination": "南京",
  "duration": 3,
  "durationDesc": "3天2晚",
  "dateRange": "2025-02-01 至 2025-02-03",
  "description": "六朝古都·金陵风韵",
  "userId": "user-123",
  "uploadFileId": "file-id-123",  // 可选
  "workflowId": "workflow-id-123",  // 可选
  "workflowInputs": {}  // 可选，额外的工作流输入参数
}
```

**响应：**
```json
{
  "code": 200,
  "message": "旅游计划生成成功",
  "workflowRunId": "run-id-123",
  "taskId": "task-id-123",
  "status": "succeeded",
  "outputs": {
    "text": "生成的旅游计划内容..."
  },
  "elapsedTime": 2.5,
  "totalTokens": 3562
}
```

### 2. 生成旅游计划（流式模式）

**接口地址：** `GET /api/travel/plan/generate/stream`

**查询参数：**
- `destination`: 目的地
- `duration`: 行程时长（天数）
- `durationDesc`: 时长描述
- `dateRange`: 日期范围
- `description`: 描述
- `userId`: 用户ID
- `uploadFileId`: 文件ID（可选）
- `workflowId`: 工作流ID（可选）

**响应格式：** SSE (Server-Sent Events)

**事件类型：**
- `workflow_started`: 工作流开始
- `node_started`: 节点开始执行
- `node_finished`: 节点执行完成
- `workflow_finished`: 工作流执行完成
- `tts_message`: TTS消息（文本转语音）
- `tts_message_end`: TTS消息结束
- `error`: 错误事件

### 3. 上传文件

**接口地址：** `POST /api/travel/plan/upload`

**请求参数：**
- `filePath`: 文件路径（当前实现）
- `userId`: 用户ID
- `fileType`: 文件类型（TXT, IMAGE, AUDIO, VIDEO）

**响应：**
```json
{
  "code": 200,
  "message": "文件上传成功",
  "fileId": "file-id-123"
}
```

### 4. 查询工作流状态

**接口地址：** `GET /api/travel/plan/status/{workflowRunId}`

**响应：**
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "id": "run-id-123",
    "workflow_id": "workflow-id-123",
    "status": "succeeded",
    "outputs": {},
    "error": null,
    "elapsed_time": 2.5,
    "total_tokens": 3562,
    "total_steps": 8,
    "created_at": 1705407629,
    "finished_at": 1727807631
  }
}
```

### 5. 停止工作流任务

**接口地址：** `POST /api/travel/plan/stop/{taskId}`

**查询参数：**
- `userId`: 用户ID

**响应：**
```json
{
  "code": 200,
  "message": "任务已停止"
}
```

## 前端使用方法

### 1. 导入API服务

```javascript
import { difyApi } from '@/services/api/difyApi'
```

### 2. 阻塞模式调用

```javascript
try {
  const response = await difyApi.generateTravelPlan({
    destination: '南京',
    duration: 3,
    durationDesc: '3天2晚',
    dateRange: '2025-02-01 至 2025-02-03',
    description: '六朝古都·金陵风韵',
    userId: 'user-123'
  })
  
  console.log('生成结果:', response)
  // response.outputs 包含工作流的输出结果
} catch (error) {
  console.error('生成失败:', error)
}
```

### 3. 流式模式调用

```javascript
// 创建EventSource连接
const eventSource = difyApi.generateTravelPlanStream({
  destination: '南京',
  duration: 3,
  userId: 'user-123',
  onMessage: (event, data) => {
    // 处理接收到的消息
    console.log('事件:', event, data)
    
    if (event === 'workflow_finished') {
      // 工作流完成
      console.log('工作流执行完成:', data)
    }
  },
  onError: (error) => {
    console.error('错误:', error)
  },
  onComplete: () => {
    console.log('连接完成')
  }
})

// 需要停止时，关闭连接
// eventSource.close()
```

### 4. 使用示例组件

项目中已包含一个完整的示例组件：`DifyWorkflowExample.vue`

在路由中引入使用：

```javascript
import DifyWorkflowExample from '@/components/DifyWorkflowExample.vue'

// 在路由配置中添加
{
  path: '/dify-example',
  component: DifyWorkflowExample
}
```

## 工作流输入参数

根据 Dify 工作流的配置，需要传递相应的输入参数。常见参数包括：

- `destination`: 目的地
- `duration`: 行程时长
- `dateRange`: 日期范围
- `description`: 行程描述

如果工作流需要文件输入，需要先上传文件获取 `fileId`，然后在输入参数中传递：

```javascript
// 文件输入格式
{
  "orig_mail": [{
    "transfer_method": "local_file",
    "upload_file_id": "file-id-123",
    "type": "document"
  }]
}
```

**注意：** 工作流变量名（如 `orig_mail`）需要根据实际的 Dify 工作流配置来设置。

## 流式响应处理

流式响应使用 SSE (Server-Sent Events) 协议，前端通过 `EventSource` API 接收实时事件。

### 事件类型说明

1. **workflow_started**: 工作流开始执行
   ```json
   {
     "event": "workflow_started",
     "task_id": "...",
     "workflow_run_id": "...",
     "data": {
       "id": "...",
       "workflow_id": "...",
       "created_at": 1679586595
     }
   }
   ```

2. **node_started**: 节点开始执行
   ```json
   {
     "event": "node_started",
     "data": {
       "node_id": "...",
       "node_type": "start",
       "title": "Start"
     }
   }
   ```

3. **node_finished**: 节点执行完成
   ```json
   {
     "event": "node_finished",
     "data": {
       "node_id": "...",
       "status": "succeeded",
       "outputs": {},
       "elapsed_time": 0.324
     }
   }
   ```

4. **workflow_finished**: 工作流执行完成
   ```json
   {
     "event": "workflow_finished",
     "data": {
       "id": "...",
       "status": "succeeded",
       "outputs": {
         "text": "生成的旅游计划内容..."
       },
       "elapsed_time": 2.5,
       "total_tokens": 3562
     }
   }
   ```

## 错误处理

### 后端错误处理

后端统一返回格式：

```json
{
  "code": 500,
  "message": "错误信息"
}
```

常见错误码：
- `200`: 成功
- `404`: 资源不存在
- `500`: 服务器内部错误

### 前端错误处理

前端使用 `try-catch` 捕获错误：

```javascript
try {
  const response = await difyApi.generateTravelPlan(params)
} catch (error) {
  // error.message 包含错误信息
  console.error('错误:', error.message)
  ElMessage.error('生成失败：' + error.message)
}
```

## 示例代码

### 完整示例：在 Vue 组件中使用

```vue
<template>
  <div>
    <el-button @click="generatePlan" :loading="loading">
      生成旅游计划
    </el-button>
    
    <div v-if="result">
      <pre>{{ JSON.stringify(result, null, 2) }}</pre>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { difyApi } from '@/services/api/difyApi'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const result = ref(null)

const generatePlan = async () => {
  loading.value = true
  try {
    const response = await difyApi.generateTravelPlan({
      destination: '南京',
      duration: 3,
      durationDesc: '3天2晚',
      userId: 'user-123'
    })
    
    result.value = response
    ElMessage.success('生成成功！')
  } catch (error) {
    ElMessage.error('生成失败：' + error.message)
  } finally {
    loading.value = false
  }
}
</script>
```

## 注意事项

1. **API密钥安全**: API密钥应存储在配置文件中，不要提交到代码仓库
2. **超时设置**: 根据工作流复杂度调整超时时间
3. **错误重试**: 建议实现重试机制处理网络错误
4. **流式连接**: 流式模式下注意及时关闭 EventSource 连接，避免资源泄漏
5. **工作流变量名**: 确保传递的变量名与 Dify 工作流配置一致
6. **文件上传**: 当前实现使用文件路径，实际生产环境应使用 MultipartFile 上传

## 相关文件

- 后端服务: `DifyService.java`
- 后端控制器: `TravelPlanController.java`
- 前端API: `difyApi.js`
- 示例组件: `DifyWorkflowExample.vue`
- 配置文件: `application.yml`

## 技术支持

如有问题，请查看：
- Dify API 文档: https://docs.dify.ai/
- 项目文档: `backend/docs/`

