# 旅游管理系统 API 接口文档

## 文档说明

本文档详细描述了旅游管理系统的所有前后端接口，采用标准化的接口文档格式，包含接口基本信息、请求参数、响应数据等详细信息。

## 接口规范

### 接口基本信息说明
- **接口名称**: 提供接口功能的直接描述
- **接口路径**: 完整的请求路径（基础域名 + 接口路由）
- **请求方法**: 遵循RESTful规范，GET（查询）、POST（创建/提交）、PUT（全量更新）、DELETE（删除）、PATCH（部分更新）
- **接口描述**: 提供接口功能的补充说明
- **是否需要认证**: 标识是否需要身份认证（是/否）
- **接口耗时**: 建议响应时间，帮助开发者评估性能

### 请求参数说明
需要区分请求头（Header）、请求路径参数（Path Param）、请求查询参数（Query Param）、请求体（Body），并明确各参数的规则。

参数以表格形式展示，包含：参数名、参数位置、类型、是否必传、默认值、示例、说明。

---

## 接口分类

### 一级分类：核心功能模块
- **地图路线模块** - 地图生成、路线规划相关接口
- **旅游指南模块** - 旅游指南、准备事项、注意事项管理
- **行程管理模块** - 旅游行程、感受记录、照片管理
- **天气服务模块** - 天气预报、天气建议管理
- **POI服务模块** - 兴趣点搜索、照片获取
- **系统监控模块** - 健康检查、状态监控

---

## 一、地图路线模块 (Route Module)

**📁 代码位置**: [`RouteController.java`](backend/src/main/java/com/tourism/controller/RouteController.java)

### 1.1 地图生成接口

#### 1.1.1 生成地图数据

**接口名称**: 生成地图数据  
**接口路径**: `POST /api/route/map`  
**请求方法**: POST  
**接口描述**: 根据地点列表生成地图数据，支持多种路线策略，返回地图ID和地图URL  
**是否需要认证**: 否  
**接口耗时**: 平均200ms，峰值500ms  
**代码位置**: [`RouteController.generateMap()`](backend/src/main/java/com/tourism/controller/RouteController.java#L59-L80)

**请求参数说明**

| 参数名 | 位置 | 类型 | 必传 | 默认值 | 示例 | 说明 |
|:------|:-----|:-----|:-----|:-------|:-----|:-----|
| `locations` | Body | Array | 是 | - | `["中山陵", "夫子庙", "南京博物院"]` | 地点列表，至少包含2个地点 |
| `strategy` | Body | String | 否 | `"driving"` | `"driving"` | 路线策略：driving（驾车）、walking（步行）、transit（公交） |

**响应数据说明**

| 字段名 | 类型 | 说明 | 示例 |
|:-------|:-----|:-----|:-----|
| `code` | Integer | 响应状态码 | `200` |
| `message` | String | 响应消息 | `"地图生成成功"` |
| `data` | Object | 地图数据 | - |
| `data.mapId` | String | 地图唯一标识 | `"map_123456789"` |
| `data.mapUrl` | String | 地图访问URL | `"https://map.example.com/view/123456789"` |
| `data.routeInfo` | Object | 路线信息 | - |
| `data.routeInfo.distance` | String | 总距离 | `"15.6公里"` |
| `data.routeInfo.duration` | String | 预计时长 | `"45分钟"` |

**前端调用**: 无直接调用

#### 1.1.2 快速生成地图

**接口名称**: 快速生成地图  
**接口路径**: `GET /api/route/map/quick`  
**请求方法**: GET  
**接口描述**: 通过URL参数快速生成地图，适用于简单的地图生成需求  
**是否需要认证**: 否  
**接口耗时**: 平均150ms，峰值300ms  
**代码位置**: [`RouteController.generateMapQuick()`](backend/src/main/java/com/tourism/controller/RouteController.java#L90-L119)

**请求参数说明**

| 参数名 | 位置 | 类型 | 必传 | 默认值 | 示例 | 说明 |
|:------|:-----|:-----|:-----|:-------|:-----|:-----|
| `locations` | Query | String | 是 | - | `"中山陵,夫子庙,南京博物院"` | 地点列表，用逗号分隔 |
| `strategy` | Query | String | 否 | `"driving"` | `"driving"` | 路线策略：driving（驾车）、walking（步行）、transit（公交） |

**响应数据说明**

| 字段名 | 类型 | 说明 | 示例 |
|:-------|:-----|:-----|:-----|
| `code` | Integer | 响应状态码 | `200` |
| `message` | String | 响应消息 | `"快速地图生成成功"` |
| `data` | Object | 地图数据 | - |
| `data.mapId` | String | 地图唯一标识 | `"map_123456789"` |
| `data.mapUrl` | String | 地图访问URL | `"https://map.example.com/view/123456789"` |

**前端调用**: 无直接调用

### 1.2 坐标管理接口

#### 1.2.1 批量更新景点经纬度

**接口名称**: 批量更新景点经纬度  
**接口路径**: `POST /api/route/coordinates/update`  
**请求方法**: POST  
**接口描述**: 批量更新景点的经纬度坐标，支持通过高德地图API获取准确的坐标信息  
**是否需要认证**: 否  
**接口耗时**: 平均500ms，峰值1000ms  
**代码位置**: [`RouteController.batchUpdateCoordinates()`](backend/src/main/java/com/tourism/controller/RouteController.java#L140-L153)

**请求参数说明**

| 参数名 | 位置 | 类型 | 必传 | 默认值 | 示例 | 说明 |
|:------|:-----|:-----|:-----|:-------|:-----|:-----|
| `attractionNames` | Body | Array | 是 | - | `["中山陵", "夫子庙", "南京博物院"]` | 景点名称列表 |

**响应数据说明**

| 字段名 | 类型 | 说明 | 示例 |
|:-------|:-----|:-----|:-----|
| `result` | String | 更新结果信息 | `"成功更新3个景点坐标"` |

**前端调用**: 无直接调用

#### 1.2.2 检查景点坐标缓存状态

**接口名称**: 检查景点坐标缓存状态  
**接口路径**: `POST /api/route/coordinates/status`  
**请求方法**: POST  
**接口描述**: 检查景点坐标的缓存状态，返回缓存命中情况  
**是否需要认证**: 否  
**接口耗时**: 平均100ms，峰值200ms  
**代码位置**: [`RouteController.getCacheStatus()`](backend/src/main/java/com/tourism/controller/RouteController.java#L162-L175)

**请求参数说明**

| 参数名 | 位置 | 类型 | 必传 | 默认值 | 示例 | 说明 |
|:------|:-----|:-----|:-----|:-------|:-----|:-----|
| `attractionNames` | Body | Array | 是 | - | `["中山陵", "夫子庙"]` | 景点名称列表 |

**响应数据说明**

| 字段名 | 类型 | 说明 | 示例 |
|:-------|:-----|:-----|:-----|
| `status` | String | 缓存状态信息 | `"缓存命中率: 80%"` |

**前端调用**: 无直接调用

### 1.3 系统监控接口

#### 1.3.1 路线服务健康检查

**接口名称**: 路线服务健康检查  
**接口路径**: `GET /api/route/health`  
**请求方法**: GET  
**接口描述**: 检查路线规划服务运行状态，用于系统监控  
**是否需要认证**: 否  
**接口耗时**: 平均50ms，峰值100ms  
**代码位置**: [`RouteController.health()`](backend/src/main/java/com/tourism/controller/RouteController.java#L128-L131)

**请求参数说明**

无参数

**响应数据说明**

| 字段名 | 类型 | 说明 | 示例 |
|:-------|:-----|:-----|:-----|
| `status` | String | 服务状态信息 | `"Route Planning Service is running"` |

**前端调用**: 无直接调用

---

## 二、旅游指南模块 (Travel Guide Module)

**📁 代码位置**: [`TravelGuideController.java`](backend/src/main/java/com/tourism/controller/TravelGuideController.java)

### 2.1 注意事项管理

#### 2.1.1 更新我的注意事项

**接口名称**: 更新我的注意事项  
**接口路径**: `POST /api/travel/guide/my-notes/update`  
**请求方法**: POST  
**接口描述**: 保存用户的个人注意事项到数据库，支持实时保存和编辑  
**是否需要认证**: 否  
**接口耗时**: 平均100ms，峰值200ms  
**代码位置**: [`TravelGuideController.updateMyNotes()`](backend/src/main/java/com/tourism/controller/TravelGuideController.java#L43-L84)

**请求参数说明**

| 参数名 | 位置 | 类型 | 必传 | 默认值 | 示例 | 说明 |
|:------|:-----|:-----|:-----|:-------|:-----|:-----|
| `userId` | Body | String | 否 | `"default_user"` | `"user123"` | 用户ID，不传则使用默认用户 |
| `myNotes` | Body | String | 是 | - | `"记得带充电宝和雨伞"` | 注意事项内容，支持多行文本 |

**响应数据说明**

| 字段名 | 类型 | 说明 | 示例 |
|:-------|:-----|:-----|:-----|
| `code` | Integer | 响应状态码 | `200` |
| `message` | String | 响应消息 | `"我的注意事项更新成功"` |
| `data` | String | 更新后的注意事项内容 | `"记得带充电宝和雨伞"` |

**前端调用**: 无直接调用

#### 2.1.2 获取我的注意事项

**接口名称**: 获取我的注意事项  
**接口路径**: `GET /api/travel/guide/my-notes`  
**请求方法**: GET  
**接口描述**: 从数据库读取用户的注意事项内容  
**是否需要认证**: 否  
**接口耗时**: 平均80ms，峰值150ms  
**代码位置**: [`TravelGuideController.getMyNotes()`](backend/src/main/java/com/tourism/controller/TravelGuideController.java#L92-L118)

**请求参数说明**

| 参数名 | 位置 | 类型 | 必传 | 默认值 | 示例 | 说明 |
|:------|:-----|:-----|:-----|:-------|:-----|:-----|
| `userId` | Query | String | 否 | `"default_user"` | `"user123"` | 用户ID，不传则使用默认用户 |

**响应数据说明**

| 字段名 | 类型 | 说明 | 示例 |
|:-------|:-----|:-----|:-----|
| `code` | Integer | 响应状态码 | `200` |
| `message` | String | 响应消息 | `"获取成功"` |
| `data` | String | 注意事项内容 | `"记得带充电宝和雨伞"` |

**前端调用**: 无直接调用

### 2.2 准备事项管理

#### 2.2.1 更新准备事项

**接口名称**: 更新准备事项  
**接口路径**: `POST /api/travel/guide/preparation-items/update`  
**请求方法**: POST  
**接口描述**: 更新用户的旅游准备事项，支持分类管理和进度跟踪  
**是否需要认证**: 否  
**接口耗时**: 平均200ms，峰值400ms  
**代码位置**: [`TravelGuideController.updatePreparationItems()`](backend/src/main/java/com/tourism/controller/TravelGuideController.java#L232-L281)

**请求参数说明**

| 参数名 | 位置 | 类型 | 必传 | 默认值 | 示例 | 说明 |
|:------|:-----|:-----|:-----|:-------|:-----|:-----|
| `userId` | Body | String | 否 | `"default_user"` | `"user123"` | 用户ID，不传则使用默认用户 |
| `preparationItems` | Body | Object | 是 | - | 见下方示例 | 准备事项数据，按分类组织 |

**preparationItems 参数结构示例**:
```json
{
  "交通准备": {
    "completed": 1,
    "total": 1,
    "percentage": 100,
    "items": [
      {
        "name": "南京地铁卡",
        "completed": true,
        "editing": false
      }
    ]
  },
  "住宿安排": {
    "completed": 1,
    "total": 2,
    "percentage": 50,
    "items": [
      {
        "name": "夫子庙酒店确认",
        "completed": true,
        "editing": false
      },
      {
        "name": "酒店早餐确认",
        "completed": false,
        "editing": false
      }
    ]
  }
}
```

**响应数据说明**

| 字段名 | 类型 | 说明 | 示例 |
|:-------|:-----|:-----|:-----|
| `code` | Integer | 响应状态码 | `200` |
| `message` | String | 响应消息 | `"准备事项更新成功"` |
| `data` | Object | 更新后的准备事项数据 | 同请求参数结构 |

**前端调用**: 无直接调用

#### 2.2.2 获取准备事项

**接口名称**: 获取准备事项  
**接口路径**: `GET /api/travel/guide/preparation-items`  
**请求方法**: GET  
**接口描述**: 获取用户的准备事项列表，支持分类展示和进度统计  
**是否需要认证**: 否  
**接口耗时**: 平均150ms，峰值300ms  
**代码位置**: [`TravelGuideController.getPreparationItems()`](backend/src/main/java/com/tourism/controller/TravelGuideController.java#L455-L486)

**请求参数说明**

| 参数名 | 位置 | 类型 | 必传 | 默认值 | 示例 | 说明 |
|:------|:-----|:-----|:-----|:-------|:-----|:-----|
| `userId` | Query | String | 否 | `"default_user"` | `"user123"` | 用户ID，不传则使用默认用户 |

**响应数据说明**

| 字段名 | 类型 | 说明 | 示例 |
|:-------|:-----|:-----|:-----|
| `code` | Integer | 响应状态码 | `200` |
| `message` | String | 响应消息 | `"获取成功"` |
| `data` | Object | 准备事项数据 | 按分类组织的准备事项 |

**data 字段结构示例**:
```json
{
  "交通准备": {
    "completed": 1,
    "total": 1,
    "percentage": 100,
    "items": [
      {
        "name": "南京地铁卡",
        "completed": true,
        "editing": false
      }
    ]
  }
}
```

**前端调用**: 无直接调用

### 2.3 行程信息管理

#### 2.3.1 获取行程信息

**接口名称**: 获取行程信息  
**接口路径**: `GET /api/travel/guide/travel-info`  
**请求方法**: GET  
**接口描述**: 获取用户的行程基本信息，包括目的地、日期、描述等详细信息  
**是否需要认证**: 否  
**接口耗时**: 平均100ms，峰值200ms  
**代码位置**: [`TravelGuideController.getTravelInfo()`](backend/src/main/java/com/tourism/controller/TravelGuideController.java#L289-L347)

**请求参数说明**

| 参数名 | 位置 | 类型 | 必传 | 默认值 | 示例 | 说明 |
|:------|:-----|:-----|:-----|:-------|:-----|:-----|
| `userId` | Query | String | 否 | `"default_user"` | `"user123"` | 用户ID，不传则使用默认用户 |

**响应数据说明**

| 字段名 | 类型 | 说明 | 示例 |
|:-------|:-----|:-----|:-----|
| `code` | Integer | 响应状态码 | `200` |
| `message` | String | 响应消息 | `"获取成功"` |
| `data` | Object | 行程信息数据 | - |
| `data.guideName` | String | 行程名称 | `"南京古都5日深度游"` |
| `data.destination` | String | 目的地 | `"江苏南京"` |
| `data.startDate` | String | 开始日期 | `"2025-08-21"` |
| `data.endDate` | String | 结束日期 | `"2025-08-25"` |
| `data.durationDays` | Integer | 行程天数 | `5` |
| `data.durationDesc` | String | 行程时长描述 | `"5天4晚"` |
| `data.description` | String | 行程描述 | `"漫步古都南京，感受深厚的历史文化底蕴..."` |
| `data.myNotes` | String | 我的注意事项 | `"记得带充电宝和雨伞"` |

**前端调用**: 无直接调用

#### 2.3.2 更新行程信息

**接口名称**: 更新行程信息  
**接口路径**: `POST /api/travel/guide/travel-info/update`  
**请求方法**: POST  
**接口描述**: 更新用户的行程信息，支持修改行程名称、目的地、日期等基本信息  
**是否需要认证**: 否  
**接口耗时**: 平均150ms，峰值300ms  
**代码位置**: [`TravelGuideController.updateTravelInfo()`](backend/src/main/java/com/tourism/controller/TravelGuideController.java#L355-L447)

**请求参数说明**

| 参数名 | 位置 | 类型 | 必传 | 默认值 | 示例 | 说明 |
|:------|:-----|:-----|:-----|:-------|:-----|:-----|
| `userId` | Body | String | 否 | `"default_user"` | `"user123"` | 用户ID，不传则使用默认用户 |
| `travelInfo` | Body | Object | 是 | - | 见下方示例 | 行程信息数据 |

**travelInfo 参数结构示例**:
```json
{
  "guideName": "南京古都5日深度游",
  "destination": "江苏南京",
  "startDate": "2025-08-21",
  "endDate": "2025-08-25",
  "durationDays": 5,
  "durationDesc": "5天4晚",
  "description": "漫步古都南京，感受深厚的历史文化底蕴。从庄严肃穆的中山陵到繁华热闹的夫子庙，从古朴厚重的明城墙到秀美宁静的玄武湖，体验金陵城的独特魅力。"
}
```

**响应数据说明**

| 字段名 | 类型 | 说明 | 示例 |
|:-------|:-----|:-----|:-----|
| `code` | Integer | 响应状态码 | `200` |
| `message` | String | 响应消息 | `"行程信息更新成功"` |
| `data` | Object | 更新后的行程信息 | 同请求参数结构 |

**前端调用**: 无直接调用

### 2.4 指南总览接口

#### 2.4.1 获取指南总览

**接口名称**: 获取指南总览  
**接口路径**: `GET /api/travel/guide/overview`  
**请求方法**: GET  
**接口描述**: 获取完整的旅游指南信息，包含指南基本信息和用户注意事项  
**是否需要认证**: 否  
**接口耗时**: 平均200ms，峰值400ms  
**代码位置**: [`TravelGuideController.getGuideOverview()`](backend/src/main/java/com/tourism/controller/TravelGuideController.java#L126-L160)

**请求参数说明**

| 参数名 | 位置 | 类型 | 必传 | 默认值 | 示例 | 说明 |
|:------|:-----|:-----|:-----|:-------|:-----|:-----|
| `userId` | Query | String | 否 | `"default_user"` | `"user123"` | 用户ID，不传则使用默认用户 |

**响应数据说明**

| 字段名 | 类型 | 说明 | 示例 |
|:-------|:-----|:-----|:-----|
| `code` | Integer | 响应状态码 | `200` |
| `message` | String | 响应消息 | `"获取成功"` |
| `data` | Object | 指南总览数据 | - |
| `data.guide` | Object | 旅游指南信息 | 包含指南基本信息 |
| `data.myNotes` | String | 我的注意事项 | `"记得带充电宝和雨伞"` |

**前端调用**: 无直接调用

### 2.5 系统监控接口

#### 2.5.1 指南服务健康检查

**接口名称**: 指南服务健康检查  
**接口路径**: `GET /api/travel/guide/health`  
**请求方法**: GET  
**接口描述**: 检查旅游指南服务运行状态，用于系统监控和运维  
**是否需要认证**: 否  
**接口耗时**: 平均50ms，峰值100ms  
**代码位置**: [`TravelGuideController.healthCheck()`](backend/src/main/java/com/tourism/controller/TravelGuideController.java#L787-L794)

**请求参数说明**

无参数

**响应数据说明**

| 字段名 | 类型 | 说明 | 示例 |
|:-------|:-----|:-----|:-----|
| `success` | Boolean | 服务状态 | `true` |
| `message` | String | 状态消息 | `"旅游指南服务运行正常"` |
| `timestamp` | Long | 时间戳 | `1640995200000` |

**前端调用**: 无直接调用

---

## 三、行程管理模块 (Travel Itinerary Module)

**📁 代码位置**: [`TravelItineraryController.java`](backend/src/main/java/com/tourism/controller/TravelItineraryController.java)

### 3.1 行程CRUD操作

#### 3.1.1 保存或更新行程

**接口名称**: 保存或更新行程  
**接口路径**: `POST /api/travel/itinerary`  
**请求方法**: POST  
**接口描述**: 保存或更新旅游行程，支持景点信息自动生成和关联管理  
**是否需要认证**: 否  
**接口耗时**: 平均300ms，峰值600ms  
**代码位置**: [`TravelItineraryController.saveOrUpdateItinerary()`](backend/src/main/java/com/tourism/controller/TravelItineraryController.java#L44-L82)

**请求参数说明**

| 参数名 | 位置 | 类型 | 必传 | 默认值 | 示例 | 说明 |
|:------|:-----|:-----|:-----|:-------|:-----|:-----|
| `userId` | Body | String | 是 | - | `"user123"` | 用户ID |
| `travelDate` | Body | String | 是 | - | `"2025-08-21"` | 旅行日期，格式：yyyy-MM-dd |
| `dayNumber` | Body | Integer | 是 | - | `1` | 第几天，必须大于0 |
| `weatherCondition` | Body | String | 否 | - | `"晴"` | 天气状况 |
| `weatherIcon` | Body | String | 否 | - | `"☀️"` | 天气图标 |
| `temperatureMin` | Body | Integer | 否 | - | `24` | 最低温度 |
| `temperatureMax` | Body | Integer | 否 | - | `30` | 最高温度 |
| `notes` | Body | String | 否 | - | `"记得带充电宝"` | 今日注意事项 |
| `travelFeelings` | Body | String | 否 | - | `"今天玩得很开心"` | 旅行感受 |
| `attractions` | Body | Array | 否 | - | 见下方示例 | 景点列表 |

**attractions 参数结构示例**:
```json
[
  {
    "name": "中山陵",
    "description": "中国近代伟大的民主革命先行者孙中山先生的陵寝",
    "icon": "🏛️",
    "duration": "2-3小时",
    "tags": "历史遗迹,文化景点",
    "tips": "建议穿舒适的鞋子，注意防晒",
    "transportationInfo": "可乘坐地铁2号线到苜蓿园站",
    "address": "江苏省南京市玄武区",
    "sequenceOrder": 1,
    "visitDuration": "2小时",
    "visitNotes": "上午9点到达，避开人流高峰",
    "transportationFromPrevious": "步行10分钟"
  }
]
```

**响应数据说明**

| 字段名 | 类型 | 说明 | 示例 |
|:-------|:-----|:-----|:-----|
| `success` | Boolean | 操作是否成功 | `true` |
| `message` | String | 响应消息 | `"行程保存成功"` |
| `data` | Object | 保存后的行程数据 | - |
| `data.id` | Long | 行程ID | `123` |
| `data.userId` | String | 用户ID | `"user123"` |
| `data.travelDate` | String | 旅行日期 | `"2025-08-21"` |
| `data.dayNumber` | Integer | 第几天 | `1` |
| `data.attractions` | Array | 景点列表 | 包含完整景点信息 |

**前端调用**: 无直接调用

#### 3.1.2 根据ID获取行程

**接口名称**: 根据ID获取行程  
**接口路径**: `GET /api/travel/itinerary/{id}`  
**请求方法**: GET  
**接口描述**: 根据行程ID获取详细的行程信息，包含景点列表和照片  
**是否需要认证**: 否  
**接口耗时**: 平均180ms，峰值350ms  
**代码位置**: [`TravelItineraryController.getItineraryById()`](backend/src/main/java/com/tourism/controller/TravelItineraryController.java#L90-L113)

**请求参数说明**

| 参数名 | 位置 | 类型 | 必传 | 默认值 | 示例 | 说明 |
|:------|:-----|:-----|:-----|:-------|:-----|:-----|
| `id` | Path | Long | 是 | - | `123` | 行程ID，必须为正整数 |

**响应数据说明**

| 字段名 | 类型 | 说明 | 示例 |
|:-------|:-----|:-----|:-----|
| `success` | Boolean | 操作是否成功 | `true` |
| `message` | String | 响应消息 | `"获取行程成功"` |
| `data` | Object | 行程详细信息 | - |
| `data.id` | Long | 行程ID | `123` |
| `data.userId` | String | 用户ID | `"user123"` |
| `data.travelDate` | String | 旅行日期 | `"2025-08-21"` |
| `data.dayNumber` | Integer | 第几天 | `1` |
| `data.weatherCondition` | String | 天气状况 | `"晴"` |
| `data.weatherIcon` | String | 天气图标 | `"☀️"` |
| `data.temperatureMin` | Integer | 最低温度 | `24` |
| `data.temperatureMax` | Integer | 最高温度 | `30` |
| `data.notes` | String | 注意事项 | `"记得带充电宝"` |
| `data.travelFeelings` | String | 旅行感受 | `"今天玩得很开心"` |
| `data.photoList` | Array | 照片列表 | `["photo1.jpg", "photo2.jpg"]` |
| `data.attractions` | Array | 景点列表 | 包含完整景点信息 |

**前端调用**: 无直接调用

#### 3.1.3 根据用户和日期获取行程

**接口名称**: 根据用户和日期获取行程  
**接口路径**: `GET /api/travel/itinerary/user/{userId}/date/{travelDate}`  
**请求方法**: GET  
**接口描述**: 根据用户ID和旅行日期获取对应的行程信息，用于查询特定日期的行程安排  
**是否需要认证**: 否  
**接口耗时**: 平均160ms，峰值300ms  
**代码位置**: [`TravelItineraryController.getItineraryByUserIdAndDate()`](backend/src/main/java/com/tourism/controller/TravelItineraryController.java#L122-L147)

**请求参数说明**

| 参数名 | 位置 | 类型 | 必传 | 默认值 | 示例 | 说明 |
|:------|:-----|:-----|:-----|:-------|:-----|:-----|
| `userId` | Path | String | 是 | - | `"user123"` | 用户ID |
| `travelDate` | Path | String | 是 | - | `"2025-08-21"` | 旅行日期，格式：yyyy-MM-dd |

**响应数据说明**

| 字段名 | 类型 | 说明 | 示例 |
|:-------|:-----|:-----|:-----|
| `success` | Boolean | 操作是否成功 | `true` |
| `message` | String | 响应消息 | `"获取行程成功"` |
| `data` | Object | 行程详细信息 | 同3.1.2接口响应结构 |

**前端调用**: 无直接调用

#### 3.1.4 根据用户和天数获取行程

**接口名称**: 根据用户和天数获取行程  
**接口路径**: `GET /api/travel/itinerary/user/{userId}/day/{dayNumber}`  
**请求方法**: GET  
**接口描述**: 根据用户ID和行程天数获取对应的行程信息，用于查询第几天的行程安排  
**是否需要认证**: 否  
**接口耗时**: 平均160ms，峰值300ms  
**代码位置**: [`TravelItineraryController.getItineraryByUserIdAndDayNumber()`](backend/src/main/java/com/tourism/controller/TravelItineraryController.java#L156-L181)

**请求参数说明**

| 参数名 | 位置 | 类型 | 必传 | 默认值 | 示例 | 说明 |
|:------|:-----|:-----|:-----|:-------|:-----|:-----|
| `userId` | Path | String | 是 | - | `"user123"` | 用户ID |
| `dayNumber` | Path | Integer | 是 | - | `1` | 第几天，必须为正整数 |


**前端调用**: 无直接调用

#### 3.1.5 获取用户所有行程

**接口名称**: 获取用户所有行程  
**接口路径**: `GET /api/travel/itinerary/user/{userId}`  
**请求方法**: GET  
**接口描述**: 获取指定用户的所有行程列表，支持分页和排序  
**是否需要认证**: 否  
**接口耗时**: 平均200ms，峰值400ms  
**代码位置**: [`TravelItineraryController.getItinerariesByUserId()`](backend/src/main/java/com/tourism/controller/TravelItineraryController.java#L189-L208)

**请求参数说明**

| 参数名 | 位置 | 类型 | 必传 | 默认值 | 示例 | 说明 |
|:------|:-----|:-----|:-----|:-------|:-----|:-----|
| `userId` | Path | String | 是 | - | `"user123"` | 用户ID |

**响应数据说明**

| 字段名 | 类型 | 说明 | 示例 |
|:-------|:-----|:-----|:-----|
| `success` | Boolean | 操作是否成功 | `true` |
| `message` | String | 响应消息 | `"获取行程列表成功"` |
| `data` | Array | 行程列表 | 包含多个行程对象 |

**前端调用**: 无直接调用

#### 3.1.6 删除行程

**接口名称**: 删除行程  
**接口路径**: `DELETE /api/travel/itinerary/{id}`  
**请求方法**: DELETE  
**接口描述**: 根据行程ID删除对应的行程记录，同时删除关联的景点信息  
**是否需要认证**: 否  
**接口耗时**: 平均200ms，峰值400ms  
**代码位置**: [`TravelItineraryController.deleteItinerary()`](backend/src/main/java/com/tourism/controller/TravelItineraryController.java#L216-L238)

**请求参数说明**

| 参数名 | 位置 | 类型 | 必传 | 默认值 | 示例 | 说明 |
|:------|:-----|:-----|:-----|:-------|:-----|:-----|
| `id` | Path | Long | 是 | - | `123` | 行程ID，必须为正整数 |

**响应数据说明**

| 字段名 | 类型 | 说明 | 示例 |
|:-------|:-----|:-----|:-----|
| `success` | Boolean | 操作是否成功 | `true` |
| `message` | String | 响应消息 | `"删除行程成功"` |

**前端调用**: 无直接调用

### 3.2 旅游感受管理

#### 3.2.1 保存旅游感受

**接口名称**: 保存旅游感受  
**接口路径**: `POST /api/travel/itinerary/feelings`  
**请求方法**: POST  
**接口描述**: 保存用户的旅游感受记录，支持按天数和用户进行管理  
**是否需要认证**: 否  
**接口耗时**: 平均150ms，峰值300ms  
**代码位置**: [`TravelItineraryController.saveTravelFeelings()`](backend/src/main/java/com/tourism/controller/TravelItineraryController.java#L247-L286)

**请求参数说明**

| 参数名 | 位置 | 类型 | 必传 | 默认值 | 示例 | 说明 |
|:------|:-----|:-----|:-----|:-------|:-----|:-----|
| `userId` | Body | String | 是 | - | `"user123"` | 用户ID |
| `dayNumber` | Body | Integer | 是 | - | `1` | 第几天，必须为正整数 |
| `content` | Body | String | 是 | - | `"今天玩得很开心"` | 感受内容，支持多行文本 |

**响应数据说明**

| 字段名 | 类型 | 说明 | 示例 |
|:-------|:-----|:-----|:-----|
| `success` | Boolean | 操作是否成功 | `true` |
| `message` | String | 响应消息 | `"旅游感受保存成功"` |
| `data` | Object | 保存后的感受数据 | - |
| `data.id` | Long | 感受记录ID | `456` |
| `data.userId` | String | 用户ID | `"user123"` |
| `data.dayNumber` | Integer | 第几天 | `1` |
| `data.content` | String | 感受内容 | `"今天玩得很开心"` |

**前端调用**: 无直接调用

#### 3.2.2 获取旅游感受

**接口名称**: 获取旅游感受  
**接口路径**: `GET /api/travel/itinerary/feelings/{dayNumber}`  
**请求方法**: GET  
**接口描述**: 获取指定天数的旅游感受内容，支持按用户筛选  
**是否需要认证**: 否  
**接口耗时**: 平均120ms，峰值250ms  
**代码位置**: [`TravelItineraryController.getTravelFeelings()`](backend/src/main/java/com/tourism/controller/TravelItineraryController.java#L296-L330)

**请求参数说明**

| 参数名 | 位置 | 类型 | 必传 | 默认值 | 示例 | 说明 |
|:------|:-----|:-----|:-----|:-------|:-----|:-----|
| `dayNumber` | Path | Integer | 是 | - | `1` | 第几天，必须为正整数 |
| `userId` | Query | String | 否 | `"default_user"` | `"user123"` | 用户ID，不传则使用默认用户 |

**响应数据说明**

| 字段名 | 类型 | 说明 | 示例 |
|:-------|:-----|:-----|:-----|
| `success` | Boolean | 操作是否成功 | `true` |
| `message` | String | 响应消息 | `"获取旅游感受成功"` |
| `data` | String | 感受内容 | `"今天玩得很开心"` |

**前端调用**: 无直接调用

#### 3.2.3 删除旅游感受

**接口名称**: 删除旅游感受  
**接口路径**: `DELETE /api/travel/itinerary/feelings/{dayNumber}`  
**请求方法**: DELETE  
**接口描述**: 删除指定天数的旅游感受记录  
**是否需要认证**: 否  
**接口耗时**: 平均100ms，峰值200ms  
**代码位置**: [`TravelItineraryController.deleteTravelFeelings()`](backend/src/main/java/com/tourism/controller/TravelItineraryController.java#L340-L372)

**请求参数说明**

| 参数名 | 位置 | 类型 | 必传 | 默认值 | 示例 | 说明 |
|:------|:-----|:-----|:-----|:-------|:-----|:-----|
| `dayNumber` | Path | Integer | 是 | - | `1` | 第几天，必须为正整数 |
| `userId` | Query | String | 否 | `"default_user"` | `"user123"` | 用户ID，不传则使用默认用户 |

**响应数据说明**

| 字段名 | 类型 | 说明 | 示例 |
|:-------|:-----|:-----|:-----|
| `success` | Boolean | 操作是否成功 | `true` |
| `message` | String | 响应消息 | `"旅游感受删除成功"` |

**前端调用**: 无直接调用

### 3.3 照片管理

#### 3.3.1 添加照片

**接口名称**: 添加照片  
**接口路径**: `POST /api/travel/itinerary/{id}/photos`  
**请求方法**: POST  
**接口描述**: 为指定行程添加照片，支持文件上传和URL添加两种方式  
**是否需要认证**: 否  
**接口耗时**: 平均300ms，峰值600ms  
**代码位置**: [`TravelItineraryController.addPhoto()`](backend/src/main/java/com/tourism/controller/TravelItineraryController.java#L381-L431)

**请求参数说明**

| 参数名 | 位置 | 类型 | 必传 | 默认值 | 示例 | 说明 |
|:------|:-----|:-----|:-----|:-------|:-----|:-----|
| `id` | Path | Long | 是 | - | `123` | 行程ID，必须为正整数 |
| `file` | Body | File | 否 | - | 文件对象 | 上传的照片文件（multipart/form-data） |
| `photoUrl` | Body | String | 否 | - | `"https://example.com/photo.jpg"` | 照片URL地址 |

**响应数据说明**

| 字段名 | 类型 | 说明 | 示例 |
|:-------|:-----|:-----|:-----|
| `success` | Boolean | 操作是否成功 | `true` |
| `message` | String | 响应消息 | `"照片添加成功"` |
| `data` | Object | 添加结果 | - |
| `data.photoUrl` | String | 照片URL | `"https://example.com/photo.jpg"` |

**前端调用**: 无直接调用

#### 3.3.2 删除照片

**接口名称**: 删除照片  
**接口路径**: `DELETE /api/travel/itinerary/{id}/photos`  
**请求方法**: DELETE  
**接口描述**: 删除指定行程的照片，支持按URL删除特定照片  
**是否需要认证**: 否  
**接口耗时**: 平均150ms，峰值300ms  
**代码位置**: [`TravelItineraryController.removePhoto()`](backend/src/main/java/com/tourism/controller/TravelItineraryController.java#L440-L471)

**请求参数说明**

| 参数名 | 位置 | 类型 | 必传 | 默认值 | 示例 | 说明 |
|:------|:-----|:-----|:-----|:-------|:-----|:-----|
| `id` | Path | Long | 是 | - | `123` | 行程ID，必须为正整数 |
| `photoUrl` | Body | String | 是 | - | `"https://example.com/photo.jpg"` | 要删除的照片URL |

**响应数据说明**

| 字段名 | 类型 | 说明 | 示例 |
|:-------|:-----|:-----|:-----|
| `success` | Boolean | 操作是否成功 | `true` |
| `message` | String | 响应消息 | `"照片删除成功"` |

**前端调用**: 无直接调用

#### 3.3.3 更新照片列表

**接口名称**: 更新照片列表  
**接口路径**: `PUT /api/travel/itinerary/{id}/photos`  
**请求方法**: PUT  
**接口描述**: 批量更新行程的照片列表，替换现有照片  
**是否需要认证**: 否  
**接口耗时**: 平均200ms，峰值400ms  
**代码位置**: [`TravelItineraryController.updatePhotos()`](backend/src/main/java/com/tourism/controller/TravelItineraryController.java#L480-L510)

**请求参数说明**

| 参数名 | 位置 | 类型 | 必传 | 默认值 | 示例 | 说明 |
|:------|:-----|:-----|:-----|:-------|:-----|:-----|
| `id` | Path | Long | 是 | - | `123` | 行程ID，必须为正整数 |
| `photoUrls` | Body | Array | 是 | - | `["url1.jpg", "url2.jpg"]` | 照片URL列表 |

**响应数据说明**

| 字段名 | 类型 | 说明 | 示例 |
|:-------|:-----|:-----|:-----|
| `success` | Boolean | 操作是否成功 | `true` |
| `message` | String | 响应消息 | `"照片列表更新成功"` |
| `data` | Array | 更新后的照片列表 | `["url1.jpg", "url2.jpg"]` |

**前端调用**: 无直接调用

#### 3.3.4 清空所有照片

**接口名称**: 清空所有照片  
**接口路径**: `DELETE /api/travel/itinerary/{id}/photos/all`  
**请求方法**: DELETE  
**接口描述**: 清空指定行程的所有照片记录  
**是否需要认证**: 否  
**接口耗时**: 平均100ms，峰值200ms  
**代码位置**: [`TravelItineraryController.clearAllPhotos()`](backend/src/main/java/com/tourism/controller/TravelItineraryController.java#L518-L541)

**请求参数说明**

| 参数名 | 位置 | 类型 | 必传 | 默认值 | 示例 | 说明 |
|:------|:-----|:-----|:-----|:-------|:-----|:-----|
| `id` | Path | Long | 是 | - | `123` | 行程ID，必须为正整数 |

**响应数据说明**

| 字段名 | 类型 | 说明 | 示例 |
|:-------|:-----|:-----|:-----|
| `success` | Boolean | 操作是否成功 | `true` |
| `message` | String | 响应消息 | `"所有照片已清空"` |

**前端调用**: 无直接调用

#### 3.3.5 获取照片列表

**接口名称**: 获取照片列表  
**接口路径**: `GET /api/travel/itinerary/{id}/photos`  
**请求方法**: GET  
**接口描述**: 获取指定行程的所有照片URL列表  
**是否需要认证**: 否  
**接口耗时**: 平均80ms，峰值150ms  
**代码位置**: [`TravelItineraryController.getPhotos()`](backend/src/main/java/com/tourism/controller/TravelItineraryController.java#L549-L569)

**请求参数说明**

| 参数名 | 位置 | 类型 | 必传 | 默认值 | 示例 | 说明 |
|:------|:-----|:-----|:-----|:-------|:-----|:-----|
| `id` | Path | Long | 是 | - | `123` | 行程ID，必须为正整数 |

**响应数据说明**

| 字段名 | 类型 | 说明 | 示例 |
|:-------|:-----|:-----|:-----|
| `success` | Boolean | 操作是否成功 | `true` |
| `message` | String | 响应消息 | `"获取照片列表成功"` |
| `data` | Array | 照片URL列表 | `["url1.jpg", "url2.jpg"]` |

**前端调用**: 无直接调用

### 3.4 系统监控接口

#### 3.4.1 行程服务健康检查

**接口名称**: 行程服务健康检查  
**接口路径**: `GET /api/travel/itinerary/health`  
**请求方法**: GET  
**接口描述**: 检查旅游行程服务运行状态，用于系统监控和运维  
**是否需要认证**: 否  
**接口耗时**: 平均50ms，峰值100ms  
**代码位置**: [`TravelItineraryController.healthCheck()`](backend/src/main/java/com/tourism/controller/TravelItineraryController.java#L576-L583)

**请求参数说明**

无参数

**响应数据说明**

| 字段名 | 类型 | 说明 | 示例 |
|:-------|:-----|:-----|:-----|
| `success` | Boolean | 服务状态 | `true` |
| `message` | String | 状态消息 | `"旅游行程服务运行正常"` |
| `timestamp` | Long | 时间戳 | `1640995200000` |

**前端调用**: 无直接调用

---

## 四、天气服务模块 (Weather Module)

**📁 代码位置**: [`WeatherController.java`](backend/src/main/java/com/tourism/controller/WeatherController.java)

### 4.1 天气预报接口

#### 4.1.1 获取天气预报

**接口名称**: 获取天气预报  
**接口路径**: `GET /api/weather/forecast`  
**请求方法**: GET  
**接口描述**: 获取指定城市的天气预报信息，支持日期范围筛选  
**是否需要认证**: 否  
**接口耗时**: 平均300ms，峰值600ms  
**代码位置**: [`WeatherController.getWeatherForecast()`](backend/src/main/java/com/tourism/controller/WeatherController.java#L40-L76)

**请求参数说明**

| 参数名 | 位置 | 类型 | 必传 | 默认值 | 示例 | 说明 |
|:------|:-----|:-----|:-----|:-------|:-----|:-----|
| `city` | Query | String | 是 | - | `"南京"` | 城市名称 |
| `startDate` | Query | String | 否 | - | `"2025-08-21"` | 开始日期，格式：yyyy-MM-dd |
| `endDate` | Query | String | 否 | - | `"2025-08-25"` | 结束日期，格式：yyyy-MM-dd |

**响应数据说明**

| 字段名 | 类型 | 说明 | 示例 |
|:-------|:-----|:-----|:-----|
| `success` | Boolean | 操作是否成功 | `true` |
| `message` | String | 响应消息 | `"获取天气预报成功"` |
| `data` | Array | 天气预报数据列表 | - |
| `data[].date` | String | 日期 | `"2025-08-21"` |
| `data[].weather` | String | 天气状况 | `"晴"` |
| `data[].temperature` | String | 温度范围 | `"24°C ~ 30°C"` |
| `data[].icon` | String | 天气图标 | `"☀️"` |

**前端调用**: 无直接调用

#### 4.1.2 根据行程获取天气预报

**接口名称**: 根据行程获取天气预报  
**接口路径**: `GET /api/weather/forecast/itinerary/{itineraryId}`  
**请求方法**: GET  
**接口描述**: 根据行程ID获取相关城市的天气预报信息  
**是否需要认证**: 否  
**接口耗时**: 平均350ms，峰值700ms  
**代码位置**: [`WeatherController.getWeatherForecastByItinerary()`](backend/src/main/java/com/tourism/controller/WeatherController.java#L84-L123)

**请求参数说明**

| 参数名 | 位置 | 类型 | 必传 | 默认值 | 示例 | 说明 |
|:------|:-----|:-----|:-----|:-------|:-----|:-----|
| `itineraryId` | Path | Long | 是 | - | `123` | 行程ID，必须为正整数 |

**响应数据说明**

| 字段名 | 类型 | 说明 | 示例 |
|:-------|:-----|:-----|:-----|
| `success` | Boolean | 操作是否成功 | `true` |
| `message` | String | 响应消息 | `"获取天气预报成功"` |
| `data` | Array | 天气预报数据列表 | 同4.1.1接口响应结构 |

**前端调用**: 无直接调用

---

## 五、天气建议模块 (Weather Suggestion Module)

**📁 代码位置**: [`WeatherSuggestionController.java`](backend/src/main/java/com/tourism/controller/WeatherSuggestionController.java)

### 5.1 天气建议查询

#### 5.1.1 获取所有启用的天气建议

**接口名称**: 获取所有启用的天气建议  
**接口路径**: `GET /api/weather/suggestions`  
**请求方法**: GET  
**接口描述**: 获取所有启用状态的天气建议，用于前端展示天气相关建议  
**是否需要认证**: 否  
**接口耗时**: 平均120ms，峰值250ms  
**代码位置**: [`WeatherSuggestionController.getAllSuggestions()`](backend/src/main/java/com/tourism/controller/WeatherSuggestionController.java#L36-L53)

**请求参数说明**

无参数

**响应数据说明**

| 字段名 | 类型 | 说明 | 示例 |
|:-------|:-----|:-----|:-----|
| `success` | Boolean | 操作是否成功 | `true` |
| `data` | Array | 天气建议列表 | - |
| `data[].id` | Long | 建议ID | `1` |
| `data[].suggestionType` | String | 建议类型 | `"防暑降温"` |
| `data[].weatherCondition` | String | 适用天气条件 | `"高温"` |
| `data[].content` | String | 建议内容 | `"👕\n天气较热，注意防暑降温"` |
| `data[].priority` | Integer | 优先级 | `1` |
| `data[].isActive` | Boolean | 是否启用 | `true` |
| `message` | String | 响应消息 | `"获取天气建议成功"` |

**前端调用**: [`weatherApi.getSuggestions()`](frontend/frontend/src/services/api/weatherApi.js#L4-L10)

#### 5.1.2 根据ID获取天气建议

**接口名称**: 根据ID获取天气建议  
**接口路径**: `GET /api/weather/suggestions/{id}`  
**请求方法**: GET  
**接口描述**: 根据建议ID获取天气建议的详细信息  
**是否需要认证**: 否  
**接口耗时**: 平均80ms，峰值150ms  
**代码位置**: [`WeatherSuggestionController.getSuggestionById()`](backend/src/main/java/com/tourism/controller/WeatherSuggestionController.java#L61-L85)

**请求参数说明**

| 参数名 | 位置 | 类型 | 必传 | 默认值 | 示例 | 说明 |
|:------|:-----|:-----|:-----|:-------|:-----|:-----|
| `id` | Path | Long | 是 | - | `1` | 建议ID，必须为正整数 |

**响应数据说明**

| 字段名 | 类型 | 说明 | 示例 |
|:-------|:-----|:-----|:-----|
| `success` | Boolean | 操作是否成功 | `true` |
| `data` | Object | 天气建议详情 | - |
| `data.id` | Long | 建议ID | `1` |
| `data.suggestionType` | String | 建议类型 | `"防暑降温"` |
| `data.weatherCondition` | String | 适用天气条件 | `"高温"` |
| `data.content` | String | 建议内容 | `"👕\n天气较热，注意防暑降温"` |
| `data.priority` | Integer | 优先级 | `1` |
| `data.isActive` | Boolean | 是否启用 | `true` |
| `message` | String | 响应消息 | `"获取天气建议成功"` |

**前端调用**: 无直接调用

#### 5.1.3 根据类型获取天气建议

**接口名称**: 根据类型获取天气建议  
**接口路径**: `GET /api/weather/suggestions/type/{type}`  
**请求方法**: GET  
**接口描述**: 根据建议类型筛选天气建议，返回符合条件的建议列表  
**是否需要认证**: 否  
**接口耗时**: 平均100ms，峰值200ms  
**代码位置**: [`WeatherSuggestionController.getSuggestionsByType()`](backend/src/main/java/com/tourism/controller/WeatherSuggestionController.java#L93-L111)

**请求参数说明**

| 参数名 | 位置 | 类型 | 必传 | 默认值 | 示例 | 说明 |
|:------|:-----|:-----|:-----|:-------|:-----|:-----|
| `type` | Path | String | 是 | - | `"防暑降温"` | 建议类型 |

**响应数据说明**

| 字段名 | 类型 | 说明 | 示例 |
|:-------|:-----|:-----|:-----|
| `success` | Boolean | 操作是否成功 | `true` |
| `data` | Array | 天气建议列表 | 同5.1.1接口响应结构 |
| `message` | String | 响应消息 | `"获取天气建议成功"` |

**前端调用**: [`weatherApi.getSuggestionsByType(type)`](frontend/frontend/src/services/api/weatherApi.js)

#### 5.1.4 根据天气条件获取建议

**接口名称**: 根据天气条件获取建议  
**接口路径**: `GET /api/weather/suggestions/weather/{weather}`  
**请求方法**: GET  
**接口描述**: 根据天气条件筛选相应的建议，返回适用的建议列表  
**是否需要认证**: 否  
**接口耗时**: 平均100ms，峰值200ms  
**代码位置**: [`WeatherSuggestionController.getSuggestionsByWeather()`](backend/src/main/java/com/tourism/controller/WeatherSuggestionController.java#L119-L137)

**请求参数说明**

| 参数名 | 位置 | 类型 | 必传 | 默认值 | 示例 | 说明 |
|:------|:-----|:-----|:-----|:-------|:-----|:-----|
| `weather` | Path | String | 是 | - | `"高温"` | 天气条件 |

**响应数据说明**

| 字段名 | 类型 | 说明 | 示例 |
|:-------|:-----|:-----|:-----|
| `success` | Boolean | 操作是否成功 | `true` |
| `data` | Array | 天气建议列表 | 同5.1.1接口响应结构 |
| `message` | String | 响应消息 | `"获取天气建议成功"` |

**前端调用**: [`weatherApi.getSuggestionsByWeather(weather)`](frontend/frontend/src/services/api/weatherApi.js)

#### 5.1.5 搜索天气建议

**接口名称**: 搜索天气建议  
**接口路径**: `GET /api/weather/suggestions/search`  
**请求方法**: GET  
**接口描述**: 根据关键词搜索天气建议，支持模糊匹配  
**是否需要认证**: 否  
**接口耗时**: 平均120ms，峰值250ms  
**代码位置**: [`WeatherSuggestionController.searchSuggestions()`](backend/src/main/java/com/tourism/controller/WeatherSuggestionController.java#L145-L163)

**请求参数说明**

| 参数名 | 位置 | 类型 | 必传 | 默认值 | 示例 | 说明 |
|:------|:-----|:-----|:-----|:-------|:-----|:-----|
| `keyword` | Query | String | 是 | - | `"防暑"` | 搜索关键词 |

**响应数据说明**

| 字段名 | 类型 | 说明 | 示例 |
|:-------|:-----|:-----|:-----|
| `success` | Boolean | 操作是否成功 | `true` |
| `data` | Array | 天气建议列表 | 同5.1.1接口响应结构 |
| `message` | String | 响应消息 | `"搜索成功"` |

**前端调用**: [`weatherApi.searchSuggestions(keyword)`](frontend/frontend/src/services/api/weatherApi.js)

### 5.2 天气建议管理

#### 5.2.1 添加天气建议

**接口名称**: 添加天气建议  
**接口路径**: `POST /api/weather/suggestions`  
**请求方法**: POST  
**接口描述**: 添加新的天气建议记录，支持自定义建议内容  
**是否需要认证**: 否  
**接口耗时**: 平均150ms，峰值300ms  
**代码位置**: [`WeatherSuggestionController.addSuggestion()`](backend/src/main/java/com/tourism/controller/WeatherSuggestionController.java#L171-L195)

**请求参数说明**

| 参数名 | 位置 | 类型 | 必传 | 默认值 | 示例 | 说明 |
|:------|:-----|:-----|:-----|:-------|:-----|:-----|
| `suggestionType` | Body | String | 是 | - | `"防暑降温"` | 建议类型 |
| `weatherCondition` | Body | String | 是 | - | `"高温"` | 适用天气条件 |
| `content` | Body | String | 是 | - | `"👕\n天气较热，注意防暑降温"` | 建议内容 |
| `priority` | Body | Integer | 否 | `1` | `1` | 优先级，数字越小优先级越高 |
| `isActive` | Body | Boolean | 否 | `true` | `true` | 是否启用 |

**响应数据说明**

| 字段名 | 类型 | 说明 | 示例 |
|:-------|:-----|:-----|:-----|
| `success` | Boolean | 操作是否成功 | `true` |
| `data` | Object | 添加后的建议数据 | 同5.1.2接口响应结构 |
| `message` | String | 响应消息 | `"天气建议添加成功"` |

**前端调用**: [`weatherApi.addSuggestion(suggestion)`](frontend/frontend/src/services/api/weatherApi.js)

#### 5.2.2 更新天气建议

**接口名称**: 更新天气建议  
**接口路径**: `PUT /api/weather/suggestions/{id}`  
**请求方法**: PUT  
**接口描述**: 更新指定ID的天气建议信息  
**是否需要认证**: 否  
**接口耗时**: 平均150ms，峰值300ms  
**代码位置**: [`WeatherSuggestionController.updateSuggestion()`](backend/src/main/java/com/tourism/controller/WeatherSuggestionController.java#L204-L230)

**请求参数说明**

| 参数名 | 位置 | 类型 | 必传 | 默认值 | 示例 | 说明 |
|:------|:-----|:-----|:-----|:-------|:-----|:-----|
| `id` | Path | Long | 是 | - | `1` | 建议ID，必须为正整数 |
| `suggestionType` | Body | String | 否 | - | `"防暑降温"` | 建议类型 |
| `weatherCondition` | Body | String | 否 | - | `"高温"` | 适用天气条件 |
| `content` | Body | String | 否 | - | `"👕\n天气较热，注意防暑降温"` | 建议内容 |
| `priority` | Body | Integer | 否 | - | `1` | 优先级 |
| `isActive` | Body | Boolean | 否 | - | `true` | 是否启用 |

**响应数据说明**

| 字段名 | 类型 | 说明 | 示例 |
|:-------|:-----|:-----|:-----|
| `success` | Boolean | 操作是否成功 | `true` |
| `data` | Object | 更新后的建议数据 | 同5.1.2接口响应结构 |
| `message` | String | 响应消息 | `"天气建议更新成功"` |

**前端调用**: [`weatherApi.updateSuggestion(id, suggestion)`](frontend/frontend/src/services/api/weatherApi.js)

#### 5.2.3 删除天气建议

**接口名称**: 删除天气建议  
**接口路径**: `DELETE /api/weather/suggestions/{id}`  
**请求方法**: DELETE  
**接口描述**: 删除指定ID的天气建议记录  
**是否需要认证**: 否  
**接口耗时**: 平均100ms，峰值200ms  
**代码位置**: [`WeatherSuggestionController.deleteSuggestion()`](backend/src/main/java/com/tourism/controller/WeatherSuggestionController.java#L238-L261)

**请求参数说明**

| 参数名 | 位置 | 类型 | 必传 | 默认值 | 示例 | 说明 |
|:------|:-----|:-----|:-----|:-------|:-----|:-----|
| `id` | Path | Long | 是 | - | `1` | 建议ID，必须为正整数 |

**响应数据说明**

| 字段名 | 类型 | 说明 | 示例 |
|:-------|:-----|:-----|:-----|
| `success` | Boolean | 操作是否成功 | `true` |
| `message` | String | 响应消息 | `"天气建议删除成功"` |

**前端调用**: [`weatherApi.deleteSuggestion(id)`](frontend/frontend/src/services/api/weatherApi.js)

#### 5.2.4 启用/禁用天气建议

**接口名称**: 启用/禁用天气建议  
**接口路径**: `PUT /api/weather/suggestions/{id}/status`  
**请求方法**: PUT  
**接口描述**: 切换天气建议的启用状态，支持单个建议的快速启用/禁用  
**是否需要认证**: 否  
**接口耗时**: 平均100ms，峰值200ms  
**代码位置**: [`WeatherSuggestionController.toggleSuggestionStatus()`](backend/src/main/java/com/tourism/controller/WeatherSuggestionController.java#L270-L301)

**请求参数说明**

| 参数名 | 位置 | 类型 | 必传 | 默认值 | 示例 | 说明 |
|:------|:-----|:-----|:-----|:-------|:-----|:-----|
| `id` | Path | Long | 是 | - | `1` | 建议ID，必须为正整数 |
| `isActive` | Body | Boolean | 是 | - | `true` | 是否启用 |

**响应数据说明**

| 字段名 | 类型 | 说明 | 示例 |
|:-------|:-----|:-----|:-----|
| `success` | Boolean | 操作是否成功 | `true` |
| `data` | Object | 更新后的建议数据 | 同5.1.2接口响应结构 |
| `message` | String | 响应消息 | `"状态更新成功"` |

**前端调用**: [`weatherApi.toggleSuggestionStatus(id, isActive)`](frontend/frontend/src/services/api/weatherApi.js)

#### 5.2.5 批量更新天气建议

**接口名称**: 批量更新天气建议  
**接口路径**: `PUT /api/weather/suggestions/batch`  
**请求方法**: PUT  
**接口描述**: 批量更新多个天气建议，提高批量操作效率  
**是否需要认证**: 否  
**接口耗时**: 平均300ms，峰值600ms  
**代码位置**: [`WeatherSuggestionController.batchUpdateSuggestions()`](backend/src/main/java/com/tourism/controller/WeatherSuggestionController.java#L309-L334)

**请求参数说明**

| 参数名 | 位置 | 类型 | 必传 | 默认值 | 示例 | 说明 |
|:------|:-----|:-----|:-----|:-------|:-----|:-----|
| `suggestions` | Body | Array | 是 | - | 见下方示例 | 天气建议对象列表 |

**suggestions 参数结构示例**:
```json
[
  {
    "id": 1,
    "suggestionType": "防暑降温",
    "weatherCondition": "高温",
    "content": "👕\n天气较热，注意防暑降温",
    "priority": 1,
    "isActive": true
  }
]
```

**响应数据说明**

| 字段名 | 类型 | 说明 | 示例 |
|:-------|:-----|:-----|:-----|
| `success` | Boolean | 操作是否成功 | `true` |
| `data` | Array | 更新后的建议列表 | - |
| `message` | String | 响应消息 | `"批量更新成功"` |

**前端调用**: [`weatherApi.batchUpdateSuggestions(suggestions)`](frontend/frontend/src/services/api/weatherApi.js)

---

## 六、POI服务模块 (POI Module)

**📁 代码位置**: [`PoiController.java`](backend/src/main/java/com/tourism/controller/PoiController.java)

### 6.1 POI搜索接口

#### 6.1.1 测试POI搜索

**接口名称**: 测试POI搜索  
**接口路径**: `GET /api/poi/test`  
**请求方法**: GET  
**接口描述**: 测试POI搜索功能，默认搜索南京著名景点，用于验证搜索功能是否正常  
**是否需要认证**: 否  
**接口耗时**: 平均500ms，峰值1000ms  
**代码位置**: [`PoiController.testPoiSearch()`](backend/src/main/java/com/tourism/controller/PoiController.java#L22-L43)

**请求参数说明**

无参数

**响应数据说明**

| 字段名 | 类型 | 说明 | 示例 |
|:-------|:-----|:-----|:-----|
| `success` | Boolean | 操作是否成功 | `true` |
| `data` | Array | POI搜索结果列表 | - |
| `message` | String | 响应消息 | `"POI搜索测试成功"` |

**前端调用**: 无直接调用

#### 6.1.2 获取POI照片

**接口名称**: 获取POI照片  
**接口路径**: `GET /api/poi/photo`  
**请求方法**: GET  
**接口描述**: 根据关键词和城市返回第一张POI照片URL，用于获取景点的代表性图片  
**是否需要认证**: 否  
**接口耗时**: 平均400ms，峰值800ms  
**代码位置**: [`PoiController.getPoiPhoto()`](backend/src/main/java/com/tourism/controller/PoiController.java#L48-L66)

**请求参数说明**

| 参数名 | 位置 | 类型 | 必传 | 默认值 | 示例 | 说明 |
|:------|:-----|:-----|:-----|:-------|:-----|:-----|
| `keyword` | Query | String | 是 | - | `"中山陵"` | 搜索关键词，通常是景点名称 |
| `city` | Query | String | 否 | - | `"南京"` | 城市名称，用于精确搜索 |

**响应数据说明**

| 字段名 | 类型 | 说明 | 示例 |
|:-------|:-----|:-----|:-----|
| `success` | Boolean | 操作是否成功 | `true` |
| `data` | String | POI照片URL | `"https://example.com/photo.jpg"` |
| `message` | String | 响应消息 | `"获取照片成功"` |

**前端调用**: 无直接调用

---

## 七、前端API调用汇总

**📁 前端API文件位置**:
- [`guideApi.js`](frontend/frontend/src/services/api/guideApi.js) - 旅游指南相关接口
- [`weatherApi.js`](frontend/frontend/src/services/api/weatherApi.js) - 天气建议相关接口
- [`request.js`](frontend/frontend/src/utils/request.js) - 请求工具封装

### 7.1 旅游指南API (guideApi.js)

#### 7.1.1 总览相关接口
- **获取总览统计数据**: `GET /overview/stats`
  - **前端调用**: [`guideApi.getStats()`](frontend/frontend/src/services/api/guideApi.js#L5-L10)
- **获取最近的路书列表**: `GET /overview/recent-guides`
  - **前端调用**: [`guideApi.getRecentGuides()`](frontend/frontend/src/services/api/guideApi.js#L12-L19)
- **搜索路书**: `GET /overview/search-guides`
  - **前端调用**: [`guideApi.searchGuides()`](frontend/frontend/src/services/api/guideApi.js#L21-L28)

#### 7.1.2 路书管理接口
- **获取路书注意事项**: `GET /guides/{guideId}`
  - **前端调用**: [`guideApi.getGuideNotes()`](frontend/frontend/src/services/api/guideApi.js#L30-L36)
- **更新路书注意事项**: `PUT /guides/{guideId}`
  - **前端调用**: [`guideApi.updateGuideNotes()`](frontend/frontend/src/services/api/guideApi.js#L38-L45)

### 7.2 天气API (weatherApi.js)

#### 7.2.1 天气建议查询接口
- **获取所有启用的天气建议**: `GET /api/weather/suggestions`
  - **前端调用**: [`weatherApi.getSuggestions()`](frontend/frontend/src/services/api/weatherApi.js#L4-L10)
- **按类型筛选天气建议**: `GET /api/weather/suggestions/type/{type}`
  - **前端调用**: [`weatherApi.getSuggestionsByType()`](frontend/frontend/src/services/api/weatherApi.js#L12-L18)
- **按天气条件筛选天气建议**: `GET /api/weather/suggestions/weather/{weather}`
  - **前端调用**: [`weatherApi.getSuggestionsByWeather()`](frontend/frontend/src/services/api/weatherApi.js#L20-L26)
- **搜索天气建议**: `GET /api/weather/suggestions/search`
  - **前端调用**: [`weatherApi.searchSuggestions()`](frontend/frontend/src/services/api/weatherApi.js#L28-L35)

#### 7.2.2 天气建议管理接口
- **添加天气建议**: `POST /api/weather/suggestions`
  - **前端调用**: [`weatherApi.addSuggestion()`](frontend/frontend/src/services/api/weatherApi.js#L37-L44)
- **更新天气建议**: `PUT /api/weather/suggestions/{id}`
  - **前端调用**: [`weatherApi.updateSuggestion()`](frontend/frontend/src/services/api/weatherApi.js#L46-L53)
- **删除天气建议**: `DELETE /api/weather/suggestions/{id}`
  - **前端调用**: [`weatherApi.deleteSuggestion()`](frontend/frontend/src/services/api/weatherApi.js#L55-L61)
- **启用/禁用天气建议**: `PUT /api/weather/suggestions/{id}/status`
  - **前端调用**: [`weatherApi.toggleSuggestionStatus()`](frontend/frontend/src/services/api/weatherApi.js#L63-L70)
- **批量更新天气建议**: `PUT /api/weather/suggestions/batch`
  - **前端调用**: [`weatherApi.batchUpdateSuggestions()`](frontend/frontend/src/services/api/weatherApi.js#L72-L79)

---

## 八、接口统计汇总

### 8.1 按模块统计

| 模块 | 接口数量 | 主要功能 |
|------|----------|----------|
| 地图路线模块 | 5个 | 地图生成、坐标管理、健康检查 |
| 旅游指南模块 | 7个 | 注意事项、准备事项、行程信息管理 |
| 行程管理模块 | 13个 | 行程CRUD、感受记录、照片管理 |
| 天气服务模块 | 2个 | 天气预报查询 |
| 天气建议模块 | 10个 | 天气建议的增删改查 |
| POI服务模块 | 2个 | POI搜索和照片获取 |

### 8.2 按HTTP方法统计

| HTTP方法 | 数量 | 用途 |
|----------|------|------|
| GET | 20个 | 查询和获取数据 |
| POST | 8个 | 创建和保存数据 |
| PUT | 6个 | 更新数据 |
| DELETE | 5个 | 删除数据 |

### 8.3 前端API调用统计

| 模块 | 前端调用接口数 | 未调用接口数 |
|------|----------------|--------------|
| 旅游指南模块 | 5个 | 2个 |
| 天气建议模块 | 8个 | 2个 |
| 其他模块 | 0个 | 29个 |

---

## 九、接口设计特点

### 9.1 统一响应格式
- 所有接口都采用统一的响应格式：`{code, message, data}`
- 成功响应code为200，失败响应code为500或其他错误码

### 9.2 跨域支持
- 所有Controller都添加了`@CrossOrigin`注解
- 支持前端跨域请求

### 9.3 参数验证
- 使用`@Valid`和`@Validated`注解进行参数验证
- 路径参数使用`@NotNull`、`@Positive`等验证注解

### 9.4 异常处理
- 统一的异常处理机制
- 详细的错误信息返回

### 9.5 健康检查
- 每个主要模块都提供健康检查接口
- 便于系统监控和运维

---

## 十、使用建议

### 10.1 前端开发建议
1. 优先使用已封装的前端API（guideApi.js, weatherApi.js）
2. 对于未封装的接口，建议参考现有API的封装方式
3. 统一使用request.js中的请求拦截器处理错误

### 10.2 后端开发建议
1. 新增接口时保持统一的响应格式
2. 添加适当的参数验证和异常处理
3. 为每个模块添加健康检查接口

### 10.3 接口测试建议
1. 使用项目中的测试脚本进行接口测试
2. 重点关注核心业务接口的测试覆盖
3. 定期进行接口性能测试

---

## 接口文档格式说明

### 标准接口格式模板

每个接口都按照以下标准格式编写：

```
#### X.X.X 接口名称

**接口名称**: 接口功能描述  
**接口路径**: `HTTP方法 /api/路径`  
**请求方法**: GET/POST/PUT/DELETE  
**接口描述**: 详细的功能说明和使用场景  
**是否需要认证**: 是/否  
**接口耗时**: 平均XXms，峰值XXms  
**代码位置**: [链接到具体代码]

**请求参数说明**

| 参数名 | 位置 | 类型 | 必传 | 默认值 | 示例 | 说明 |
|:------|:-----|:-----|:-----|:-------|:-----|:-----|
| `paramName` | Body/Query/Path | String/Integer/Array | 是/否 | - | `"example"` | 参数详细说明 |

**响应数据说明**

| 字段名 | 类型 | 说明 | 示例 |
|:-------|:-----|:-----|:-----|
| `fieldName` | String/Integer/Object | 字段说明 | `"example"` |

**前端调用**: 前端API调用方法或"无直接调用"
```

### 参数位置说明
- **Header**: 请求头参数
- **Path**: 路径参数（如 `/api/user/{id}` 中的 `{id}`）
- **Query**: 查询参数（如 `?name=value`）
- **Body**: 请求体参数（JSON格式）

### 数据类型说明
- **String**: 字符串类型
- **Integer**: 整数类型
- **Boolean**: 布尔类型
- **Array**: 数组类型
- **Object**: 对象类型
- **Date**: 日期类型

---

## 接口统计汇总

### 按模块统计

| 模块 | 接口数量 | 主要功能 |
|------|----------|----------|
| 地图路线模块 | 5个 | 地图生成、坐标管理、健康检查 |
| 旅游指南模块 | 7个 | 注意事项、准备事项、行程信息管理 |
| 行程管理模块 | 13个 | 行程CRUD、感受记录、照片管理 |
| 天气服务模块 | 2个 | 天气预报查询 |
| 天气建议模块 | 10个 | 天气建议的增删改查 |
| POI服务模块 | 2个 | POI搜索和照片获取 |

### 按HTTP方法统计

| HTTP方法 | 数量 | 用途 |
|----------|------|------|
| GET | 20个 | 查询和获取数据 |
| POST | 8个 | 创建和保存数据 |
| PUT | 6个 | 更新数据 |
| DELETE | 5个 | 删除数据 |

### 前端API调用统计

| 模块 | 前端调用接口数 | 未调用接口数 |
|------|----------------|--------------|
| 旅游指南模块 | 5个 | 2个 |
| 天气建议模块 | 8个 | 2个 |
| 其他模块 | 0个 | 29个 |

---

## 接口设计特点

### 统一响应格式
- 所有接口都采用统一的响应格式：`{success, message, data}`
- 成功响应success为true，失败响应success为false

### 跨域支持
- 所有Controller都添加了`@CrossOrigin`注解
- 支持前端跨域请求

### 参数验证
- 使用`@Valid`和`@Validated`注解进行参数验证
- 路径参数使用`@NotNull`、`@Positive`等验证注解

### 异常处理
- 统一的异常处理机制
- 详细的错误信息返回

### 健康检查
- 每个主要模块都提供健康检查接口
- 便于系统监控和运维

---

## 使用建议

### 前端开发建议
1. 优先使用已封装的前端API（guideApi.js, weatherApi.js）
2. 对于未封装的接口，建议参考现有API的封装方式
3. 统一使用request.js中的请求拦截器处理错误

### 后端开发建议
1. 新增接口时保持统一的响应格式
2. 添加适当的参数验证和异常处理
3. 为每个模块添加健康检查接口

### 接口测试建议
1. 使用项目中的测试脚本进行接口测试
2. 重点关注核心业务接口的测试覆盖
3. 定期进行接口性能测试

---

*文档生成时间：2025年1月27日*  
*版本：v2.0*  
*格式：标准化接口文档格式*
