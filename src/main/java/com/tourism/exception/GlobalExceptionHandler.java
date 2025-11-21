package com.tourism.exception;

import com.tourism.dto.RouteResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

/**
 * 全局异常处理器（Exception层）
 * 
 * 作用：
 * - 统一处理应用中的所有异常
 * - 提供统一的错误响应格式
 * - 避免异常信息直接暴露给客户端
 * 
 * 处理异常类型：
 * - MethodArgumentNotValidException: 参数验证异常
 * - BindException: 参数绑定异常
 * - ConstraintViolationException: 约束违反异常
 * - IllegalArgumentException: 非法参数异常
 * - RuntimeException: 运行时异常
 * - Exception: 通用异常
 * 
 * 处理策略：
 * - 记录详细的异常日志
 * - 返回用户友好的错误信息
 * - 保持统一的响应格式
 * - 区分客户端错误和服务器错误
 * 
 * 响应格式：
 * - 使用RouteResponse统一响应格式
 * - 包含错误码和错误消息
 * - 不暴露敏感的系统信息
 * 
 * 使用场景：
 * - 所有Controller层的异常处理
 * - 参数验证失败处理
 * - 业务逻辑异常处理
 * - 系统异常兜底处理
 * 
 * 注解说明：
 * @RestControllerAdvice - 全局异常处理器
 * @ExceptionHandler - 异常处理方法
 * @Slf4j - Lombok注解，自动生成日志对象
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * 处理参数验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RouteResponse> handleValidationException(MethodArgumentNotValidException e) {
        log.warn("参数验证失败: {}", e.getMessage());
        
        StringBuilder errorMsg = new StringBuilder("参数验证失败: ");
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            errorMsg.append(error.getField()).append(" ").append(error.getDefaultMessage()).append("; ");
        }
        
        RouteResponse response = RouteResponse.error(400, errorMsg.toString());
        return ResponseEntity.badRequest().body(response);
    }
    
    /**
     * 处理绑定异常
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<RouteResponse> handleBindException(BindException e) {
        log.warn("参数绑定失败: {}", e.getMessage());
        
        StringBuilder errorMsg = new StringBuilder("参数绑定失败: ");
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            errorMsg.append(error.getField()).append(" ").append(error.getDefaultMessage()).append("; ");
        }
        
        RouteResponse response = RouteResponse.error(400, errorMsg.toString());
        return ResponseEntity.badRequest().body(response);
    }
    
    /**
     * 处理约束违反异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<RouteResponse> handleConstraintViolationException(ConstraintViolationException e) {
        log.warn("约束违反: {}", e.getMessage());
        
        StringBuilder errorMsg = new StringBuilder("约束违反: ");
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations) {
            errorMsg.append(violation.getPropertyPath()).append(" ").append(violation.getMessage()).append("; ");
        }
        
        RouteResponse response = RouteResponse.error(400, errorMsg.toString());
        return ResponseEntity.badRequest().body(response);
    }
    
    /**
     * 处理非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<RouteResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("非法参数: {}", e.getMessage());
        RouteResponse response = RouteResponse.error(400, "参数错误: " + e.getMessage());
        return ResponseEntity.badRequest().body(response);
    }
    
    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<RouteResponse> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常: {}", e.getMessage(), e);
        RouteResponse response = RouteResponse.error(500, "服务异常: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    /**
     * 处理通用异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<RouteResponse> handleException(Exception e) {
        log.error("未知异常: {}", e.getMessage(), e);
        RouteResponse response = RouteResponse.error(500, "系统异常: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
