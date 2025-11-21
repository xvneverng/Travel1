-- 创建数据库
CREATE DATABASE IF NOT EXISTS travel_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE travel_db;

-- 创建旅游感受表
CREATE TABLE IF NOT EXISTS travel_feelings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id VARCHAR(50) NOT NULL COMMENT '用户ID',
    day_number INT NOT NULL COMMENT '天数（第几天）',
    content TEXT COMMENT '旅游感受内容',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 索引
    INDEX idx_user_day (user_id, day_number) COMMENT '用户和天数的复合索引',
    INDEX idx_created_at (created_at) COMMENT '创建时间索引',
    INDEX idx_user_id (user_id) COMMENT '用户ID索引',
    
    -- 唯一约束：同一用户同一天只能有一条记录
    UNIQUE KEY uk_user_day (user_id, day_number) COMMENT '用户和天数的唯一约束'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='旅游感受表';

-- 插入测试数据
INSERT INTO travel_feelings (user_id, day_number, content) VALUES 
('default_user', 1, '今天在南京玩得很开心，夫子庙的夜景很美！'),
('default_user', 2, '中山陵很壮观，爬了392级台阶，虽然累但很值得！'),
('test_user', 1, '第一次来南京，被这座城市的历史文化深深震撼了。'),
('test_user', 2, '南京博物院里的文物太精美了，学到了很多历史知识。');

-- 查看表结构
DESCRIBE travel_feelings;

-- 查看数据
SELECT * FROM travel_feelings ORDER BY user_id, day_number;
