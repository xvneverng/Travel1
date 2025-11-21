package com.tourism.config;

import com.tourism.mapper.TravelItineraryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 数据库连接测试类
 * 应用启动时自动测试数据库连接
 */
@Component
public class DatabaseConnectionTest implements CommandLineRunner {
    
    @Autowired
    private TravelItineraryMapper travelItineraryMapper;
    
    @Override
    public void run(String... args) throws Exception {
        try {
            // 测试MyBatis Plus连接
            Long count = travelItineraryMapper.selectCount(null);
            System.out.println("✅ MyBatis Plus连接成功，travel_itinerary表记录数: " + count);
            
        } catch (Exception e) {
            System.err.println("❌ 数据库连接失败: " + e.getMessage());
            System.err.println("请检查:");
            System.err.println("1. MySQL服务是否启动");
            System.err.println("2. 数据库连接配置是否正确");
            System.err.println("3. 用户名密码是否正确");
            System.err.println("4. 数据库travel_db是否存在");
            System.err.println("5. travel_itinerary表是否存在");
        }
    }
}
