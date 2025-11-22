package com.tourism.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tourism.config.DifyConfig;
import com.tourism.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.*;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;

/**
 * Dify服务类
 * 负责与Dify API进行交互，包括文件上传、工作流运行等功能
 * 
 * 主要功能：
 * 1. 文件上传到Dify平台
 * 2. 运行Dify工作流（支持阻塞和流式两种模式）
 * 3. 查询工作流运行状态
 * 4. 停止工作流任务
 * 
 * API文档参考：
 * - 文件上传: POST /v1/files/upload
 * - 工作流运行: POST /v1/workflows/run
 * - 查询运行状态: GET /v1/workflows/run/:workflow_run_id
 * - 停止任务: POST /v1/workflows/tasks/:task_id/stop
 * 
 * @author tourism
 * @since 2025-01-27
 */
@Slf4j
@Service
public class DifyService {
    
    @Autowired
    private DifyConfig difyConfig;
    
    @Autowired
    private WebClient.Builder webClientBuilder;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 上传文件到Dify平台
     * 
     * @param filePath 文件路径
     * @param user 用户标识
     * @param fileType 文件类型（TXT, IMAGE, AUDIO, VIDEO等）
     * @return 上传后的文件ID，失败返回null
     */
    public String uploadFile(String filePath, String user, String fileType) {
        try {
            log.info("开始上传文件到Dify: filePath={}, user={}, fileType={}", filePath, user, fileType);
            
            // 检查API密钥
            String apiKey = difyConfig.getApiKey();
            if (apiKey == null || apiKey.trim().isEmpty()) {
                log.error("❌ Dify API密钥未配置或为空！");
                return null;
            }
            apiKey = apiKey.trim();
            
            // 构建文件上传URL
            String uploadUrl = difyConfig.getBaseUrl() + "/files/upload";
            
            // 创建文件资源
            File file = new File(filePath);
            if (!file.exists()) {
                log.error("文件不存在: {}", filePath);
                return null;
            }
            
            // 根据文件类型确定MIME类型
            String mimeType = getMimeType(fileType, file.getName());
            
            // 构建Multipart请求体
            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part("file", new FileSystemResource(file))
                    .contentType(MediaType.parseMediaType(mimeType));
            builder.part("user", user);
            builder.part("type", fileType);
            
            // 发送请求
            DifyFileUploadResponse response = webClientBuilder.build()
                    .post()
                    .uri(uploadUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(builder.build()))
                    .retrieve()
                    .bodyToMono(DifyFileUploadResponse.class)
                    .timeout(Duration.ofSeconds(30))
                    .block();
            
            if (response != null && response.getId() != null) {
                log.info("文件上传成功: fileId={}", response.getId());
                return response.getId();
            } else {
                log.error("文件上传失败: 响应为空或缺少文件ID");
                return null;
            }
            
        } catch (Exception e) {
            log.error("上传文件异常: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 运行Dify工作流（阻塞模式）
     * 等待工作流执行完成后返回完整结果
     * 
     * @param inputs 工作流输入参数
     * @param user 用户标识
     * @param workflowId 工作流ID（可选）
     * @return 工作流执行结果
     */
    public DifyWorkflowResponse runWorkflowBlocking(Map<String, Object> inputs, String user, String workflowId) {
        try {
            log.info("开始运行Dify工作流（阻塞模式）: user={}, workflowId={}", user, workflowId);
            log.info("[DifyService] Blocking-Step1: 进入 runWorkflowBlocking 方法");
            
            // 构建工作流运行URL（final变量，用于lambda表达式）
            final String workflowUrl;
            if (StringUtils.hasText(workflowId)) {
                workflowUrl = difyConfig.getBaseUrl() + "/workflows/" + workflowId + "/run";
                log.info("[DifyService] Blocking-Step2: 使用指定 workflowId 构建 URL = {}", workflowUrl);
            } else {
                workflowUrl = difyConfig.getBaseUrl() + "/workflows/run";
                log.info("[DifyService] Blocking-Step2: 使用默认工作流 URL = {}", workflowUrl);
            }
            
            // 检查API密钥
            String apiKey = difyConfig.getApiKey();
            if (apiKey == null || apiKey.trim().isEmpty()) {
                log.error("❌ Dify API密钥未配置或为空！");
                log.error("[DifyService] Blocking-Step3-Error: apiKey 为空，直接返回 null");
                return null;
            }

            // 去除可能的空格和换行符
            apiKey = apiKey.trim();
            log.info("[DifyService] Blocking-Step3: apiKey 去除空白后长度={}, 前缀={}", apiKey.length(),
                    apiKey.length() > 8 ? apiKey.substring(0, 8) + "..." : apiKey);
            
            // 验证API密钥格式
            if (!apiKey.startsWith("app-")) {
                log.error("❌ API密钥格式错误！应该以'app-'开头");
                log.error("[DifyService] Blocking-Step4-Error: apiKey 格式错误, 当前前缀={}",
                        apiKey.length() > 8 ? apiKey.substring(0, 8) + "..." : apiKey);
                return null;
            }
            log.info("[DifyService] Blocking-Step4: apiKey 格式校验通过");
            
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("inputs", inputs != null ? inputs : new HashMap<>());
            requestBody.put("response_mode", "blocking");
            requestBody.put("user", user);
            log.info("[DifyService] Blocking-Step5: 已构建 requestBody, inputs 是否为空={}, user={}",
                    (inputs == null ? "true" : "false"), user);
            
            // 详细记录请求体内容（用于调试）
            try {
                String requestBodyJson = objectMapper.writeValueAsString(requestBody);
                log.info("📤 发送请求体到Dify API (阻塞模式):");
                log.info("   URL: {}", workflowUrl);
                log.info("   Method: POST");
                log.info("   Body: {}", requestBodyJson);
                log.info("   Inputs详情: {}", objectMapper.writeValueAsString(inputs));
                log.info("[DifyService] Blocking-Step6: 请求体序列化成功，准备通过 WebClient 发送请求");
            } catch (Exception e) {
                log.warn("序列化请求体失败: {}", e.getMessage());
                log.warn("[DifyService] Blocking-Step6-Warn: 请求体序列化失败，但仍继续调用 WebClient");
            }
            
            // 发送请求
            log.info("[DifyService] Blocking-Step7: 开始通过 WebClient 发送请求到 Dify, url={}", workflowUrl);
            DifyWorkflowResponse response = webClientBuilder.build()
                    .post()
                    .uri(workflowUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(status -> status.isError(), errorResponse -> {
                        int statusCode = errorResponse.statusCode().value();
                        log.error("❌ Dify API调用失败 (阻塞模式): status={}, url={}", statusCode, workflowUrl);
                        
                        if (statusCode == 400) {
                            log.error("❌ 400 Bad Request - 请求参数错误！");
                            log.error("当前inputs: {}", inputs);
                        }
                        
                        log.error("[DifyService] Blocking-Step8-Error: 收到 Dify 非 2xx 状态码, status={}", statusCode);
                        return errorResponse.bodyToMono(String.class)
                                .defaultIfEmpty("无响应体")
                                .flatMap(body -> {
                                    log.error("📥 Dify API错误响应体: {}", body);
                                    log.error("[DifyService] Blocking-Step8-Error: 错误响应体已记录");
                                    return Mono.error(new RuntimeException("Dify API调用失败: " + statusCode + "，错误: " + body));
                                });
                    })
                    .bodyToMono(DifyWorkflowResponse.class)
                    .timeout(Duration.ofSeconds(difyConfig.getTimeout() / 1000))
                    .block();
            log.info("[DifyService] Blocking-Step9: WebClient 调用结束, response 是否为空={}", response == null ? "true" : "false");
            
            if (response != null) {
                log.info("工作流执行成功: workflowRunId={}, status={}", 
                        response.getWorkflowRunId(), 
                        response.getData() != null ? response.getData().getStatus() : "unknown");
                log.info("[DifyService] Blocking-Step10: response 非空，已记录 workflowRunId 和 status");
            } else {
                log.error("工作流执行失败: 响应为空");
                log.error("[DifyService] Blocking-Step10-Error: response 为空，可能是 WebClient 超时或异常");
            }
            
            return response;
            
        } catch (Exception e) {
            log.error("运行工作流异常: {}", e.getMessage(), e);
            log.error("[DifyService] Blocking-Step-Exception: runWorkflowBlocking 捕获到异常, message={}", e.getMessage());
            return null;
        }
    }
    
    /**
     * 运行Dify工作流（流式模式）
     * 实时返回工作流执行过程中的事件流
     * 
     * @param inputs 工作流输入参数
     * @param user 用户标识
     * @param workflowId 工作流ID（可选）
     * @param sseEmitter SSE发射器，用于向前端推送流式数据
     */
    public void runWorkflowStreaming(Map<String, Object> inputs, String user, String workflowId, SseEmitter sseEmitter) {
        try {
            log.info("开始运行Dify工作流（流式模式）: user={}, workflowId={}", user, workflowId);
            
            // 检查API密钥
            String apiKey = difyConfig.getApiKey();
            if (apiKey == null || apiKey.trim().isEmpty()) {
                log.error("❌ Dify API密钥未配置或为空！");
                log.error("请在application.yml中配置: dify.api.api-key");
                try {
                    sseEmitter.send(SseEmitter.event()
                            .name("error")
                            .data("{\"error\":\"API密钥未配置，请在application.yml中配置dify.api.api-key\"}"));
                    sseEmitter.completeWithError(new RuntimeException("API密钥未配置"));
                } catch (Exception e) {
                    log.error("发送错误事件失败: {}", e.getMessage());
                }
                return;
            }
            
            // 去除可能的空格和换行符
            apiKey = apiKey.trim();
            
            // 验证API密钥格式
            if (!apiKey.startsWith("app-")) {
                log.error("❌ API密钥格式错误！应该以'app-'开头，当前: {}", apiKey.length() > 10 ? apiKey.substring(0, 10) + "..." : apiKey);
                try {
                    sseEmitter.send(SseEmitter.event()
                            .name("error")
                            .data("{\"error\":\"API密钥格式错误，应该以'app-'开头\"}"));
                    sseEmitter.completeWithError(new RuntimeException("API密钥格式错误"));
                } catch (Exception e) {
                    log.error("发送错误事件失败: {}", e.getMessage());
                }
                return;
            }
            
            // 构建工作流运行URL（final变量，用于lambda表达式）
            final String workflowUrl;
            if (StringUtils.hasText(workflowId)) {
                workflowUrl = difyConfig.getBaseUrl() + "/workflows/" + workflowId + "/run";
            } else {
                workflowUrl = difyConfig.getBaseUrl() + "/workflows/run";
            }
            
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("inputs", inputs != null ? inputs : new HashMap<>());
            requestBody.put("response_mode", "streaming");
            requestBody.put("user", user);
            
            // 记录API密钥的前几位（用于调试，不记录完整密钥）
            final String apiKeyPrefix = apiKey.length() > 12 ? apiKey.substring(0, 12) + "..." : apiKey;
            log.info("✅ 使用API密钥: {}", apiKeyPrefix);
            log.info("✅ 请求URL: {}", workflowUrl);
            
            // 详细记录请求体内容（用于调试）
            try {
                String requestBodyJson = objectMapper.writeValueAsString(requestBody);
                log.info("📤 发送请求体到Dify API:");
                log.info("   URL: {}", workflowUrl);
                log.info("   Method: POST");
                log.info("   Headers: Authorization=Bearer {}, Content-Type=application/json, Accept=text/event-stream", apiKeyPrefix);
                log.info("   Body: {}", requestBodyJson);
                log.info("   Inputs详情: {}", objectMapper.writeValueAsString(inputs));
            } catch (Exception e) {
                log.warn("序列化请求体失败: {}", e.getMessage());
            }
            
            // 发送流式请求
            // 注意：Dify API返回的是text/event-stream格式的SSE流
            Flux<DataBuffer> responseFlux = webClientBuilder.build()
                    .post()
                    .uri(workflowUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .accept(MediaType.TEXT_EVENT_STREAM) // 接受SSE流
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(status -> status.isError(), response -> {
                        int statusCode = response.statusCode().value();
                        log.error("❌ Dify API调用失败: status={}, url={}", statusCode, workflowUrl);
                        
                        // 特殊处理401错误
                        if (statusCode == 401) {
                            log.error("❌ 401 Unauthorized - API密钥认证失败！");
                            log.error("请检查：");
                            log.error("1. application.yml中的dify.api.api-key配置是否正确");
                            log.error("2. API密钥是否有效（在Dify平台验证）");
                            log.error("3. API密钥格式是否正确（应该以'app-'开头）");
                            log.error("当前使用的API密钥前缀: {}", apiKeyPrefix);
                        } else if (statusCode == 400) {
                            log.error("❌ 400 Bad Request - 请求参数错误！");
                            log.error("请检查：");
                            log.error("1. 工作流输入参数是否符合要求");
                            log.error("2. 必填字段是否都已提供");
                            log.error("3. 参数格式是否正确（所有参数应为字符串类型）");
                            log.error("当前inputs: {}", inputs);
                        } else if (statusCode == 404) {
                            log.error("❌ 404 Not Found - 工作流不存在！");
                            log.error("请检查工作流ID是否正确: {}", workflowId != null ? workflowId : "使用默认工作流");
                        }
                        
                        return response.bodyToMono(String.class)
                                .defaultIfEmpty("无响应体")
                                .flatMap(body -> {
                                    log.error("📥 Dify API错误响应体: {}", body);
                                    
                                    String errorMessage;
                                    if (statusCode == 401) {
                                        errorMessage = "API密钥认证失败，请检查application.yml中的dify.api.api-key配置是否正确";
                                    } else if (statusCode == 400) {
                                        errorMessage = "请求参数错误，请检查工作流输入参数是否符合要求。错误详情: " + (body.length() > 200 ? body.substring(0, 200) : body);
                                    } else if (statusCode == 404) {
                                        errorMessage = "工作流不存在，请检查工作流ID是否正确";
                                    } else if (statusCode == 429) {
                                        errorMessage = "API调用频率超限，请稍后重试";
                                    } else {
                                        errorMessage = "Dify API调用失败: " + statusCode + "，错误详情: " + (body.length() > 200 ? body.substring(0, 200) : body);
                                    }
                                    
                                    try {
                                        // 转义JSON字符串中的特殊字符
                                        String escapedMessage = errorMessage.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
                                        sseEmitter.send(SseEmitter.event()
                                                .name("error")
                                                .data("{\"error\":\"" + escapedMessage + "\",\"statusCode\":" + statusCode + "}"));
                                    } catch (Exception e) {
                                        log.error("发送错误事件失败: {}", e.getMessage());
                                    }
                                    return Mono.error(new RuntimeException(errorMessage));
                                });
                    })
                    .bodyToFlux(DataBuffer.class);
            
            // 处理流式响应
            responseFlux
                    .timeout(Duration.ofSeconds(Math.max(difyConfig.getTimeout() / 1000, 300))) // 至少300秒超时（5分钟）
                    .doOnSubscribe(subscription -> {
                        log.info("开始订阅Dify工作流响应流");
                    })
                    .doOnError(error -> {
                        log.error("工作流流式响应异常: {}", error.getMessage(), error);
                        log.error("异常堆栈: ", error);
                        try {
                            String errorMsg = error.getMessage() != null ? error.getMessage() : "未知错误";
                            // 转义JSON字符串中的特殊字符
                            errorMsg = errorMsg.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
                            sseEmitter.send(SseEmitter.event()
                                    .name("error")
                                    .data("{\"error\":\"" + errorMsg + "\"}"));
                            sseEmitter.completeWithError(error);
                        } catch (Exception e) {
                            log.error("发送错误事件失败: {}", e.getMessage(), e);
                            try {
                                sseEmitter.completeWithError(error);
                            } catch (Exception ex) {
                                log.error("完成SSE连接失败: {}", ex.getMessage());
                            }
                        }
                    })
                    .subscribe(
                            dataBuffer -> {
                                try {
                                    // 读取数据缓冲区内容
                                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                                    dataBuffer.read(bytes);
                                    DataBufferUtils.release(dataBuffer);
                                    
                                    // 将字节转换为字符串
                                    String data = new String(bytes, StandardCharsets.UTF_8);
                                    
                                    // 处理SSE格式的数据
                                    // SSE格式: "data: {json}\n\n" 或 "data: {json}\n"
                                    // 需要累积数据，因为一个DataBuffer可能包含不完整的数据
                                    String[] lines = data.split("\n");
                                    for (String line : lines) {
                                        line = line.trim();
                                        if (line.startsWith("data: ")) {
                                            String jsonData = line.substring(6); // 去掉 "data: " 前缀
                                            
                                            if (jsonData.isEmpty()) {
                                                continue; // 跳过空数据行
                                            }
                                            
                                            // 解析JSON数据
                                            try {
                                                JsonNode jsonNode = objectMapper.readTree(jsonData);
                                                String eventType = jsonNode.has("event") ? jsonNode.get("event").asText() : "message";
                                                
                                                // 详细记录工作流完成事件的数据
                                                if ("workflow_finished".equals(eventType)) {
                                                    log.info("📥 工作流完成事件数据:");
                                                    log.info("   完整JSON: {}", jsonData);
                                                    if (jsonNode.has("data")) {
                                                        JsonNode dataNode = jsonNode.get("data");
                                                        log.info("   data字段: {}", dataNode.toString());
                                                        if (dataNode.has("outputs")) {
                                                            JsonNode outputsNode = dataNode.get("outputs");
                                                            log.info("   outputs字段: {}", outputsNode.toString());
                                                            // 遍历所有输出字段
                                                            if (outputsNode.isObject()) {
                                                                log.info("   outputs字段列表:");
                                                                outputsNode.fields().forEachRemaining(entry -> {
                                                                    log.info("     - {}: {}", entry.getKey(), entry.getValue().toString());
                                                                });
                                                            }
                                                        } else {
                                                            log.warn("   ⚠️ outputs字段不存在！");
                                                        }
                                                    } else {
                                                        log.warn("   ⚠️ data字段不存在！");
                                                    }
                                                }
                                                
                                                // 发送SSE事件到前端
                                                sseEmitter.send(SseEmitter.event()
                                                        .name(eventType)
                                                        .data(jsonData));
                                                
                                                log.debug("发送SSE事件: event={}", eventType);
                                                
                                                // 如果是工作流完成事件，关闭连接
                                                if ("workflow_finished".equals(eventType)) {
                                                    log.info("工作流执行完成，关闭SSE连接");
                                                    sseEmitter.complete();
                                                    return;
                                                }
                                                
                                            } catch (JsonProcessingException e) {
                                                log.warn("解析SSE数据失败: {}", jsonData, e);
                                                // 即使解析失败，也尝试发送原始数据
                                                sseEmitter.send(SseEmitter.event()
                                                        .name("message")
                                                        .data(jsonData));
                                            }
                                        }
                                    }
                                    
                                } catch (Exception e) {
                                    log.error("处理流式数据异常: {}", e.getMessage(), e);
                                }
                            },
                            error -> {
                                log.error("流式响应订阅异常: {}", error.getMessage(), error);
                                try {
                                    sseEmitter.send(SseEmitter.event()
                                            .name("error")
                                            .data("{\"error\":\"" + error.getMessage() + "\"}"));
                                    sseEmitter.completeWithError(error);
                                } catch (Exception e) {
                                    log.error("发送错误事件失败: {}", e.getMessage());
                                }
                            },
                            () -> {
                                log.info("流式响应完成");
                                sseEmitter.complete();
                            }
                    );
            
        } catch (Exception e) {
            log.error("运行工作流（流式）异常: {}", e.getMessage(), e);
            try {
                sseEmitter.send(SseEmitter.event()
                        .name("error")
                        .data("{\"error\":\"" + e.getMessage() + "\"}"));
                sseEmitter.completeWithError(e);
            } catch (Exception ex) {
                log.error("发送错误事件失败: {}", ex.getMessage());
            }
        }
    }
    
    /**
     * 查询工作流运行状态
     * 
     * @param workflowRunId 工作流运行ID
     * @return 工作流运行状态数据
     */
    public DifyWorkflowResponse.WorkflowRunData getWorkflowRunStatus(String workflowRunId) {
        try {
            log.info("查询工作流运行状态: workflowRunId={}", workflowRunId);
            
            // 检查API密钥
            String apiKey = difyConfig.getApiKey();
            if (apiKey == null || apiKey.trim().isEmpty()) {
                log.error("❌ Dify API密钥未配置或为空！");
                return null;
            }
            apiKey = apiKey.trim();
            
            String statusUrl = difyConfig.getBaseUrl() + "/workflows/run/" + workflowRunId;
            
            DifyWorkflowResponse response = webClientBuilder.build()
                    .get()
                    .uri(statusUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .retrieve()
                    .bodyToMono(DifyWorkflowResponse.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();
            
            if (response != null && response.getData() != null) {
                log.info("查询成功: status={}", response.getData().getStatus());
                return response.getData();
            } else {
                log.error("查询失败: 响应为空");
                return null;
            }
            
        } catch (Exception e) {
            log.error("查询工作流状态异常: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 停止工作流任务
     * 
     * @param taskId 任务ID
     * @param user 用户标识
     * @return 是否成功
     */
    public boolean stopWorkflowTask(String taskId, String user) {
        try {
            log.info("停止工作流任务: taskId={}, user={}", taskId, user);
            
            // 检查API密钥
            String apiKey = difyConfig.getApiKey();
            if (apiKey == null || apiKey.trim().isEmpty()) {
                log.error("❌ Dify API密钥未配置或为空！");
                return false;
            }
            apiKey = apiKey.trim();
            
            String stopUrl = difyConfig.getBaseUrl() + "/workflows/tasks/" + taskId + "/stop";
            
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("user", user);
            
            @SuppressWarnings("unchecked")
            Map<String, Object> response = webClientBuilder.build()
                    .post()
                    .uri(stopUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();
            
            if (response != null && "success".equals(response.get("result"))) {
                log.info("停止任务成功");
                return true;
            } else {
                log.error("停止任务失败: response={}", response);
                return false;
            }
            
        } catch (Exception e) {
            log.error("停止任务异常: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 根据文件类型和文件名获取MIME类型
     * 
     * @param fileType 文件类型（TXT, IMAGE等）
     * @param fileName 文件名
     * @return MIME类型
     */
    private String getMimeType(String fileType, String fileName) {
        if (fileType == null) {
            fileType = "TXT";
        }
        
        switch (fileType.toUpperCase()) {
            case "TXT":
                return "text/plain";
            case "IMAGE":
                String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
                switch (extension) {
                    case "png":
                        return "image/png";
                    case "jpg":
                    case "jpeg":
                        return "image/jpeg";
                    case "webp":
                        return "image/webp";
                    case "gif":
                        return "image/gif";
                    default:
                        return "image/png";
                }
            case "AUDIO":
                return "audio/mpeg";
            case "VIDEO":
                return "video/mp4";
            default:
                return "text/plain";
        }
    }
}

