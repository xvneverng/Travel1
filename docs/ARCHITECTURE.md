# 旅游路线规划后端服务 - 架构说明

## 项目概述

这是一个基于Spring Boot的旅游路线规划后端服务，集成了高德地图API，提供智能路线规划功能。项目采用分层架构设计，确保代码的可维护性、可扩展性和可测试性。

## 分层架构

### 1. Controller层（控制层）
**位置**: `com.tourism.controller`
**作用**: 处理HTTP请求和响应，是前后端交互的入口

#### 核心类：
- **RouteController**: 路线规划控制器
  - 处理路线规划相关的HTTP请求
  - 提供RESTful API接口
  - 负责参数验证和响应格式化

#### 职责：
- 接收HTTP请求
- 参数验证和转换
- 调用Service层处理业务逻辑
- 返回HTTP响应
- 异常处理和错误响应

### 2. Service层（服务层）
**位置**: `com.tourism.service`
**作用**: 处理业务逻辑，协调各个组件

#### 核心类：
- **RoutePlanningService**: 路线规划服务
  - 实现核心业务逻辑
  - 协调Controller层和Util层
  - 实现降级策略（真实API失败时使用模拟数据）

#### 职责：
- 业务逻辑处理
- 参数验证和业务验证
- 调用工具类进行API调用
- 数据转换和格式化
- 异常处理和降级策略

### 3. Util层（工具层）
**位置**: `com.tourism.util`
**作用**: 封装第三方API调用和通用工具方法

#### 核心类：
- **AmapApiUtil**: 高德地图API工具类
  - 封装高德地图API调用逻辑
  - 处理地理编码和路径规划
  - 数据格式转换和解析

- **MockAmapApiUtil**: 模拟API工具类
  - 提供模拟数据用于测试
  - 当真实API不可用时作为降级方案
  - 支持常见城市的地点识别

#### 职责：
- 第三方API调用
- 数据格式转换
- 异常处理和重试
- 提供降级方案

### 4. Entity层（实体层）
**位置**: `com.tourism.entity`
**作用**: 定义数据模型，表示业务对象

#### 核心类：
- **Location**: 地点实体
  - 表示地理位置信息
  - 包含坐标、地址、城市等信息

- **Route**: 路线实体
  - 表示完整路线信息
  - 包含起点、终点、途经点和详细步骤

- **RouteStep**: 路线步骤实体
  - 表示路线中单个步骤的详细信息
  - 包含导航指令和道路信息

#### 职责：
- 定义业务数据模型
- 封装业务对象的属性和行为
- 提供数据访问接口

### 5. DTO层（数据传输对象）
**位置**: `com.tourism.dto`
**作用**: 封装请求和响应数据，定义API接口格式

#### 核心类：
- **RouteRequest**: 路线规划请求DTO
  - 封装客户端请求参数
  - 提供参数验证规则

- **RouteResponse**: 路线规划响应DTO
  - 封装API响应数据格式
  - 提供统一的响应结构

- **AmapGeocodeResponse**: 高德地理编码响应DTO
- **AmapDirectionResponse**: 高德路径规划响应DTO

#### 职责：
- 定义API接口格式
- 提供参数验证规则
- 标准化数据传输格式

### 6. Config层（配置层）
**位置**: `com.tourism.config`
**作用**: 管理应用配置和Bean定义

#### 核心类：
- **AmapConfig**: 高德地图API配置
  - 管理API相关配置参数
  - 从配置文件读取设置

- **CorsConfig**: CORS跨域配置
  - 配置跨域资源共享策略
  - 允许前端访问后端API

- **WebClientConfig**: HTTP客户端配置
  - 配置WebClient HTTP客户端
  - 设置编解码器和缓冲区

#### 职责：
- 管理应用配置
- 定义Spring Bean
- 配置第三方组件

### 7. Exception层（异常处理层）
**位置**: `com.tourism.exception`
**作用**: 统一处理应用异常

#### 核心类：
- **GlobalExceptionHandler**: 全局异常处理器
  - 统一处理所有异常
  - 提供统一的错误响应格式

#### 职责：
- 异常捕获和处理
- 错误响应格式化
- 日志记录和监控

## 技术栈

### 核心框架
- **Spring Boot 2.7.14**: 主框架
- **Spring Web**: Web开发支持
- **Spring Validation**: 参数验证

### 工具库
- **Lombok**: 减少样板代码
- **Jackson**: JSON处理
- **WebClient**: HTTP客户端
- **Apache Commons Lang**: 工具类

### 开发工具
- **Maven**: 项目构建和依赖管理
- **JUnit 5**: 单元测试
- **Mockito**: 模拟测试

## 数据流向

```
客户端请求 → Controller层 → Service层 → Util层 → 第三方API
                ↓           ↓         ↓
            参数验证    业务逻辑    数据转换
                ↓           ↓         ↓
客户端响应 ← Controller层 ← Service层 ← Util层 ← 第三方API
```

## 降级策略

1. **优先使用真实API**: 首先尝试调用高德地图API
2. **自动降级**: API失败时自动切换到模拟数据
3. **服务可用性**: 确保服务始终可用，即使API不可用

## 配置管理

### 配置文件
- **application.yml**: 主配置文件
- **环境变量**: 支持外部配置覆盖

### 关键配置
- 服务器端口: 8080
- 上下文路径: /api
- 高德地图API密钥配置
- 日志级别配置

## 安全考虑

- API密钥安全存储
- 参数验证和过滤
- 异常信息不暴露敏感数据
- CORS跨域安全配置

## 扩展性

- 支持添加新的路线策略
- 支持集成其他地图服务
- 支持添加缓存机制
- 支持添加数据库存储

## 监控和日志

- 完整的日志记录
- 异常监控和告警
- 性能监控
- 业务指标统计

## 部署说明

1. **环境要求**: JDK 8+, Maven 3.6+
2. **配置API密钥**: 在application.yml中配置高德地图API密钥
3. **启动应用**: `mvn spring-boot:run`
4. **访问地址**: `http://localhost:8080/api`

## API接口

- `POST /api/route/plan` - 规划路线
- `GET /api/route/quick` - 快速规划路线
- `POST /api/route/statistics` - 获取路线统计
- `GET /api/route/health` - 健康检查
