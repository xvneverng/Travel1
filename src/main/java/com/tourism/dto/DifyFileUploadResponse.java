package com.tourism.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Dify文件上传响应DTO
 * 用于封装Dify文件上传API的响应数据
 * 
 * 对应API文档：
 * POST https://api.dify.ai/v1/files/upload
 * 
 * 响应格式：
 * {
 *   "id": "...",
 *   "name": "...",
 *   "size": 1024,
 *   "extension": "png",
 *   "mime_type": "image/png",
 *   "created_by": 123,
 *   "created_at": 1577836800
 * }
 * 
 * @author tourism
 * @since 2025-01-27
 */
@Data
public class DifyFileUploadResponse {
    
    /**
     * 文件ID
     * 用于后续在工作流中引用此文件
     */
    private String id;
    
    /**
     * 文件名
     */
    private String name;
    
    /**
     * 文件大小（字节）
     */
    private Long size;
    
    /**
     * 文件扩展名
     */
    private String extension;
    
    /**
     * MIME类型
     */
    @JsonProperty("mime_type")
    private String mimeType;
    
    /**
     * 创建者ID
     */
    @JsonProperty("created_by")
    private Long createdBy;
    
    /**
     * 创建时间（Unix时间戳）
     */
    @JsonProperty("created_at")
    private Long createdAt;
}

