-- 为travel_guide表添加行程信息字段
USE travel_db;

-- 添加出行日期字段
ALTER TABLE travel_guide 
ADD COLUMN start_date DATE COMMENT '出行开始日期' AFTER destination;

-- 添加结束日期字段
ALTER TABLE travel_guide 
ADD COLUMN end_date DATE COMMENT '出行结束日期' AFTER start_date;

-- 添加行程时长字段
ALTER TABLE travel_guide 
ADD COLUMN duration_days INT COMMENT '行程天数' AFTER end_date;

-- 添加行程时长描述字段
ALTER TABLE travel_guide 
ADD COLUMN duration_desc VARCHAR(50) COMMENT '行程时长描述(如: 5天4晚)' AFTER duration_days;

-- 查看表结构确认字段已添加
DESCRIBE travel_guide;

-- 为现有记录设置默认值
UPDATE travel_guide 
SET 
    start_date = '2025-08-21',
    end_date = '2025-08-25',
    duration_days = 5,
    duration_desc = '5天4晚'
WHERE start_date IS NULL;
