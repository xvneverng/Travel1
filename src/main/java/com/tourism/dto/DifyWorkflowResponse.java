package com.tourism.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.Map;

/**
 * Dify工作流响应DTO
 * 用于封装Dify工作流API的响应数据
 * 
 * 对应API文档中的响应格式：
 * {
 *   "workflow_run_id": "...",
 *   "task_id": "...",
 *   "data": { ... }
 * }
 * 
 * @author tourism
 * @since 2025-01-27
 */
@Data
public class DifyWorkflowResponse {
    
    /**
     * 工作流运行ID
     */
    @JsonProperty("workflow_run_id")
    private String workflowRunId;
    
    /**
     * 任务ID
     */
    @JsonProperty("task_id")
    private String taskId;
    
    /**
     * 工作流运行数据
     */
    private WorkflowRunData data;
    
    /**
     * 工作流运行数据内部类
     */
    @Data
    public static class WorkflowRunData {
        
        /**
         * 运行ID
         */
        private String id;
        
        /**
         * 工作流ID
         */
        @JsonProperty("workflow_id")
        private String workflowId;
        
        /**
         * 运行状态
         * - "succeeded": 成功
         * - "failed": 失败
         * - "running": 运行中
         * - "stopped": 已停止
         */
        private String status;
        
        /**
         * 工作流输出结果
         * 键为输出变量名，值为输出值
         */
        private Map<String, Object> outputs;
        
        /**
         * 错误信息（如果执行失败）
         */
        private String error;
        
        /**
         * 执行耗时（秒）
         */
        @JsonProperty("elapsed_time")
        private Double elapsedTime;
        
        /**
         * 总token数
         */
        @JsonProperty("total_tokens")
        private Long totalTokens;
        
        /**
         * 总步骤数
         */
        @JsonProperty("total_steps")
        private Integer totalSteps;
        
        /**
         * 创建时间（Unix时间戳）
         */
        @JsonProperty("created_at")
        private Long createdAt;
        
        /**
         * 完成时间（Unix时间戳）
         */
        @JsonProperty("finished_at")
        private Long finishedAt;
    }
}

