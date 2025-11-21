-- 为travel_guide_item表插入默认的准备事项数据
-- 这些数据会作为用户的初始准备事项

USE travel_db;



-- 删除现有的默认数据（如果存在）
DELETE FROM travel_guide_item WHERE guide_id = @guide_id;

-- 插入交通准备事项
INSERT INTO travel_guide_item (guide_id, category, item_name, description, status, created_time, updated_time) VALUES
(@guide_id, '交通准备', '南京地铁卡', '购买或激活南京地铁卡', 'completed', NOW(), NOW());

-- 插入住宿安排事项
INSERT INTO travel_guide_item (guide_id, category, item_name, description, status, created_time, updated_time) VALUES
(@guide_id, '住宿安排', '夫子庙酒店确认', '确认酒店预订信息', 'completed', NOW(), NOW()),
(@guide_id, '住宿安排', '酒店早餐确认', '确认酒店早餐时间和地点', 'pending', NOW(), NOW());

-- 插入应用准备事项
INSERT INTO travel_guide_item (guide_id, category, item_name, description, status, created_time, updated_time) VALUES
(@guide_id, '应用准备', '高德地图下载', '下载并注册高德地图APP', 'completed', NOW(), NOW());

-- 插入物品准备事项
INSERT INTO travel_guide_item (guide_id, category, item_name, description, status, created_time, updated_time) VALUES
(@guide_id, '物品准备', '充电宝', '准备移动电源', 'completed', NOW(), NOW()),
(@guide_id, '物品准备', '舒适步行鞋', '准备适合长时间步行的鞋子', 'pending', NOW(), NOW()),
(@guide_id, '物品准备', '雨伞', '准备雨伞以防下雨', 'pending', NOW(), NOW()),
(@guide_id, '物品准备', '常用药品', '准备感冒药、止痛药等常用药品', 'completed', NOW(), NOW());

-- 插入预订信息事项
INSERT INTO travel_guide_item (guide_id, category, item_name, description, status, created_time, updated_time) VALUES
(@guide_id, '预订信息', '明孝陵门票', '购买明孝陵门票', 'completed', NOW(), NOW()),
(@guide_id, '预订信息', '中山陵门票预约', '预约中山陵参观时间', 'pending', NOW(), NOW()),
(@guide_id, '预订信息', '南京博物院预约', '预约南京博物院参观', 'pending', NOW(), NOW()),
(@guide_id, '预订信息', '总统府门票', '购买总统府门票', 'completed', NOW(), NOW());

-- 查看插入的数据
SELECT 
    tgi.id,
    tgi.category,
    tgi.item_name,
    tgi.status,
    tgi.created_time
FROM travel_guide_item tgi
WHERE tgi.guide_id = @guide_id
ORDER BY tgi.category, tgi.id;

-- 显示统计信息
SELECT 
    category,
    COUNT(*) as total_items,
    SUM(CASE WHEN status = 'completed' THEN 1 ELSE 0 END) as completed_items,
    ROUND(SUM(CASE WHEN status = 'completed' THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 0) as completion_percentage
FROM travel_guide_item 
WHERE guide_id = @guide_id
GROUP BY category
ORDER BY category;
