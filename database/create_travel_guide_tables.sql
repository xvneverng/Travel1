-- 旅游路书管理系统数据库表结构
-- 创建时间: 2025-01-27

-- 使用数据库
USE travel_db;

-- 1. 路书主表 - 存储路书的基本信息
CREATE TABLE IF NOT EXISTS travel_guide (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '路书ID',
    user_id VARCHAR(50) NOT NULL COMMENT '用户ID',
    guide_name VARCHAR(100) NOT NULL COMMENT '路书名称',
    destination VARCHAR(100) COMMENT '目的地',
    description TEXT COMMENT '路书描述',
    cover_image VARCHAR(200) COMMENT '封面图片URL',
    status ENUM('draft', 'published', 'archived') DEFAULT 'draft' COMMENT '路书状态',
    is_public BOOLEAN DEFAULT FALSE COMMENT '是否公开',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_guide_name (guide_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='路书主表';

-- 2. 路书事项表 - 存储路书中的具体准备事项（简化版）
CREATE TABLE IF NOT EXISTS travel_guide_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '事项ID',
    guide_id BIGINT NOT NULL COMMENT '路书ID',
    category VARCHAR(50) NOT NULL COMMENT '事项类别',
    item_name VARCHAR(200) NOT NULL COMMENT '事项名称',
    description TEXT COMMENT '事项描述',
    status ENUM('pending', 'completed', 'cancelled') DEFAULT 'pending' COMMENT '完成状态',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (guide_id) REFERENCES travel_guide(id) ON DELETE CASCADE,
    INDEX idx_guide_id (guide_id),
    INDEX idx_category (category),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='路书事项表（简化版）';

-- 3. 路书与行程关联表 - 关联路书和具体行程
CREATE TABLE IF NOT EXISTS travel_guide_itinerary (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关联ID',
    guide_id BIGINT NOT NULL COMMENT '路书ID',
    itinerary_id BIGINT NOT NULL COMMENT '行程ID',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (guide_id) REFERENCES travel_guide(id) ON DELETE CASCADE,
    FOREIGN KEY (itinerary_id) REFERENCES travel_itinerary(id) ON DELETE CASCADE,
    UNIQUE KEY uk_guide_itinerary (guide_id, itinerary_id),
    INDEX idx_guide_id (guide_id),
    INDEX idx_itinerary_id (itinerary_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='路书与行程关联表';

-- 4. 路书模板表 - 存储常用的路书模板
CREATE TABLE IF NOT EXISTS travel_guide_template (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '模板ID',
    template_name VARCHAR(100) NOT NULL COMMENT '模板名称',
    destination VARCHAR(100) COMMENT '适用目的地',
    description TEXT COMMENT '模板描述',
    category_items JSON COMMENT '类别事项配置，JSON格式',
    is_system BOOLEAN DEFAULT FALSE COMMENT '是否系统模板',
    created_by VARCHAR(50) COMMENT '创建者',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_template_name (template_name),
    INDEX idx_destination (destination)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='路书模板表';

-- 5. 天气建议表 - 存储天气相关的建议信息
CREATE TABLE IF NOT EXISTS weather_suggestion (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '建议ID',
    suggestion_type VARCHAR(50) NOT NULL COMMENT '建议类型',
    content TEXT NOT NULL COMMENT '建议内容（包含图标、标题和描述）',
    weather_condition VARCHAR(50) COMMENT '适用天气条件',
    temperature_min INT COMMENT '最低温度',
    temperature_max INT COMMENT '最高温度',
    priority ENUM('low', 'medium', 'high') DEFAULT 'medium' COMMENT '优先级',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_suggestion_type (suggestion_type),
    INDEX idx_weather_condition (weather_condition),
    INDEX idx_priority (priority),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='天气建议表';

-- 6. 路书天气建议关联表 - 关联路书和天气建议
CREATE TABLE IF NOT EXISTS travel_guide_weather (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关联ID',
    guide_id BIGINT NOT NULL COMMENT '路书ID',
    weather_suggestion_id BIGINT NOT NULL COMMENT '天气建议ID',
    is_applied BOOLEAN DEFAULT FALSE COMMENT '是否已应用',
    applied_time TIMESTAMP NULL COMMENT '应用时间',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (guide_id) REFERENCES travel_guide(id) ON DELETE CASCADE,
    FOREIGN KEY (weather_suggestion_id) REFERENCES weather_suggestion(id) ON DELETE CASCADE,
    UNIQUE KEY uk_guide_weather (guide_id, weather_suggestion_id),
    INDEX idx_guide_id (guide_id),
    INDEX idx_weather_suggestion_id (weather_suggestion_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='路书天气建议关联表';

-- 插入天气建议数据
INSERT INTO weather_suggestion (suggestion_type, content, weather_condition, temperature_min, temperature_max, priority) VALUES
('防暑降温', '👕 天气较热,注意防暑降温\n天气较热，建议多喝水，避免长时间户外活动，注意防暑降温', 'hot', 30, 40, 'high'),
('雨天提醒', '☔ 今天有雨,出门记得带伞\n今天有雨，出门记得带伞，注意防滑', 'rainy', 15, 25, 'high'),
('运动建议', '🏃 舒适的天气别浪费,快集结好友出门运动\n天气舒适，适合户外运动，建议与朋友一起进行户外活动', 'sunny', 20, 28, 'medium'),
('防晒提醒', '☀️ 防晒很重要,出门更安心\n阳光强烈，建议涂抹防晒霜，佩戴太阳镜和帽子', 'sunny', 25, 35, 'high'),
('洗车建议', '🚗 三天内有雨,不适宜洗车\n未来三天有雨，不建议洗车，可以等天气转晴后再洗', 'rainy', 10, 20, 'low'),
('过敏提醒', '🤧 过敏提醒:天气和降水对过敏患者不友好\n天气和降水对过敏患者不友好，减少外出，外出穿长袖长裤，避免受凉过敏', 'rainy', 5, 15, 'high'),
('空调建议', '❄️ 空调建议:炎热午后,建议开启制冷空调\n炎热午后，建议开启制冷空调，保持室内舒适温度', 'hot', 30, 40, 'medium');

-- 插入示例路书数据
INSERT INTO travel_guide (user_id, guide_name, destination, description, status, is_public) VALUES
('default_user', '南京之旅准备清单', '南京', '南京三日游完整准备清单，包含交通、住宿、物品等各项准备事项', 'published', TRUE);

-- 插入示例路书事项数据（简化版）
INSERT INTO travel_guide_item (guide_id, category, item_name, description, status) VALUES
(1, '交通准备', '南京地铁卡', '购买或激活南京地铁卡，方便市内交通出行', 'completed'),
(1, '住宿安排', '夫子庙酒店确认', '确认夫子庙附近酒店预订信息', 'completed'),
(1, '住宿安排', '酒店早餐确认', '确认酒店是否提供早餐服务', 'pending'),
(1, '应用准备', '高德地图下载', '下载并熟悉高德地图APP使用', 'completed'),
(1, '物品准备', '充电宝', '准备移动电源，确保手机电量充足', 'completed'),
(1, '物品准备', '舒适步行鞋', '准备舒适的步行鞋，适合长时间步行', 'pending'),
(1, '物品准备', '雨伞', '准备雨伞，应对可能的雨天', 'pending'),
(1, '物品准备', '常用药品', '准备常用药品，如感冒药、止痛药等', 'completed'),
(1, '预订信息', '明孝陵门票', '提前预订明孝陵门票，避免现场排队', 'completed');

-- 插入路书与行程关联数据
INSERT INTO travel_guide_itinerary (guide_id, itinerary_id) VALUES
(1, 1);

-- 插入示例路书模板数据
INSERT INTO travel_guide_template (template_name, destination, description, category_items, is_system, created_by) VALUES
('南京旅游准备模板', '南京', '南京旅游通用准备清单模板', 
'{"交通准备": ["地铁卡", "公交卡", "打车软件"], "住宿安排": ["酒店预订", "早餐确认", "入住时间确认"], "应用准备": ["地图APP", "翻译软件", "支付软件"], "物品准备": ["充电宝", "步行鞋", "雨伞", "常用药品", "相机"], "预订信息": ["景点门票", "餐厅预订", "演出票务"]}', 
TRUE, 'system'),
('北京旅游准备模板', '北京', '北京旅游通用准备清单模板',
'{"交通准备": ["地铁卡", "公交卡"], "住宿安排": ["酒店预订", "早餐确认"], "应用准备": ["地图APP", "翻译软件"], "物品准备": ["充电宝", "步行鞋", "防雾霾口罩", "常用药品"], "预订信息": ["故宫门票", "长城门票", "天安门升旗"]}',
TRUE, 'system');

-- 插入路书与天气建议关联数据
INSERT INTO travel_guide_weather (guide_id, weather_suggestion_id, is_applied) VALUES
(1, 1, TRUE),  -- 防暑降温
(1, 2, TRUE),  -- 雨天提醒
(1, 3, FALSE), -- 运动建议
(1, 4, TRUE),  -- 防晒提醒
(1, 5, FALSE), -- 洗车建议
(1, 6, FALSE), -- 过敏提醒
(1, 7, TRUE);  -- 空调建议


-- 查看表结构
DESCRIBE travel_guide;
DESCRIBE travel_guide_item;
DESCRIBE travel_guide_itinerary;
DESCRIBE travel_guide_template;

-- 查看示例数据
SELECT * FROM travel_guide;
SELECT * FROM travel_guide_item ORDER BY guide_id, id;
SELECT * FROM travel_guide_itinerary;
SELECT * FROM travel_guide_template;
