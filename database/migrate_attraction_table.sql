-- 景点表结构迁移脚本
-- 将category和tags字段合并为统一的tags字段
-- 执行时间: 2025-01-27

-- 使用数据库
USE travel_db;

-- 1. 备份现有数据（可选）
-- CREATE TABLE attraction_backup AS SELECT * FROM attraction;

-- 2. 添加新的tags字段（如果不存在）
-- 注意：这里假设tags字段已经存在，如果不存在需要先添加
-- ALTER TABLE attraction ADD COLUMN new_tags TEXT COMMENT '景点标签，JSON格式存储，包含类别和标签信息';

-- 3. 更新tags字段，将category和tags合并
UPDATE attraction 
SET tags = CASE 
    WHEN category IS NOT NULL AND tags IS NOT NULL AND tags != '' THEN
        CONCAT('[', JSON_QUOTE(category), ',', SUBSTRING(tags, 2))
    WHEN category IS NOT NULL AND (tags IS NULL OR tags = '') THEN
        CONCAT('[', JSON_QUOTE(category), ']')
    WHEN category IS NULL AND tags IS NOT NULL AND tags != '' THEN
        tags
    ELSE
        '[]'
END;

-- 4. 删除category字段
ALTER TABLE attraction DROP COLUMN category;

-- 5. 更新索引
-- 删除旧的category索引
DROP INDEX IF EXISTS idx_category ON attraction;

-- 添加新的tags索引
ALTER TABLE attraction ADD INDEX idx_tags (tags(100));

-- 6. 验证数据更新结果
SELECT 
    id, 
    name, 
    tags,
    created_time
FROM attraction 
ORDER BY id 
LIMIT 10;

-- 7. 显示更新统计
SELECT 
    'Total attractions' as description,
    COUNT(*) as count
FROM attraction
UNION ALL
SELECT 
    'Attractions with tags' as description,
    COUNT(*) as count
FROM attraction 
WHERE tags IS NOT NULL AND tags != '' AND tags != '[]';
