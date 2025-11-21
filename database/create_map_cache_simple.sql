-- 地图缓存表 - 简单版本
-- 用于存储生成的地图URL，避免重复生成

CREATE TABLE IF NOT EXISTS map_cache (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '缓存ID',
    cache_key VARCHAR(255) NOT NULL COMMENT '缓存键（基于景点名称生成）',
    map_url TEXT NOT NULL COMMENT '地图URL',
    locations TEXT NOT NULL COMMENT '景点名称列表，用逗号分隔',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    -- 索引
    INDEX idx_cache_key (cache_key) COMMENT '缓存键索引',
    UNIQUE KEY uk_cache_key (cache_key) COMMENT '缓存键唯一约束'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='地图缓存表';

-- 插入示例数据
INSERT INTO map_cache (cache_key, map_url, locations) VALUES
('南京南站,中山陵,夫子庙,南京博物院', 'https://example.com/map/main/12345', '南京南站,中山陵,夫子庙,南京博物院'),
('南京南站,中山陵', 'https://example.com/map/segment/12346', '南京南站,中山陵'),
('中山陵,夫子庙', 'https://example.com/map/segment/12347', '中山陵,夫子庙'),
('夫子庙,南京博物院', 'https://example.com/map/segment/12348', '夫子庙,南京博物院');

-- 查看表结构
DESCRIBE map_cache;

-- 查看示例数据
SELECT * FROM map_cache;
