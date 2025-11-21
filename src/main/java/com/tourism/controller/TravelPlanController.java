package com.tourism.controller;

import com.tourism.dto.*;
import com.tourism.service.DifyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 旅游计划控制器
 * 用于保存和管理AI生成的旅游计划
 * 
 * 主要功能：
 * 1. 生成旅游计划（调用Dify工作流）
 * 2. 流式生成旅游计划（SSE实时推送）
 * 3. 上传文件到Dify
 * 4. 查询工作流运行状态
 * 5. 停止工作流任务
 * 6. 保存和获取旅游计划
 * 
 * @author tourism
 * @since 2025-01-27
 */
@Slf4j
@RestController
@RequestMapping("/api/travel/plan")
@CrossOrigin(originPatterns = "*")
public class TravelPlanController {
    
    @Autowired
    private DifyService difyService;
    
    // 临时存储（实际应该使用数据库）
    private final Map<String, TravelPlanRequest> planStorage = new HashMap<>();
    
    /**
     * 生成旅游计划（阻塞模式）
     * 调用Dify工作流生成旅游计划，等待完整结果返回
     * 
     * POST /api/travel/plan/generate
     * 
     * @param request 旅游计划请求，包含目的地、时长等信息
     * @return 工作流执行结果
     */
    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> generateTravelPlan(@RequestBody TravelPlanRequest request) {
        try {
            log.info("生成旅游计划（阻塞模式）: destination={}, duration={}, num={}, user={}", 
                    request.getDestination(), request.getDuration(), request.getNum(), request.getUserId());
            
            // 构建工作流输入参数（按照Dify工作流的结构化输出参数格式）
            Map<String, Object> inputs = new HashMap<>();
            
            log.info("📋 接收到的请求参数:");
            log.info("   intentType: {}", request.getIntentType());
            log.info("   num: {}", request.getNum());
            log.info("   date: {}", request.getDate());
            log.info("   destination: {}", request.getDestination());
            log.info("   duration: {}", request.getDuration());
            log.info("   specialNeed: {}", request.getSpecialNeed());
            log.info("   instruction: {}", request.getInstruction());
            
            // 必填字段
            // intent_type: 判断是否包含旅游相关意图, 1表示包含, 0表示不包含
            inputs.put("intent_type", request.getIntentType() != null ? request.getIntentType() : "1");
            
            // num: 解析出的人数信息, 无则为0
            inputs.put("num", request.getNum() != null ? request.getNum() : "0");
            
            // destination: 解析出的目的地信息, 无则为0
            inputs.put("destination", request.getDestination() != null ? request.getDestination() : "0");
            
            // duration: 解析出的旅行时长信息, 无则为0
            inputs.put("duration", request.getDuration() != null ? request.getDuration() : "0");
            
            // special_need: 解析出的特殊需求信息, 无则为0
            inputs.put("special_need", request.getSpecialNeed() != null ? request.getSpecialNeed() : "0");
            
            // 非必填字段
            // date: 解析出的日期信息, 无则为0
            if (request.getDate() != null) {
                inputs.put("date", request.getDate());
            } else {
                inputs.put("date", "0");
            }
            
            // instruction: 解析出的指令信息, 无则为0
            if (request.getInstruction() != null) {
                inputs.put("instruction", request.getInstruction());
            } else {
                inputs.put("instruction", "0");
            }
            
            // user_input: 用户的原始输入（Dify工作流必需参数）
            // 优先使用传入的userInput，否则使用instruction，最后使用默认值
            String finalUserInput;
            if (request.getUserInput() != null && !request.getUserInput().trim().isEmpty()) {
                finalUserInput = request.getUserInput().trim();
            } else if (request.getInstruction() != null && !request.getInstruction().equals("0") && !request.getInstruction().trim().isEmpty()) {
                finalUserInput = request.getInstruction().trim();
            } else if (request.getDestination() != null && !request.getDestination().equals("0")) {
                finalUserInput = "我想去" + request.getDestination() + "玩" + (request.getDuration() != null && !request.getDuration().equals("0") ? request.getDuration() + "天" : "");
            } else {
                finalUserInput = "我想规划一次旅行";
            }
            inputs.put("user_input", finalUserInput);
            
            // 如果有文件ID，添加文件输入
            if (request.getUploadFileId() != null) {
                // 根据工作流变量名添加文件输入
                // 注意：这里的变量名需要根据实际的Dify工作流配置来设置
                Map<String, Object> fileInput = new HashMap<>();
                fileInput.put("transfer_method", "local_file");
                fileInput.put("upload_file_id", request.getUploadFileId());
                fileInput.put("type", "document");
                
                // 假设工作流变量名为 "orig_mail" 或 "file_input"
                // 实际使用时需要根据工作流配置调整
                inputs.put("orig_mail", new Object[]{fileInput});
            }
            
            // 合并额外的输入参数
            if (request.getWorkflowInputs() != null) {
                inputs.putAll(request.getWorkflowInputs());
            }
            
            // 记录最终构建的inputs（用于调试）
            log.info("📦 构建的工作流输入参数:");
            for (Map.Entry<String, Object> entry : inputs.entrySet()) {
                log.info("   {} = {} (类型: {})", entry.getKey(), entry.getValue(), 
                        entry.getValue() != null ? entry.getValue().getClass().getSimpleName() : "null");
            }
            
            // 调用Dify工作流
            String user = request.getUserId() != null ? request.getUserId() : "default-user";
            DifyWorkflowResponse response = difyService.runWorkflowBlocking(
                    inputs, 
                    user, 
                    request.getWorkflowId()
            );
            
            if (response != null && response.getData() != null) {
                Map<String, Object> result = new HashMap<>();
                result.put("code", 200);
                result.put("message", "旅游计划生成成功");
                result.put("workflowRunId", response.getWorkflowRunId());
                result.put("taskId", response.getTaskId());
                result.put("status", response.getData().getStatus());
                result.put("outputs", response.getData().getOutputs());
                result.put("elapsedTime", response.getData().getElapsedTime());
                result.put("totalTokens", response.getData().getTotalTokens());
                
                log.info("旅游计划生成成功: workflowRunId={}, status={}", 
                        response.getWorkflowRunId(), response.getData().getStatus());
                return ResponseEntity.ok(result);
            } else {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("code", 500);
                errorResponse.put("message", "工作流执行失败：响应为空");
                return ResponseEntity.internalServerError().body(errorResponse);
            }
            
        } catch (Exception e) {
            log.error("生成旅游计划异常: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "生成失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * 生成旅游计划（流式模式）
     * 使用SSE实时推送工作流执行过程中的事件
     * 
     * GET /api/travel/plan/generate/stream?intent_type=1&num=2&destination=南京&duration=3&special_need=0&date=2025-02-01&instruction=0&userId=xxx
     * 
     * @param intentType 判断是否包含旅游相关意图, 1表示包含, 0表示不包含
     * @param num 解析出的人数信息, 无则为0
     * @param date 解析出的日期信息, 无则为0
     * @param destination 解析出的目的地信息, 无则为0
     * @param duration 解析出的旅行时长信息, 无则为0
     * @param specialNeed 解析出的特殊需求信息, 无则为0
     * @param instruction 解析出的指令信息, 无则为0
     * @param userId 用户ID
     * @param uploadFileId 上传的文件ID（可选）
     * @param workflowId 工作流ID（可选）
     * @return SSE流
     */
    @GetMapping(value = "/generate/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter generateTravelPlanStream(
            @RequestParam(required = true) String userInput,
            @RequestParam(required = false, defaultValue = "1") String intentType,
            @RequestParam(required = false, defaultValue = "0") String num,
            @RequestParam(required = false, defaultValue = "0") String date,
            @RequestParam(required = false, defaultValue = "0") String destination,
            @RequestParam(required = false, defaultValue = "0") String duration,
            @RequestParam(required = false, defaultValue = "0") String specialNeed,
            @RequestParam(required = false, defaultValue = "0") String instruction,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String uploadFileId,
            @RequestParam(required = false) String workflowId) {
        
        // 设置SSE超时时间（5分钟，300秒）
        // 注意：工作流可能需要较长时间执行，所以设置较长的超时时间
        SseEmitter sseEmitter = new SseEmitter(300000L);
        
        try {
            log.info("生成旅游计划（流式模式）: destination={}, duration={}, num={}, user={}", 
                    destination, duration, num, userId);
            
            // 记录接收到的参数（用于调试）
            log.info("📋 接收到的请求参数:");
            log.info("   userInput: {}", userInput);
            log.info("   intentType: {}", intentType);
            log.info("   num: {}", num);
            log.info("   date: {}", date);
            log.info("   destination: {}", destination);
            log.info("   duration: {}", duration);
            log.info("   specialNeed: {}", specialNeed);
            log.info("   instruction: {}", instruction);
            
            // 构建工作流输入参数
            // 注意：Dify工作流需要user_input参数（用户的原始输入）
            Map<String, Object> inputs = new HashMap<>();
            
            // 最重要的参数：user_input（用户的原始输入，Dify工作流必需）
            // 工作流会从这个输入中解析出结构化信息并生成JSON输出
            inputs.put("user_input", userInput != null && !userInput.trim().isEmpty() ? userInput.trim() : "我想规划一次旅行");
            
            // 可选的结构化参数（如果工作流支持，可以作为补充信息）
            // 如果工作流只需要user_input，这些参数可能不会被使用
            if (intentType != null && !intentType.equals("0")) {
                inputs.put("intent_type", intentType);
            }
            if (num != null && !num.equals("0")) {
                inputs.put("num", num);
            }
            if (date != null && !date.equals("0")) {
                inputs.put("date", date);
            }
            if (destination != null && !destination.equals("0")) {
                inputs.put("destination", destination);
            }
            if (duration != null && !duration.equals("0")) {
                inputs.put("duration", duration);
            }
            if (specialNeed != null && !specialNeed.equals("0")) {
                inputs.put("special_need", specialNeed);
            }
            if (instruction != null && !instruction.equals("0")) {
                inputs.put("instruction", instruction);
            }
            
            // 记录最终构建的inputs（用于调试）
            log.info("📦 构建的工作流输入参数:");
            for (Map.Entry<String, Object> entry : inputs.entrySet()) {
                log.info("   {} = {} (类型: {})", entry.getKey(), entry.getValue(), 
                        entry.getValue() != null ? entry.getValue().getClass().getSimpleName() : "null");
            }
            
            // 如果有文件ID，添加文件输入
            if (uploadFileId != null) {
                Map<String, Object> fileInput = new HashMap<>();
                fileInput.put("transfer_method", "local_file");
                fileInput.put("upload_file_id", uploadFileId);
                fileInput.put("type", "document");
                inputs.put("orig_mail", new Object[]{fileInput});
            }
            
            // 调用Dify工作流（流式模式）
            String user = userId != null ? userId : "default-user";
            difyService.runWorkflowStreaming(inputs, user, workflowId, sseEmitter);
            
            // 设置完成和错误回调
            sseEmitter.onCompletion(() -> {
                log.info("SSE连接完成");
            });
            
            sseEmitter.onError((ex) -> {
                log.error("SSE连接错误: {}", ex.getMessage(), ex);
            });
            
            sseEmitter.onTimeout(() -> {
                log.warn("SSE连接超时");
                sseEmitter.complete();
            });
            
        } catch (Exception e) {
            log.error("启动流式生成异常: {}", e.getMessage(), e);
            try {
                sseEmitter.send(SseEmitter.event()
                        .name("error")
                        .data("{\"error\":\"" + e.getMessage() + "\"}"));
                sseEmitter.completeWithError(e);
            } catch (Exception ex) {
                log.error("发送错误事件失败: {}", ex.getMessage());
            }
        }
        
        return sseEmitter;
    }
    
    /**
     * 上传文件到Dify
     * 
     * POST /api/travel/plan/upload
     * 
     * @param filePath 文件路径（临时方案，实际应该使用MultipartFile接收文件）
     * @param userId 用户ID
     * @param fileType 文件类型（TXT, IMAGE, AUDIO, VIDEO）
     * @return 上传结果，包含文件ID
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(
            @RequestParam String filePath,
            @RequestParam(required = false, defaultValue = "default-user") String userId,
            @RequestParam(required = false, defaultValue = "TXT") String fileType) {
        try {
            log.info("上传文件到Dify: filePath={}, user={}, fileType={}", filePath, userId, fileType);
            
            String fileId = difyService.uploadFile(filePath, userId, fileType);
            
            if (fileId != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 200);
                response.put("message", "文件上传成功");
                response.put("fileId", fileId);
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("code", 500);
                errorResponse.put("message", "文件上传失败");
                return ResponseEntity.internalServerError().body(errorResponse);
            }
            
        } catch (Exception e) {
            log.error("上传文件异常: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "上传失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * 查询工作流运行状态
     * 
     * GET /api/travel/plan/status/{workflowRunId}
     * 
     * @param workflowRunId 工作流运行ID
     * @return 工作流运行状态
     */
    @GetMapping("/status/{workflowRunId}")
    public ResponseEntity<Map<String, Object>> getWorkflowStatus(@PathVariable String workflowRunId) {
        try {
            log.info("查询工作流状态: workflowRunId={}", workflowRunId);
            
            DifyWorkflowResponse.WorkflowRunData status = difyService.getWorkflowRunStatus(workflowRunId);
            
            if (status != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 200);
                response.put("message", "查询成功");
                response.put("data", status);
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("code", 404);
                errorResponse.put("message", "工作流运行记录不存在");
                return ResponseEntity.status(404).body(errorResponse);
            }
            
        } catch (Exception e) {
            log.error("查询工作流状态异常: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * 停止工作流任务
     * 
     * POST /api/travel/plan/stop/{taskId}
     * 
     * @param taskId 任务ID
     * @param userId 用户ID
     * @return 停止结果
     */
    @PostMapping("/stop/{taskId}")
    public ResponseEntity<Map<String, Object>> stopWorkflow(
            @PathVariable String taskId,
            @RequestParam(required = false, defaultValue = "default-user") String userId) {
        try {
            log.info("停止工作流任务: taskId={}, user={}", taskId, userId);
            
            boolean success = difyService.stopWorkflowTask(taskId, userId);
            
            Map<String, Object> response = new HashMap<>();
            if (success) {
                response.put("code", 200);
                response.put("message", "任务已停止");
            } else {
                response.put("code", 500);
                response.put("message", "停止任务失败");
            }
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("停止任务异常: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "停止失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * 保存旅游计划
     * POST /api/travel/plan/save
     */
    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> saveTravelPlan(@RequestBody TravelPlanRequest request) {
        try {
            log.info("保存旅游计划: destination={}, duration={}", 
                    request.getDestination(), request.getDuration());
            
            // 生成计划ID
            String planId = UUID.randomUUID().toString().replace("-", "");
            
            // 保存计划（实际应该保存到数据库）
            planStorage.put(planId, request);
            
            // 返回计划ID和访问链接
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "旅游计划保存成功");
            response.put("planId", planId);
            response.put("link", "/?planId=" + planId);
            
            log.info("旅游计划保存成功: planId={}", planId);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("保存旅游计划异常: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "保存失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * 获取旅游计划
     * GET /api/travel/plan/{planId}
     */
    @GetMapping("/{planId}")
    public ResponseEntity<Map<String, Object>> getTravelPlan(@PathVariable String planId) {
        try {
            log.info("获取旅游计划: planId={}", planId);
            
            TravelPlanRequest plan = planStorage.get(planId);
            
            if (plan == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("code", 404);
                errorResponse.put("message", "旅游计划不存在");
                return ResponseEntity.status(404).body(errorResponse);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "获取成功");
            response.put("data", plan);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("获取旅游计划异常: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "获取失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}

