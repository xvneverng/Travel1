# 旅游路线规划后端服务

基于Spring Boot的旅游路线规划后端服务，集成高德地图API，提供智能路线规划功能。

## 项目架构

### 分层架构说明

```
├── controller/          # 控制层 - 处理HTTP请求和响应
├── service/            # 服务层 - 业务逻辑处理
├── util/               # 工具层 - 第三方API调用和工具方法
├── entity/             # 实体层 - 数据模型定义
├── dto/                # 数据传输对象 - 请求和响应模型
├── config/             # 配置层 - 应用配置和Bean定义
└── exception/          # 异常处理层 - 全局异常处理
```

### 各层职责说明

1. **Controller层（控制层）**
   - 接收HTTP请求
   - 参数验证和转换
   - 调用Service层处理业务逻辑
   - 返回HTTP响应

2. **Service层（服务层）**
   - 业务逻辑处理
   - 协调各个组件
   - 数据转换和验证
   - 异常处理

3. **Util层（工具层）**
   - 第三方API调用（高德地图API）
   - 通用工具方法
   - 外部服务集成

4. **Entity层（实体层）**
   - 数据模型定义
   - 业务对象表示

5. **DTO层（数据传输对象）**
   - 请求参数封装
   - 响应数据封装
   - API接口定义

6. **Config层（配置层）**
   - 应用配置管理
   - Bean定义和配置
   - 第三方服务配置

7. **Exception层（异常处理层）**
   - 全局异常处理
   - 统一错误响应格式

### 详细架构说明

每个类都添加了详细的注释，说明其作用、用法和含义：

- **主启动类**: `TravelBackendApplication` - Spring Boot应用入口
- **控制器**: `RouteController` - 处理HTTP请求和响应
- **服务类**: `RoutePlanningService` - 业务逻辑处理和降级策略
- **工具类**: `AmapApiUtil` - 高德地图API调用，`MockAmapApiUtil` - 模拟数据降级
- **实体类**: `Location`、`Route`、`RouteStep` - 数据模型定义
- **DTO类**: `RouteRequest`、`RouteResponse` - 请求响应封装
- **配置类**: `AmapConfig`、`CorsConfig`、`WebClientConfig` - 应用配置
- **异常处理**: `GlobalExceptionHandler` - 全局异常处理

更多详细信息请查看 [ARCHITECTURE.md](ARCHITECTURE.md) 文件。

## 功能特性

- 🗺️ 智能路线规划（支持驾车、步行、公交）
- 📍 地理编码（地址转坐标）
- 🛣️ 多途经点路线规划
- 📊 路线统计信息
- 🔧 灵活的配置管理
- 🛡️ 完善的异常处理
- 📝 详细的日志记录

## 技术栈

- **框架**: Spring Boot 2.7.14
- **Java版本**: JDK 8
- **HTTP客户端**: WebFlux WebClient
- **JSON处理**: Jackson
- **参数验证**: Spring Validation
- **日志**: SLF4J + Logback
- **地图服务**: 高德地图API

## 快速开始

### 1. 环境要求

- JDK 8+
- Maven 3.6+
- 高德地图API密钥

### 2. 配置高德地图API

在 `src/main/resources/application.yml` 中配置你的高德地图API密钥：

```yaml
amap:
  api:
    key: your_amap_api_key_here
```

### 3. 运行项目

```bash
# 编译项目
mvn clean compile

# 运行项目
mvn spring-boot:run
```

项目将在 `http://localhost:8080` 启动。

## API接口

### 1. 规划路线

**POST** `/api/route/plan`

请求体：
```json
{
  "locations": ["南京站", "小米南京科技园", "夫子庙"],
  "strategy": "driving",
  "includeSteps": true
}
```

响应：
```json
{
  "code": 200,
  "message": "路线规划成功",
  "data": {
    "routeId": "route_1234567890",
    "origin": {
      "name": "南京站",
      "longitude": 118.7969,
      "latitude": 32.0603,
      "address": "江苏省南京市玄武区",
      "city": "南京市"
    },
    "destination": {
      "name": "夫子庙",
      "longitude": 118.7969,
      "latitude": 32.0603,
      "address": "江苏省南京市秦淮区",
      "city": "南京市"
    },
    "waypoints": [...],
    "distance": 15000,
    "duration": 1800,
    "steps": [...],
    "polyline": "..."
  }
}
```

### 2. 快速规划路线

**GET** `/api/route/quick?locations=南京站,小米南京科技园,夫子庙&strategy=driving`

### 3. 获取路线统计

**POST** `/api/route/statistics`

### 4. 健康检查

**GET** `/api/route/health`

## 使用示例

### 使用curl测试

```bash
# 规划路线
curl -X POST http://localhost:8080/api/route/plan \
  -H "Content-Type: application/json" \
  -d '{
    "locations": ["南京站", "小米南京科技园", "夫子庙"],
    "strategy": "driving"
  }'

# 快速规划
curl "http://localhost:8080/api/route/quick?locations=南京站,小米南京科技园,夫子庙&strategy=driving"
```

## 配置说明

### 高德地图API配置

```yaml
amap:
  api:
    key: ${AMAP_API_KEY:your_amap_api_key_here}  # API密钥
    base-url: https://restapi.amap.com/v3        # API基础URL
    geocode-url: /geocode/geo                    # 地理编码API
    direction-url: /direction/driving            # 路径规划API
    place-search-url: /place/text                # 地点搜索API
```

### 服务器配置

```yaml
server:
  port: 8080                    # 服务端口
  servlet:
    context-path: /api          # 上下文路径
```

## 开发说明

### 项目结构

```
src/main/java/com/tourism/
├── TravelBackendApplication.java    # 主启动类
├── controller/
│   └── RouteController.java         # 路线控制器
├── service/
│   └── RoutePlanningService.java    # 路线规划服务
├── util/
│   └── AmapApiUtil.java            # 高德地图API工具
├── entity/
│   ├── Location.java               # 地点实体
│   ├── Route.java                  # 路线实体
│   └── RouteStep.java              # 路线步骤实体
├── dto/
│   ├── RouteRequest.java           # 路线请求DTO
│   ├── RouteResponse.java          # 路线响应DTO
│   ├── AmapGeocodeResponse.java    # 高德地理编码响应DTO
│   └── AmapDirectionResponse.java  # 高德路径规划响应DTO
├── config/
│   ├── AmapConfig.java             # 高德地图配置
│   ├── WebClientConfig.java        # WebClient配置
│   └── CorsConfig.java             # CORS配置
└── exception/
    └── GlobalExceptionHandler.java # 全局异常处理
```

### 扩展功能

1. **添加新的路线策略**
   - 在 `AmapApiUtil` 中添加新的策略支持
   - 更新 `RouteRequest` 中的策略验证

2. **添加缓存功能**
   - 集成Redis缓存
   - 缓存地理编码结果
   - 缓存路线规划结果

3. **添加数据库支持**
   - 集成JPA/Hibernate
   - 保存路线历史记录
   - 用户偏好设置

4. **添加认证授权**
   - 集成Spring Security
   - JWT令牌认证
   - 用户权限管理

## 注意事项

1. **API密钥安全**: 请妥善保管高德地图API密钥，不要提交到版本控制系统
2. **API调用限制**: 注意高德地图API的调用频率限制
3. **错误处理**: 所有API调用都有完善的错误处理机制
4. **日志记录**: 项目包含详细的日志记录，便于调试和监控

## 许可证

MIT License

