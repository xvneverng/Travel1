package com.tourism.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 高德地图API配置类（Config层）
 * 
 * 作用：
 * - 管理高德地图API相关配置
 * - 从application.yml读取配置参数
 * - 提供API调用所需的配置信息
 * 
 * 配置属性：
 * - key: 高德地图API密钥（必填）
 * - baseUrl: API基础URL
 * - geocodeUrl: 地理编码API路径
 * - directionUrl: 路径规划API路径
 * - placeSearchUrl: 地点搜索API路径
 * 
 * 配置来源：
 * - application.yml文件中的amap.api配置节
 * - 支持环境变量和外部配置覆盖
 * 
 * 使用场景：
 * - AmapApiUtil工具类获取API配置
 * - 不同环境使用不同的API密钥
 * - 配置热更新和动态调整
 * 
 * 注意事项：
 * - API密钥需要在高德开放平台申请
 * - 确保密钥类型为"Web服务"
 * - 注意API调用频率限制
 * 
 * 注解说明：
 * @ConfigurationProperties - 绑定配置文件属性
 * @Component - 标识为Spring组件
 * @Data - Lombok注解，自动生成getter/setter等方法
 */
@Data
@Component
@ConfigurationProperties(prefix = "amap.api")
public class AmapConfig {
    
    /**
     * 高德地图API密钥
     */
    private String key;
    
    /**
     * API基础URL
     */
    private String baseUrl;
    
    /**
     * 地理编码API路径
     */
    private String geocodeUrl;
    
    /**
     * 路径规划API路径
     */
    private String directionUrl;
    
    /**
     * 地点搜索API路径
     */
    private String placeSearchUrl;
}
