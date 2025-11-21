package com.tourism.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import java.time.Duration;

/**
 * WebClient配置类（Config层）
 * 
 * 作用：
 * - 配置HTTP客户端WebClient
 * - 设置请求和响应的编解码器
 * - 提供统一的HTTP调用配置
 * 
 * 配置内容：
 * - 设置最大内存缓冲区大小为1MB
 * - 支持JSON编解码
 * - 支持异步非阻塞HTTP调用
 * 
 * 使用场景：
 * - AmapApiUtil工具类调用高德地图API
 * - 其他需要HTTP调用的场景
 * - 替代传统的RestTemplate
 * 
 * 技术优势：
 * - 响应式编程模型
 * - 非阻塞I/O操作
 * - 更好的性能和资源利用率
 * - 支持流式数据处理
 * 
 * 注解说明：
 * @Configuration - 标识为配置类
 * @Bean - 定义Spring Bean
 */
@Configuration
public class WebClientConfig {
    
    @Bean
    public WebClient.Builder webClientBuilder() {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(30));
                
        return WebClient.builder()
                .clientConnector(new org.springframework.http.client.reactive.ReactorClientHttpConnector(httpClient))
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024));
    }
}
