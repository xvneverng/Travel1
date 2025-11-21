package com.tourism.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 路线步骤实体类（Entity层）
 * 
 * 作用：
 * - 表示路线中单个步骤的详细信息
 * - 封装导航指令和道路信息
 * - 用于详细的路线导航和指引
 * 
 * 属性说明：
 * - instruction: 导航指令（如"直行500米"、"右转进入中山路"）
 * - distance: 步骤距离（米）
 * - duration: 步骤耗时（秒）
 * - polyline: 步骤坐标点
 * - road: 道路名称
 * - direction: 行驶方向
 * 
 * 使用场景：
 * - 详细导航指引
 * - 路线步骤展示
 * - 地图路径绘制
 * - 语音导航播报
 * 
 * 数据来源：
 * - 高德地图API路径规划详细步骤
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
public class RouteStep {
    
    /**
     * 步骤说明
     */
    private String instruction;
    
    /**
     * 步骤距离（米）
     */
    private Integer distance;
    
    /**
     * 步骤耗时（秒）
     */
    private Integer duration;
    
    /**
     * 步骤坐标点
     */
    private String polyline;
    
    /**
     * 道路名称
     */
    private String road;
    
    /**
     * 方向
     */
    private String direction;
}
