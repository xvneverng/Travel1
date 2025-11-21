# MySQL数据库设置说明

## 📋 前置要求

1. **安装MySQL 8.0+**
2. **启动MySQL服务**
3. **创建数据库和用户**

## 🔧 数据库设置步骤

### 1. 连接MySQL
```bash
mysql -u root -p
```

### 2. 创建数据库
```sql
CREATE DATABASE travel_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. 创建用户（可选，推荐）
```sql
-- 创建用户
CREATE USER 'travel_user'@'localhost' IDENTIFIED BY 'travel_password';

-- 授权
GRANT ALL PRIVILEGES ON travel_db.* TO 'travel_user'@'localhost';

-- 刷新权限
FLUSH PRIVILEGES;
```

### 4. 验证数据库
```sql
-- 查看数据库
SHOW DATABASES;

-- 使用数据库
USE travel_db;

-- 查看表
SHOW TABLES;
```

## ⚙️ 配置文件说明

### application.yml配置
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/travel_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: root          # 修改为您的MySQL用户名
    password: 123456        # 修改为您的MySQL密码
```

### 重要参数说明
- `useUnicode=true`: 支持中文
- `characterEncoding=utf8`: 字符编码
- `useSSL=false`: 禁用SSL（开发环境）
- `serverTimezone=Asia/Shanghai`: 时区设置
- `allowPublicKeyRetrieval=true`: 允许公钥检索

## 🚀 启动应用

1. **确保MySQL服务运行**
2. **修改application.yml中的数据库连接信息**
3. **启动Spring Boot应用**
4. **查看控制台输出，确认数据库连接成功**

## 🔍 验证功能

### 1. 检查数据库连接
启动应用后，控制台应显示：
```
✅ MySQL连接成功
✅ travel_feelings表已存在
```

### 2. 测试API接口
```bash
# 健康检查
curl http://localhost:8080/api/travel/health

# 保存旅游感受
curl -X POST http://localhost:8080/api/travel/feelings \
  -H "Content-Type: application/json" \
  -d '{"userId":"test_user","dayNumber":1,"content":"今天很开心"}'

# 获取旅游感受
curl http://localhost:8080/api/travel/feelings/1?userId=test_user
```

### 3. 查看数据库数据
```sql
USE travel_db;
SELECT * FROM travel_feelings;
```

## 🛠️ 常见问题

### 1. 连接被拒绝
- 检查MySQL服务是否启动
- 检查端口3306是否开放
- 检查防火墙设置

### 2. 认证失败
- 检查用户名密码是否正确
- 检查用户是否有访问权限

### 3. 时区问题
- 确保MySQL时区设置正确
- 在连接URL中指定时区

### 4. 字符编码问题
- 确保数据库使用utf8mb4编码
- 检查连接URL中的字符编码参数

## 📊 数据库表结构

```sql
CREATE TABLE travel_feelings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL COMMENT '用户ID',
    day_number INT NOT NULL COMMENT '天数',
    content TEXT COMMENT '旅游感受内容',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_day (user_id, day_number),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='旅游感受表';
```

## 🔄 数据迁移

如果需要从H2迁移到MySQL：
1. 导出H2数据
2. 转换数据格式
3. 导入到MySQL
4. 验证数据完整性
