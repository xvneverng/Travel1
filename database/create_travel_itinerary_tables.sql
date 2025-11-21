-- 旅游行程管理系统数据库表结构
-- 创建时间: 2025-01-27

-- 1. 旅游行程表 - 存储每日行程的基本信息
CREATE TABLE IF NOT EXISTS travel_itinerary (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '行程ID',
    user_id VARCHAR(50) NOT NULL COMMENT '用户ID',
    travel_date DATE NOT NULL COMMENT '旅行日期',
    day_number INT NOT NULL COMMENT '第几天',
    weather_condition VARCHAR(20) COMMENT '天气状况',
    weather_icon VARCHAR(10) COMMENT '天气图标',
    temperature_min INT COMMENT '最低温度',
    temperature_max INT COMMENT '最高温度',
    notes TEXT COMMENT '今日注意事项',
    travel_feelings TEXT COMMENT '旅行感受',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_date (user_id, travel_date),
    INDEX idx_day_number (day_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='旅游行程表';

-- 2. 景点信息表 - 存储景点详细信息
CREATE TABLE IF NOT EXISTS attraction (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '景点ID',
    name VARCHAR(100) NOT NULL COMMENT '景点名称',
    description TEXT COMMENT '景点描述',
    icon VARCHAR(10) COMMENT '景点图标',
    duration VARCHAR(20) COMMENT '建议游览时长',
    tags TEXT COMMENT '景点标签，JSON格式存储',
    tips TEXT COMMENT '游览提示',
    transportation_info TEXT COMMENT '交通信息',
    latitude DECIMAL(10, 8) COMMENT '纬度',
    longitude DECIMAL(11, 8) COMMENT '经度',
    address VARCHAR(200) COMMENT '详细地址',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='景点信息表';

-- 3. 行程景点关联表 - 存储行程中的景点顺序和详细信息
CREATE TABLE IF NOT EXISTS itinerary_attraction (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关联ID',
    itinerary_id BIGINT NOT NULL COMMENT '行程ID',
    attraction_id BIGINT NOT NULL COMMENT '景点ID',
    sequence_order INT NOT NULL COMMENT '景点顺序',
    visit_duration VARCHAR(20) COMMENT '实际游览时长',
    visit_notes TEXT COMMENT '游览备注',
    transportation_from_previous TEXT COMMENT '从上一个景点的交通方式',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (itinerary_id) REFERENCES travel_itinerary(id) ON DELETE CASCADE,
    FOREIGN KEY (attraction_id) REFERENCES attraction(id) ON DELETE CASCADE,
    INDEX idx_itinerary_sequence (itinerary_id, sequence_order),
    UNIQUE KEY uk_itinerary_sequence (itinerary_id, sequence_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='行程景点关联表';

-- 插入示例景点数据
INSERT INTO attraction (name, description, icon, duration, tags, tips, transportation_info, address) VALUES
('南京南站', '南京南站是南京市的主要铁路客运站，位于雨花台区，是京沪高速铁路、沪汉蓉高速铁路、宁杭高速铁路、宁安城际铁路的交汇点。车站建筑宏伟，设施完善，是南京的重要交通枢纽。', '🏢', '30分钟', '["交通设施服务", "火车站"]', '建议提前30分钟到达，注意车次信息', '乘坐地铁S6号线，换乘S3号线，或乘坐20路公交车', '江苏省南京市雨花台区'),
('中山陵', '中山陵是中国近代伟大的民主革命先行者孙中山先生的陵寝，位于南京市玄武区紫金山南麓。陵寝建筑群依山而建，气势恢宏，是中国近代建筑史上的重要作品。', '🏛️', '3小时', '["风景名胜", "国家级景点"]', '需爬392级台阶，穿舒适运动鞋，免费参观', '从南京南站乘坐出租车前往', '江苏省南京市玄武区紫金山南麓'),
('夫子庙', '夫子庙是南京最著名的历史文化街区，以秦淮河为背景，集古建筑、传统文化、商业购物于一体。夜晚的夫子庙灯火辉煌，是体验南京古都风情的绝佳去处。', '🏮', '3小时', '["风景名胜", "历史文化", "商业街区"]', '晚上最美，建议傍晚时分前往，可欣赏秦淮河夜景', '从中山陵乘坐地铁S3号线到夫子庙站', '江苏省南京市秦淮区夫子庙'),
('南京博物院', '南京博物院是中国三大博物馆之一，收藏有丰富的文物珍品，包括青铜器、陶瓷、书画、玉器等。博物馆建筑典雅，展陈精美，是了解南京历史文化的重要窗口。', '🏛️', '2小时', '["科教文化服务", "博物馆", "历史文化"]', '免费参观，需提前预约，周一闭馆', '从夫子庙乘坐地铁2号线到明故宫站', '江苏省南京市玄武区中山东路321号');

-- 插入示例行程数据
INSERT INTO travel_itinerary (user_id, travel_date, day_number, weather_condition, weather_icon, temperature_min, temperature_max, notes, travel_feelings) VALUES
('default_user', '2025-08-21', 1, '阴', '☁️', 24, 30, '天气适宜外出游览，可安排户外景点参观', '漫步在古都南京，感受着深厚的历史文化底蕴。夫子庙的繁华热闹，秦淮河的夜色迷人，品尝了正宗的盐水鸭和鸭血粉丝汤，在秦淮河游船上仿佛穿越回了古代。');

-- 插入行程景点关联数据
INSERT INTO itinerary_attraction (itinerary_id, attraction_id, sequence_order, visit_duration, transportation_from_previous) VALUES
(1, 1, 1, '30分钟', '起点'),
(1, 2, 2, '3小时', '从南京南站乘坐出租车前往'),
(1, 3, 3, '3小时', '从中山陵乘坐地铁S3号线到夫子庙站'),
(1, 4, 4, '2小时', '从夫子庙乘坐地铁2号线到明故宫站');
