# 地图功能使用说明

## 🚨 重要提示：跨域问题

### 问题原因
当您直接双击HTML文件打开时，浏览器使用 `file://` 协议，这会触发跨域安全限制，无法正常调用后端API。

### 解决方案

## 方法一：使用本地HTTP服务器（推荐）

### 1. 启动HTTP服务器
```bash
# 方法1：使用Python（如果已安装）
python -m http.server 8000

# 方法2：使用批处理文件
双击 start-server.bat

# 方法3：使用PowerShell脚本
右键点击 start-server.ps1 -> 使用PowerShell运行
```

### 2. 访问页面
打开浏览器，访问：`http://localhost:8000/travel-day1.html`

## 方法二：使用VS Code Live Server

1. 在VS Code中安装 "Live Server" 扩展
2. 右键点击 `travel-day1.html` 文件
3. 选择 "Open with Live Server"

## 方法三：使用其他HTTP服务器

### Node.js http-server
```bash
npm install -g http-server
http-server -p 8000
```

### PHP内置服务器
```bash
php -S localhost:8000
```

## 🔧 故障排除

### 1. 检查后端服务
确保Spring Boot应用正在运行：
```bash
mvn spring-boot:run
```

### 2. 检查端口
- 后端服务：8080端口
- 前端服务：8000端口

### 3. 查看控制台
按F12打开开发者工具，查看Console标签页的错误信息

### 4. 测试API
使用 `test-api.html` 页面测试API接口是否正常

## 📱 访问地址

- **正确访问方式**：`http://localhost:8000/travel-day1.html`
- **错误访问方式**：`file:///C:/Users/21235/Desktop/tourism/travel/backend/travel-day1.html`

## 🎯 预期效果

正确访问后，页面应该：
1. 自动加载地图
2. 显示从"镇江江苏大学"到"桐庐大奇山森林公园"的路线
3. 显示路线信息（距离、时间等）
4. 提供导航功能

## ❓ 常见问题

**Q: 为什么直接双击HTML文件不行？**
A: 浏览器的同源策略阻止了file://协议访问http://localhost:8080的API。

**Q: 如何知道是否成功？**
A: 页面会显示完整的地图，而不是"地图加载中..."的占位符。

**Q: 如果还是不行怎么办？**
A: 检查控制台错误信息，确保后端服务正在运行。
