package com.tourism.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.Map;

/**
 * Dify流式事件DTO
 * 用于封装Dify工作流流式响应中的事件数据
 * 
 * 流式响应格式（SSE）：
 * data: {"event": "workflow_started", "task_id": "...", "workflow_run_id": "...", "data": {...}}
 * 
 * 事件类型：
 * - workflow_started: 工作流开始
 * - node_started: 节点开始
 * - node_finished: 节点完成
 * - workflow_finished: 工作流完成
 * - tts_message: TTS消息（文本转语音）
 * - tts_message_end: TTS消息结束
 * 
 * @author tourism
 * @since 2025-01-27
 */
@Data
public class DifyStreamEvent {
    
    /**
     * 事件类型
     */
    private String event;
    
    /**
     * 任务ID
     */
    @JsonProperty("task_id")
    private String taskId;
    
    /**
     * 工作流运行ID
     */
    @JsonProperty("workflow_run_id")
    private String workflowRunId;
    
    /**
     * 事件数据
     * 根据事件类型不同，data的结构也不同
     */
    private Map<String, Object> data;
    
    /**
     * 对话ID（TTS事件中使用）
     */
    @JsonProperty("conversation_id")
    private String conversationId;
    
    /**
     * 消息ID（TTS事件中使用）
     */
    @JsonProperty("message_id")
    private String messageId;
    
    /**
     * 音频数据（TTS事件中使用）
     */
    private String audio;
    
    /**
     * 创建时间（Unix时间戳）
     */
    @JsonProperty("created_at")
    private Long createdAt;
}

