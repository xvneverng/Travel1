package com.tourism.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * Dify API配置类（Config层）
 * 
 * 作用：
 * - 管理Dify API相关配置
 * - 从application.yml读取配置参数
 * - 提供API调用所需的配置信息
 * 
 * 配置属性：
 * - apiKey: Dify API密钥（必填）
 * - baseUrl: Dify API基础URL
 * - workflowApiUrl: 工作流API路径
 * - chatApiUrl: 聊天API路径
 * - timeout: 请求超时时间（毫秒）
 * 
 * 配置来源：
 * - application.yml文件中的dify.api配置节
 * - 支持环境变量和外部配置覆盖
 * 
 * 使用场景：
 * - DifyService服务类获取API配置
 * - 不同环境使用不同的API密钥
 * - 配置热更新和动态调整
 * 
 * 注意事项：
 * - API密钥需要在Dify平台申请
 * - 确保密钥有相应的工作流调用权限
 * - 注意API调用频率限制
 * 
 * 注解说明：
 * @ConfigurationProperties - 绑定配置文件属性
 * @Component - 标识为Spring组件
 * @Data - Lombok注解，自动生成getter/setter等方法
 */
@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "dify.api")
public class DifyConfig {
    
    /**
     * Dify API密钥
     */
    private String apiKey;
    
    /**
     * Dify API基础URL
     * 例如: https://api.dify.ai/v1
     */
    private String baseUrl;
    
    /**
     * 工作流API路径
     * 例如: /workflows/run
     */
    private String workflowApiUrl;
    
    /**
     * 聊天API路径
     * 例如: /chat-messages
     */
    private String chatApiUrl;
    
    /**
     * 请求超时时间（毫秒）
     * 默认: 30000 (30秒)
     */
    private Integer timeout = 30000;
    
    /**
     * 备用工作流ID
     * 如果API获取失败，使用此ID
     */
    private String fallbackWorkflowId;
    
    /**
     * 配置初始化后验证
     */
    @PostConstruct
    public void validate() {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            log.warn("⚠️  Dify API密钥未配置！请在application.yml中配置: dify.api.api-key");
        } else {
            String prefix = apiKey.length() > 8 ? apiKey.substring(0, 8) + "..." : apiKey;
            log.info("✅ Dify API配置已加载: apiKey={}, baseUrl={}", prefix, baseUrl);
        }
        
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            log.warn("⚠️  Dify API基础URL未配置！");
        }
    }
}

