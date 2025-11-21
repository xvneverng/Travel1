-- 向travel_guide表添加myNotes字段
-- 用于存储总览页面最下面的"我的注意事项"

USE travel_db;

-- 添加myNotes字段到travel_guide表
ALTER TABLE travel_guide 
ADD COLUMN my_notes TEXT COMMENT '我的注意事项' AFTER description;

-- 查看表结构确认字段已添加
DESCRIBE travel_guide;

-- 可选：为现有记录设置默认值
UPDATE travel_guide 
SET my_notes = '暂无注意事项' 
WHERE my_notes IS NULL;
