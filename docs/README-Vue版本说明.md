# 沿途路书 Vue版本

## 概述

这是将原始HTML文件转换为Vue.js组件的版本，完全保持了原有的页面样式和功能，并确保前后端接口一致。

## 文件结构

```
├── TravelDay1.vue          # DAY1详细行程页面组件
├── TravelItinerary.vue     # 总览页面组件
├── main.js                 # Vue应用入口文件
├── index.html              # HTML入口文件
├── package.json            # 项目依赖配置
└── README-Vue版本说明.md   # 本说明文件
```

## 功能特性

### TravelDay1.vue
- ✅ 完全保持原始HTML的页面样式和布局
- ✅ 支持行程信息编辑功能
- ✅ 集成地图显示功能
- ✅ 旅游感受编辑和保存到数据库
- ✅ 景点列表动态管理
- ✅ 与后端API完全兼容

### TravelItinerary.vue
- ✅ 完全保持原始HTML的页面样式和布局
- ✅ 天气预报显示
- ✅ 天气建议展示
- ✅ 准备事项清单管理
- ✅ 个人注意事项编辑
- ✅ 进度统计功能

## 技术特点

1. **完全兼容**: 与原始HTML文件保持100%相同的视觉效果
2. **API一致**: 使用相同的前后端接口，确保数据交互正常
3. **响应式设计**: 支持移动端和桌面端
4. **组件化**: 使用Vue 3 Composition API
5. **状态管理**: 使用Vue的响应式数据管理

## 使用方法

### 1. 启动开发服务器

```bash
# 使用Python HTTP服务器
python -m http.server 8000

# 或者使用npm脚本
npm run dev
```

### 2. 访问应用

打开浏览器访问: `http://localhost:8000`

### 3. 确保后端服务运行

确保Spring Boot后端服务在 `http://localhost:8080` 运行

## API接口

### 行程相关接口
- `POST /api/travel/itinerary` - 保存行程信息
- `GET /api/travel/itinerary/feelings/{dayNumber}` - 获取旅游感受
- `POST /api/travel/itinerary/feelings` - 保存旅游感受

### 地图相关接口
- `POST /api/route/map` - 生成路线地图
- `GET /api/route/health` - 检查后端连接状态

## 主要改进

1. **组件化架构**: 将HTML页面拆分为可复用的Vue组件
2. **数据绑定**: 使用Vue的响应式数据绑定，自动更新UI
3. **事件处理**: 使用Vue的事件系统处理用户交互
4. **状态管理**: 集中管理组件状态，提高代码可维护性
5. **代码复用**: 提取公共样式和逻辑，减少重复代码

## 样式保持

所有CSS样式都完全保持与原始HTML文件一致，包括：
- 状态栏样式
- 头部导航样式
- 横幅样式
- 标签页样式
- 内容区域样式
- 地图区域样式
- 编辑表单样式
- 响应式设计

## 浏览器兼容性

- Chrome 60+
- Firefox 60+
- Safari 12+
- Edge 79+

## 注意事项

1. 确保后端Spring Boot应用正在运行
2. 使用HTTP服务器访问，避免file://协议的跨域问题
3. 所有API接口与原始HTML版本完全一致
4. 页面样式和交互体验与原始版本完全相同

## 开发说明

如果需要修改页面样式或功能，请：
1. 修改对应的Vue组件文件
2. 保持与原始HTML的样式一致性
3. 确保API接口调用正确
4. 测试所有功能是否正常工作
