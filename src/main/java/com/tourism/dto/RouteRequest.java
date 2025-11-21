package com.tourism.dto;

import lombok.Data;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 路线规划请求DTO（Data Transfer Object）
 * 
 * 作用：
 * - 封装客户端请求参数
 * - 提供参数验证规则
 * - 标准化API接口输入格式
 * 
 * 属性说明：
 * - locations: 地点列表（必填，2-10个地点）
 * - strategy: 路线策略（可选，默认驾车）
 * - includeSteps: 是否包含详细步骤（可选，默认true）
 * 
 * 验证规则：
 * - locations不能为空
 * - locations数量必须在2-10个之间
 * - 每个地点名称不能为空
 * 
 * 支持的路线策略：
 * - driving: 驾车（默认）
 * - walking: 步行
 * - transit: 公交
 * 
 * 使用场景：
 * - POST /api/route/plan 接口请求体
 * - 参数验证和错误提示
 * - 业务逻辑处理输入
 * 
 * 注解说明：
 * @Data - Lombok注解，自动生成getter/setter等方法
 * @NotEmpty - 验证注解，确保字段不为空
 * @Size - 验证注解，限制集合大小
 */
@Data
public class RouteRequest {
    
    /**
     * 地点名称列表
     */
    @NotEmpty(message = "地点列表不能为空")
    @Size(min = 2, max = 10, message = "地点数量必须在2-10个之间")
    private List<String> locations;
    
    /**
     * 路线类型：driving(驾车), walking(步行), transit(公交)
     */
    private String strategy = "driving";
    
    /**
     * 是否返回详细步骤
     */
    private Boolean includeSteps = true;
}
