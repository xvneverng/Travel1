package com.tourism.dto;

import com.tourism.entity.Route;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 路线规划响应DTO（Data Transfer Object）
 * 
 * 作用：
 * - 封装API响应数据格式
 * - 提供统一的响应结构
 * - 包含状态码、消息和数据
 * 
 * 属性说明：
 * - code: 响应状态码（200成功，400客户端错误，500服务器错误）
 * - message: 响应消息（成功或错误描述）
 * - data: 路线数据（成功时包含Route对象，失败时为null）
 * 
 * 状态码说明：
 * - 200: 请求成功
 * - 400: 参数错误或业务逻辑错误
 * - 500: 服务器内部错误
 * 
 * 静态方法：
 * - success(Route route): 创建成功响应
 * - error(Integer code, String message): 创建错误响应
 * 
 * 使用场景：
 * - 所有API接口的统一响应格式
 * - 前端错误处理和状态判断
 * - 日志记录和监控
 * 
 * 注解说明：
 * @Data - Lombok注解，自动生成getter/setter等方法
 * @NoArgsConstructor - 生成无参构造函数
 * @AllArgsConstructor - 生成全参构造函数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteResponse {
    
    /**
     * 响应状态码
     */
    private Integer code;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * 路线数据
     */
    private Route data;
    
    /**
     * 成功响应
     */
    public static RouteResponse success(Route route) {
        return new RouteResponse(200, "路线规划成功", route);
    }
    
    /**
     * 失败响应
     */
    public static RouteResponse error(Integer code, String message) {
        return new RouteResponse(code, message, null);
    }
}
