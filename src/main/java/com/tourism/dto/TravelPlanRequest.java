package com.tourism.dto;

import lombok.Data;
import java.util.Map;

/**
 * 旅游计划请求DTO
 * 用于接收前端提交的旅游计划生成请求
 * 对应Dify工作流的结构化输出参数
 * 
 * @author tourism
 * @since 2025-01-27
 */
@Data
public class TravelPlanRequest {
    
    /**
     * 判断是否包含旅游相关意图
     * 1表示包含, 0表示不包含
     * 必填
     */
    private String intentType;
    
    /**
     * 解析出的人数信息
     * 无则为0
     * 必填
     */
    private String num;
    
    /**
     * 解析出的日期信息
     * 无则为0
     * 非必填
     */
    private String date;
    
    /**
     * 解析出的目的地信息
     * 无则为0
     * 必填
     */
    private String destination;
    
    /**
     * 解析出的旅行时长信息
     * 无则为0
     * 必填
     */
    private String duration;
    
    /**
     * 解析出的特殊需求信息
     * 无则为0
     * 必填
     */
    private String specialNeed;
    
    /**
     * 解析出的指令信息
     * 无则为0
     * 非必填
     */
    private String instruction;

    /**
     * 用户的原始输入（Dify工作流必需参数）
     * 工作流会从这个输入中解析出结构化信息并生成JSON输出
     */
    private String userInput;

    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 工作流输入参数（可选，用于传递额外的参数给Dify工作流）
     */
    private Map<String, Object> workflowInputs;
    
    /**
     * 是否使用流式响应
     * true: 流式响应（SSE）
     * false: 阻塞式响应（等待完整结果）
     */
    private Boolean streaming = false;
    
    /**
     * 上传的文件ID（如果工作流需要文件输入）
     */
    private String uploadFileId;
    
    /**
     * 工作流ID（可选，如果不指定则使用配置中的默认工作流）
     */
    private String workflowId;
}

