package com.tourism.dto;

import lombok.Data;

import java.util.List;

/**
 * 高德地图POI搜索响应DTO
 * 用于接收高德地图POI搜索API的响应数据
 * 
 * @author tourism
 * @since 2025-01-27
 */
@Data
public class AmapPoiSearchResponse {
    
    /**
     * 响应状态码
     */
    private String status;
    
    /**
     * 响应信息
     */
    private String info;
    
    /**
     * 响应码
     */
    private String infocode;
    
    /**
     * 建议城市
     */
    private String count;
    
    /**
     * POI搜索结果
     */
    private Suggestion suggestion;
    
    /**
     * POI列表
     */
    private List<Poi> pois;
    
    @Data
    public static class Suggestion {
        /**
         * 建议城市列表
         */
        private List<String> cities;
        
        /**
         * 建议关键词列表
         */
        private List<String> keywords;
    }
    
    @Data
    public static class Poi {
        /**
         * POI ID
         */
        private String id;
        
        /**
         * POI名称
         */
        private String name;
        
        /**
         * POI类型
         */
        private String type;
        
        /**
         * 类型编码
         */
        private String typecode;
        
        /**
         * 地址
         */
        private String address;
        
        /**
         * 位置
         */
        private String location;
        
        /**
         * 距离
         */
        private String distance;
        
        /**
         * 电话
         */
        private String tel;
        
        /**
         * 区域编码
         */
        private String adcode;
        
        /**
         * 城市编码
         */
        private String citycode;
        
        /**
         * 省份
         */
        private String pname;
        
        /**
         * 城市
         */
        private String cityname;
        
        /**
         * 区域
         */
        private String adname;
        
        /**
         * 重要性
         */
        private String importance;
        
        /**
         * 是否有美食
         */
        private String shopinfo;
        
        /**
         * 是否有优惠
         */
        private String discount_num;
        
        /**
         * 营业时间
         */
        private String business_area;
        
        /**
         * 经纬度
         */
        private String[] locationArray;
        
        /**
         * 获取经度
         */
        public Double getLongitude() {
            if (location != null && location.contains(",")) {
                String[] coords = location.split(",");
                if (coords.length >= 2) {
                    try {
                        return Double.parseDouble(coords[0]);
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }
            }
            return null;
        }
        
        /**
         * 获取纬度
         */
        public Double getLatitude() {
            if (location != null && location.contains(",")) {
                String[] coords = location.split(",");
                if (coords.length >= 2) {
                    try {
                        return Double.parseDouble(coords[1]);
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }
            }
            return null;
        }
    }
}

