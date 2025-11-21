package com.tourism.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 地点实体类（Entity层）
 * 
 * 作用：
 * - 表示地理位置信息的数据模型
 * - 封装地点的基本属性和坐标信息
 * - 用于地理编码和路径规划的数据传输
 * 
 * 属性说明：
 * - name: 地点名称（如"南京站"、"北京天安门"）
 * - longitude: 经度坐标
 * - latitude: 纬度坐标
 * - address: 详细地址信息
 * - city: 所属城市
 * 
 * 使用场景：
 * - 地理编码结果存储
 * - 路径规划的起点、终点、途经点
 * - 地图显示和标记
 * - 路线统计和分析
 * 
 * 注解说明：
 * @Data - Lombok注解，自动生成getter/setter/toString等方法
 * @NoArgsConstructor - 生成无参构造函数
 * @AllArgsConstructor - 生成全参构造函数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    
    /**
     * 地点名称
     */
    private String name;
    
    /**
     * 经度
     */
    private Double longitude;
    
    /**
     * 纬度
     */
    private Double latitude;
    
    /**
     * 地址
     */
    private String address;
    
    /**
     * 城市
     */
    private String city;
}
