-- 向travel_itinerary表添加照片URL字段
-- 创建时间: 2025-01-27

-- 添加照片URL字段，支持存储多张照片的URL，使用JSON格式存储
ALTER TABLE travel_itinerary 
ADD COLUMN photo_urls TEXT COMMENT '照片URL列表，JSON格式存储，如：["url1", "url2", "url3"]';

-- 添加索引以提高查询性能
CREATE INDEX idx_photo_urls ON travel_itinerary(photo_urls(100));

-- 更新现有数据，为已有记录设置空的照片URL数组
UPDATE travel_itinerary 
SET photo_urls = '[]' 
WHERE photo_urls IS NULL;
