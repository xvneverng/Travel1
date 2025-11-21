package com.tourism.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 路线实体类（Entity层）
 * 
 * 作用：
 * - 表示完整路线信息的数据模型
 * - 封装路径规划的结果数据
 * - 包含起点、终点、途经点和详细步骤
 * 
 * 属性说明：
 * - routeId: 路线唯一标识
 * - origin: 起点位置
 * - destination: 终点位置
 * - waypoints: 途经点列表
 * - distance: 总距离（米）
 * - duration: 总耗时（秒）
 * - steps: 详细路线步骤
 * - polyline: 路线坐标点（用于地图绘制）
 * 
 * 使用场景：
 * - 路径规划API响应
 * - 地图路线显示
 * - 路线分析和优化
 * - 用户路线推荐
 * 
 * 数据来源：
 * - 高德地图API路径规划结果
 * - 模拟数据工具类生成
 * 
 * 注解说明：
 * @Data - Lombok注解，自动生成getter/setter/toString等方法
 * @NoArgsConstructor - 生成无参构造函数
 * @AllArgsConstructor - 生成全参构造函数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Route {
    
    /**
     * 路线ID
     */
    private String routeId;
    
    /**
     * 起点
     */
    private Location origin;
    
    /**
     * 终点
     */
    private Location destination;
    
    /**
     * 途经点列表
     */
    private List<Location> waypoints;
    
    /**
     * 总距离（米）
     */
    private Integer distance;
    
    /**
     * 总耗时（秒）
     */
    private Integer duration;
    
    /**
     * 路线步骤
     */
    private List<RouteStep> steps;
    
    /**
     * 路线坐标点
     */
    private String polyline;
}
