package com.tourism.dto;

import lombok.Data;
import java.util.List;

/**
 * 地图响应DTO
 * 用于返回地图相关的JSON数据
 * 
 * @author tourism
 * @version 1.0
 * @since 2025-09-21
 */
@Data
public class MapResponse {
    
    /**
     * 响应状态码
     * 200: 成功
     * 400: 请求参数错误
     * 500: 服务器内部错误
     */
    private Integer code;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * 地图数据
     */
    private MapData data;
    
    /**
     * 地图数据内容
     */
    @Data
    public static class MapData {
        /**
         * 地图ID
         */
        private String mapId;
        
        /**
         * 地图中心点坐标
         */
        private CenterPoint center;
        
        /**
         * 地图缩放级别
         */
        private Integer zoom;
        
        /**
         * 地点标记列表
         */
        private List<MapMarker> markers;
        
        /**
         * 路线信息
         */
        private RouteInfo route;
        
        /**
         * 地图URL（用于前端显示）
         */
        private String mapUrl;
        
        /**
         * 地图图片Base64编码（可选）
         */
        private String mapImage;
    }
    
    /**
     * 地图中心点
     */
    @Data
    public static class CenterPoint {
        /**
         * 经度
         */
        private Double longitude;
        
        /**
         * 纬度
         */
        private Double latitude;
    }
    
    /**
     * 地图标记
     */
    @Data
    public static class MapMarker {
        /**
         * 标记ID
         */
        private String markerId;
        
        /**
         * 标记名称
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
         * 标记类型（起点、终点、途经点）
         */
        private String type;
        
        /**
         * 标记图标URL
         */
        private String iconUrl;
    }
    
    /**
     * 路线信息
     */
    @Data
    public static class RouteInfo {
        /**
         * 路线ID
         */
        private String routeId;
        
        /**
         * 总距离（米）
         */
        private Integer distance;
        
        /**
         * 总耗时（秒）
         */
        private Integer duration;
        
        /**
         * 路线策略
         */
        private String strategy;
        
        /**
         * 路线坐标点列表
         */
        private List<RoutePoint> points;
        
        /**
         * 路线步骤
         */
        private List<RouteStep> steps;
    }
    
    /**
     * 路线坐标点
     */
    @Data
    public static class RoutePoint {
        /**
         * 经度
         */
        private Double longitude;
        
        /**
         * 纬度
         */
        private Double latitude;
    }
    
    /**
     * 路线步骤
     */
    @Data
    public static class RouteStep {
        /**
         * 步骤描述
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
         * 道路名称
         */
        private String road;
        
        /**
         * 方向
         */
        private String direction;
    }
    
    /**
     * 创建成功响应
     */
    public static MapResponse success(MapData data) {
        MapResponse response = new MapResponse();
        response.setCode(200);
        response.setMessage("地图生成成功");
        response.setData(data);
        return response;
    }
    
    /**
     * 创建缓存成功响应
     */
    public static MapResponse success(String message, String mapUrl) {
        MapResponse response = new MapResponse();
        response.setCode(200);
        response.setMessage(message);
        
        MapData data = new MapData();
        data.setMapUrl(mapUrl);
        response.setData(data);
        
        return response;
    }
    
    /**
     * 创建失败响应
     */
    public static MapResponse error(Integer code, String message) {
        MapResponse response = new MapResponse();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }
}
