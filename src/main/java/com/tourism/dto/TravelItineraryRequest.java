package com.tourism.dto;


import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.List;

/**
 * 旅游行程请求DTO
 * 用于接收前端提交的行程数据
 * 
 * @author tourism
 * @since 2025-01-27
 */

 @Data
public class TravelItineraryRequest {
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 旅行日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate travelDate;
    
    /**
     * 第几天
     */
    private Integer dayNumber;
    
    /**
     * 天气状况
     */
    private String weatherCondition;
    
    /**
     * 天气图标
     */
    private String weatherIcon;
    
    /**
     * 最低温度
     */
    private Integer temperatureMin;
    
    /**
     * 最高温度
     */
    private Integer temperatureMax;
    
    /**
     * 今日注意事项
     */
    private String notes;
    
    /**
     * 旅行感受
     */
    private String travelFeelings;
    
    /**
     * 景点列表
     */
    private List<AttractionRequest> attractions;
    
    // 构造函数
    public TravelItineraryRequest() {}
    
    // Getter和Setter方法
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public LocalDate getTravelDate() {
        return travelDate;
    }
    
    public void setTravelDate(LocalDate travelDate) {
        this.travelDate = travelDate;
    }
    
    public Integer getDayNumber() {
        return dayNumber;
    }
    
    public void setDayNumber(Integer dayNumber) {
        this.dayNumber = dayNumber;
    }
    
    public String getWeatherCondition() {
        return weatherCondition;
    }
    
    public void setWeatherCondition(String weatherCondition) {
        this.weatherCondition = weatherCondition;
    }
    
    public String getWeatherIcon() {
        return weatherIcon;
    }
    
    public void setWeatherIcon(String weatherIcon) {
        this.weatherIcon = weatherIcon;
    }
    
    public Integer getTemperatureMin() {
        return temperatureMin;
    }
    
    public void setTemperatureMin(Integer temperatureMin) {
        this.temperatureMin = temperatureMin;
    }
    
    public Integer getTemperatureMax() {
        return temperatureMax;
    }
    
    public void setTemperatureMax(Integer temperatureMax) {
        this.temperatureMax = temperatureMax;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public String getTravelFeelings() {
        return travelFeelings;
    }
    
    public void setTravelFeelings(String travelFeelings) {
        this.travelFeelings = travelFeelings;
    }
    
    public List<AttractionRequest> getAttractions() {
        return attractions;
    }
    
    public void setAttractions(List<AttractionRequest> attractions) {
        this.attractions = attractions;
    }
    
    @Override
    public String toString() {
        return "TravelItineraryRequest{" +
                "userId='" + userId + '\'' +
                ", travelDate=" + travelDate +
                ", dayNumber=" + dayNumber +
                ", weatherCondition='" + weatherCondition + '\'' +
                ", weatherIcon='" + weatherIcon + '\'' +
                ", temperatureMin=" + temperatureMin +
                ", temperatureMax=" + temperatureMax +
                ", notes='" + notes + '\'' +
                ", travelFeelings='" + travelFeelings + '\'' +
                ", attractions=" + attractions +
                '}';
    }
    
    /**
     * 景点请求DTO - 内部类
     */
    @Data
    public static class AttractionRequest {
        
        /**
         * 景点名称
         */
        private String name;
        
        /**
         * 景点描述
         */
        private String description;
        
        /**
         * 景点图标
         */
        private String icon;
        
        /**
         * 建议游览时长
         */
        private String duration;
        
        /**
         * 景点类别
         */
        private String category;
        
        /**
         * 景点标签
         */
        private String tags;
        
        /**
         * 游览提示
         */
        private String tips;
        
        /**
         * 交通信息
         */
        private String transportationInfo;
        
        /**
         * 详细地址
         */
        private String address;
        
        /**
         * 景点顺序
         */
        private Integer sequenceOrder;
        
        /**
         * 实际游览时长
         */
        private String visitDuration;
        
        /**
         * 游览备注
         */
        private String visitNotes;
        
        /**
         * 从上一个景点的交通方式
         */
        private String transportationFromPrevious;
        
        // 构造函数
        public AttractionRequest() {}
        

        
        @Override
        public String toString() {
            return "AttractionRequest{" +
                    "name='" + name + '\'' +
                    ", description='" + description + '\'' +
                    ", icon='" + icon + '\'' +
                    ", duration='" + duration + '\'' +
                    ", category='" + category + '\'' +
                    ", tags='" + tags + '\'' +
                    ", tips='" + tips + '\'' +
                    ", transportationInfo='" + transportationInfo + '\'' +
                    ", address='" + address + '\'' +
                    ", sequenceOrder=" + sequenceOrder +
                    ", visitDuration='" + visitDuration + '\'' +
                    ", visitNotes='" + visitNotes + '\'' +
                    ", transportationFromPrevious='" + transportationFromPrevious + '\'' +
                    '}';
        }
    }
}
