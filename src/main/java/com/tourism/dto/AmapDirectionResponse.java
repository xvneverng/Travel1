package com.tourism.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 高德地图路径规划响应DTO - v5 API
 */
@Data
public class AmapDirectionResponse {
    
    private String status;
    private String info;
    private String infocode;
    private String count;
    
    @JsonProperty("route")
    private Route route;
    
    @Data
    public static class Route {
        private String origin;
        private String destination;
        private String taxi_cost;
        private List<Path> paths;
        
        @Data
        public static class Path {
            private String distance;
            private String duration;
            private String strategy;
            private String tolls;
            private String toll_distance;
            private String restriction;
            private String traffic_lights;
            private List<Step> steps; // 根据实际API响应，steps是数组
            private String polyline;
            
            @Data
            public static class Step {
                private String instruction;
                private String orientation;
                private String step_distance;
                private String road_name; // 添加road_name字段
            }
        }
    }
}
