package com.tourism.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.util.Map;

/**
 * Dify工作流请求DTO
 * 用于封装调用Dify工作流API的请求参数
 * 
 * 对应API文档：
 * POST https://api.dify.ai/v1/workflows/run
 * 
 * @author tourism
 * @since 2025-01-27
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DifyWorkflowRequest {
    
    /**
     * 工作流输入参数
     * 键为变量名，值为变量值
     * 如果变量需要文件输入，值应为文件输入对象列表
     */
    private Map<String, Object> inputs;
    
    /**
     * 响应模式
     * - "blocking": 阻塞模式，等待工作流执行完成后返回完整结果
     * - "streaming": 流式模式，实时返回工作流执行过程中的事件流
     */
    private String responseMode = "blocking";
    
    /**
     * 用户标识
     * 用于标识调用API的用户，可以是用户ID或会话ID
     */
    private String user;
    
    /**
     * 工作流ID（可选）
     * 如果指定，则调用指定的工作流
     * 如果不指定，则使用默认工作流
     */
    private String workflowId;
}

/**
 * 文件输入对象
 * 用于在工作流输入中传递文件
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
class FileInput {
    
    /**
     * 传输方式
     * - "local_file": 使用已上传的文件ID
     * - "remote_url": 使用远程URL
     */
    private String transferMethod;
    
    /**
     * 上传的文件ID（当transferMethod为"local_file"时使用）
     */
    private String uploadFileId;
    
    /**
     * 远程文件URL（当transferMethod为"remote_url"时使用）
     */
    private String url;
    
    /**
     * 文件类型
     * - "document": 文档类型
     * - "image": 图片类型
     * - "audio": 音频类型
     * - "video": 视频类型
     */
    private String type;
}

