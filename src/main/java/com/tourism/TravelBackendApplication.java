package com.tourism;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 旅游路线规划后端服务主启动类
 * 
 * 作用：
 * - Spring Boot应用的入口点
 * - 自动配置Spring容器和Web服务器
 * - 启动内嵌Tomcat服务器，默认端口8080
 * 
 * 用法：
 * - 直接运行main方法启动应用
 * - 或使用命令：mvn spring-boot:run
 * - 访问地址：http://localhost:8080/api
 * 
 * 注解说明：
 * @SpringBootApplication - 组合注解，包含：
 *   - @Configuration：标识为配置类
 *   - @EnableAutoConfiguration：启用自动配置
 *   - @ComponentScan：扫描当前包及子包下的组件
 */


 @SpringBootApplication(scanBasePackages = "com.tourism")
public class TravelBackendApplication {

    /**
     * 应用程序主入口
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(TravelBackendApplication.class, args);
    }
}
