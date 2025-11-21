package com.tourism.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS跨域配置类（Config层）
 * 
 * 作用：
 * - 配置跨域资源共享（CORS）策略
 * - 允许前端应用访问后端API
 * - 处理预检请求和跨域响应头
 * 
 * 配置内容：
 * - 允许的源：所有源（*）
 * - 允许的方法：GET、POST、PUT、DELETE、OPTIONS
 * - 允许的头部：所有头部（*）
 * - 是否允许凭证：是
 * - 预检请求缓存时间：3600秒
 * 
 * 实现方式：
 * 1. 实现WebMvcConfigurer接口，重写addCorsMappings方法
 * 2. 提供CorsConfigurationSource Bean
 * 
 * 使用场景：
 * - 前端应用与后端API分离部署
 * - 不同域名间的API调用
 * - 开发环境的前后端联调
 * 
 * 注意事项：
 * - 生产环境建议限制允许的源
 * - 注意allowCredentials与allowedOrigins的兼容性
 * - 使用allowedOriginPatterns替代allowedOrigins避免通配符问题
 * 
 * 注解说明：
 * @Configuration - 标识为配置类
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("*");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
