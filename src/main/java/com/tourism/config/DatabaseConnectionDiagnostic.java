package com.tourism.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * 数据库连接诊断类
 */
@Component
public class DatabaseConnectionDiagnostic implements CommandLineRunner {
    
    @Autowired
    private DataSource dataSource;
    
    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== 数据库连接诊断开始 ===");
        
        try {
            // 1. 测试基本连接
            System.out.println("1. 测试数据库连接...");
            Connection connection = dataSource.getConnection();
            System.out.println("✅ 数据库连接成功！");
            
            // 2. 获取数据库信息
            System.out.println("2. 获取数据库信息...");
            DatabaseMetaData metaData = connection.getMetaData();
            System.out.println("数据库产品名称: " + metaData.getDatabaseProductName());
            System.out.println("数据库版本: " + metaData.getDatabaseProductVersion());
            System.out.println("驱动名称: " + metaData.getDriverName());
            System.out.println("驱动版本: " + metaData.getDriverVersion());
            System.out.println("连接URL: " + metaData.getURL());
            System.out.println("用户名: " + metaData.getUserName());
            
            // 3. 检查数据库是否存在
            System.out.println("3. 检查数据库...");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT DATABASE()");
            if (resultSet.next()) {
                String currentDb = resultSet.getString(1);
                System.out.println("当前数据库: " + currentDb);
            }
            
            // 4. 检查表是否存在
            System.out.println("4. 检查表是否存在...");
            resultSet = statement.executeQuery("SHOW TABLES LIKE 'travel_feelings'");
            if (resultSet.next()) {
                System.out.println("✅ travel_feelings表存在");
                
                // 5. 检查表结构
                System.out.println("5. 检查表结构...");
                resultSet = statement.executeQuery("DESCRIBE travel_feelings");
                System.out.println("表结构:");
                while (resultSet.next()) {
                    System.out.println("  " + resultSet.getString("Field") + " - " + resultSet.getString("Type"));
                }
                
                // 6. 检查数据
                System.out.println("6. 检查数据...");
                resultSet = statement.executeQuery("SELECT COUNT(*) as count FROM travel_feelings");
                if (resultSet.next()) {
                    System.out.println("表中记录数: " + resultSet.getInt("count"));
                }
                
            } else {
                System.out.println("❌ travel_feelings表不存在");
                System.out.println("请执行 create_tables.sql 创建表");
            }
            
            // 7. 测试基本查询
            System.out.println("7. 测试基本查询...");
            resultSet = statement.executeQuery("SELECT 1 as test");
            if (resultSet.next()) {
                System.out.println("✅ 基本查询测试成功: " + resultSet.getInt("test"));
            }
            
            // 关闭连接
            resultSet.close();
            statement.close();
            connection.close();
            
            System.out.println("=== 数据库连接诊断完成 ===");
            
        } catch (Exception e) {
            System.err.println("❌ 数据库连接失败！");
            System.err.println("错误信息: " + e.getMessage());
            System.err.println("错误类型: " + e.getClass().getSimpleName());
            
            // 提供解决建议
            System.err.println("\n=== 可能的解决方案 ===");
            System.err.println("1. 检查MySQL服务是否启动");
            System.err.println("2. 检查用户名密码是否正确");
            System.err.println("3. 检查数据库是否存在");
            System.err.println("4. 检查端口3306是否被占用");
            System.err.println("5. 检查防火墙设置");
            System.err.println("6. 检查MySQL配置文件");
            
            // 打印详细错误信息
            e.printStackTrace();
        }
    }
}
