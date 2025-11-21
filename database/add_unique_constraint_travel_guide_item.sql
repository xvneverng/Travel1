-- 为travel_guide_item表添加唯一性约束
-- 确保同一个路书下不能有相同类别和名称的事项

USE travel_db;

-- 首先删除可能存在的重复数据
DELETE t1 FROM travel_guide_item t1
INNER JOIN travel_guide_item t2 
WHERE t1.id > t2.id 
AND t1.guide_id = t2.guide_id 
AND t1.category = t2.category 
AND t1.item_name = t2.item_name;

-- 添加唯一性约束
ALTER TABLE travel_guide_item 
ADD UNIQUE KEY uk_guide_category_item (guide_id, category, item_name);

-- 查看表结构确认约束已添加
SHOW CREATE TABLE travel_guide_item;
