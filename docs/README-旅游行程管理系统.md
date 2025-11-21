# 🏛️ 旅游行程管理系统

## 项目概述

这是一个完整的旅游行程管理系统，包含前端页面和后端API，支持用户创建、编辑、保存和查询旅游行程信息。

## 功能特性

### 📅 行程管理
- ✅ 创建和编辑每日行程
- ✅ 设置旅行日期、天气信息
- ✅ 添加旅行感受和注意事项
- ✅ 支持多天行程管理

### 🏛️ 景点管理
- ✅ 添加、编辑、删除景点信息
- ✅ 设置景点游览顺序
- ✅ 记录景点详细信息（描述、图标、时长、类别等）
- ✅ 管理景点间的交通信息

### 💾 数据持久化
- ✅ 完整的数据库设计
- ✅ 支持数据的增删改查
- ✅ 事务处理确保数据一致性

### 🎨 用户界面
- ✅ 响应式设计，支持移动端
- ✅ 直观的编辑界面
- ✅ 实时数据更新
- ✅ 美观的UI设计

## 技术栈

### 后端技术
- **Java 8+** - 编程语言
- **Spring Boot 2.x** - 应用框架
- **MyBatis Plus** - ORM框架
- **MySQL** - 数据库
- **Maven** - 项目管理

### 前端技术
- **HTML5** - 页面结构
- **CSS3** - 样式设计
- **JavaScript (ES6+)** - 交互逻辑
- **Fetch API** - HTTP请求

## 数据库设计

### 1. 旅游行程表 (travel_itinerary)
```sql
CREATE TABLE travel_itinerary (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id VARCHAR(50) NOT NULL,
    travel_date DATE NOT NULL,
    day_number INT NOT NULL,
    weather_condition VARCHAR(20),
    weather_icon VARCHAR(10),
    temperature_min INT,
    temperature_max INT,
    notes TEXT,
    travel_feelings TEXT,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### 2. 景点信息表 (attraction)
```sql
CREATE TABLE attraction (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    icon VARCHAR(10),
    duration VARCHAR(20),
    category VARCHAR(50),
    tags TEXT,
    tips TEXT,
    transportation_info TEXT,
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    address VARCHAR(200),
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### 3. 行程景点关联表 (itinerary_attraction)
```sql
CREATE TABLE itinerary_attraction (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    itinerary_id BIGINT NOT NULL,
    attraction_id BIGINT NOT NULL,
    sequence_order INT NOT NULL,
    visit_duration VARCHAR(20),
    visit_notes TEXT,
    transportation_from_previous TEXT,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (itinerary_id) REFERENCES travel_itinerary(id) ON DELETE CASCADE,
    FOREIGN KEY (attraction_id) REFERENCES attraction(id) ON DELETE CASCADE
);
```

## 项目结构

```
backend/
├── src/main/java/com/tourism/
│   ├── entity/                 # 实体类
│   │   ├── TravelItinerary.java
│   │   ├── Attraction.java
│   │   └── ItineraryAttraction.java
│   ├── dto/                    # 数据传输对象
│   │   ├── TravelItineraryRequest.java
│   │   └── TravelItineraryResponse.java
│   ├── mapper/                 # MyBatis Mapper接口
│   │   ├── TravelItineraryMapper.java
│   │   ├── AttractionMapper.java
│   │   └── ItineraryAttractionMapper.java
│   ├── service/                # 业务逻辑层
│   │   └── TravelItineraryService.java
│   ├── controller/             # 控制器层
│   │   └── TravelItineraryController.java
│   └── config/                 # 配置类
├── src/main/resources/
│   ├── application.yml         # 应用配置
│   └── mapper/                 # MyBatis XML映射文件
├── create_travel_itinerary_tables.sql  # 数据库建表脚本
├── travel-day1.html           # 主页面
├── test-itinerary-api.html    # API测试页面
└── README-旅游行程管理系统.md  # 说明文档
```

## API接口文档

### 基础URL
```
http://localhost:8080/api/travel/itinerary
```

### 1. 保存或更新行程
```http
POST /api/travel/itinerary
Content-Type: application/json

{
    "userId": "default_user",
    "travelDate": "2025-08-21",
    "dayNumber": 1,
    "weatherCondition": "阴",
    "weatherIcon": "☁️",
    "temperatureMin": 24,
    "temperatureMax": 30,
    "notes": "天气适宜外出游览，可安排户外景点参观",
    "travelFeelings": "漫步在古都南京，感受着深厚的历史文化底蕴...",
    "attractions": [
        {
            "name": "南京南站",
            "description": "南京南站是南京市的主要铁路客运站...",
            "icon": "🏢",
            "duration": "30分钟",
            "category": "交通设施",
            "tags": "[\"交通设施服务\", \"火车站\"]",
            "tips": "建议提前30分钟到达，注意车次信息",
            "transportationInfo": "乘坐地铁S6号线，换乘S3号线，或乘坐20路公交车",
            "address": "江苏省南京市雨花台区",
            "sequenceOrder": 1,
            "visitDuration": "30分钟",
            "visitNotes": "",
            "transportationFromPrevious": "起点"
        }
    ]
}
```

### 2. 根据ID获取行程
```http
GET /api/travel/itinerary/{id}
```

### 3. 根据用户ID和日期获取行程
```http
GET /api/travel/itinerary/user/{userId}/date/{travelDate}
```

### 4. 根据用户ID和天数获取行程
```http
GET /api/travel/itinerary/user/{userId}/day/{dayNumber}
```

### 5. 获取用户所有行程
```http
GET /api/travel/itinerary/user/{userId}
```

### 6. 删除行程
```http
DELETE /api/travel/itinerary/{id}
```

### 7. 健康检查
```http
GET /api/travel/itinerary/health
```

## 快速开始

### 1. 环境要求
- Java 8+
- MySQL 5.7+
- Maven 3.6+

### 2. 数据库设置
```bash
# 1. 创建数据库
mysql -u root -p
CREATE DATABASE tourism_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 2. 执行建表脚本
mysql -u root -p tourism_db < create_travel_itinerary_tables.sql
```

### 3. 配置应用
编辑 `src/main/resources/application.yml`：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/tourism_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
```

### 4. 启动应用
```bash
# 编译项目
mvn clean compile

# 启动应用
mvn spring-boot:run
```

### 5. 访问页面
- **主页面**: http://localhost:8000/travel-day1.html
- **API测试页面**: http://localhost:8000/test-itinerary-api.html
- **后端API**: http://localhost:8080/api/travel/itinerary/health

## 使用说明

### 1. 启动HTTP服务器
```bash
# 使用Python启动静态文件服务器
python -m http.server 8000

# 或者使用提供的脚本
# Windows
start-server.bat

# PowerShell
start-server.ps1
```

### 2. 编辑行程
1. 打开主页面 http://localhost:8000/travel-day1.html
2. 点击"✏️ 编辑"按钮进入编辑模式
3. 修改基本信息（日期、天气、温度等）
4. 编辑景点信息（添加、删除、修改景点）
5. 点击"💾 保存"按钮保存更改

### 3. 测试API
1. 打开API测试页面 http://localhost:8000/test-itinerary-api.html
2. 使用表单测试各种API接口
3. 查看响应结果验证功能

## 功能演示

### 编辑模式
- 点击编辑按钮进入编辑模式
- 可以修改日期、天气、温度等基本信息
- 可以添加、删除、修改景点信息
- 支持景点顺序调整

### 数据保存
- 自动保存到数据库
- 支持事务处理
- 数据验证和错误处理

### 实时更新
- 保存后立即更新页面显示
- 路线图自动更新
- 天气信息实时显示

## 扩展功能

### 已实现
- ✅ 完整的CRUD操作
- ✅ 数据验证和错误处理
- ✅ 响应式UI设计
- ✅ 事务处理
- ✅ 详细的注释文档

### 可扩展
- 🔄 用户认证和授权
- 🗺️ 地图集成（高德地图API）
- 📸 照片上传和管理
- 📊 数据统计和分析
- 🌐 多语言支持
- 📱 移动端APP

## 故障排除

### 常见问题

1. **数据库连接失败**
   - 检查MySQL服务是否启动
   - 验证数据库连接配置
   - 确认数据库用户权限

2. **跨域问题**
   - 确保使用HTTP服务器而不是file://协议
   - 检查CORS配置

3. **API请求失败**
   - 确认后端服务已启动
   - 检查端口配置（默认8080）
   - 查看控制台错误信息

### 日志查看
```bash
# 查看应用日志
tail -f logs/application.log

# 查看数据库连接日志
tail -f logs/database.log
```

## 贡献指南

1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 联系方式

如有问题或建议，请通过以下方式联系：
- 提交 Issue
- 发送邮件
- 微信联系

---

**享受你的旅行规划！** 🎉✈️🏛️
