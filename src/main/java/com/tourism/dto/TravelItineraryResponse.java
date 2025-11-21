package com.tourism.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 旅游行程响应DTO
 * 用于返回给前端的行程数据
 * 
 * @author tourism
 * @since 2025-01-27
 */


 @Data
public class TravelItineraryResponse {
    
    /**
     * 行程ID
     */
    private Long id;
    
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
     * 照片URL列表
     */
    private List<String> photoList;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;
    
    /**
     * 景点列表
     */
    private List<AttractionResponse> attractions;
    
    // 构造函数
    public TravelItineraryResponse() {}
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
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
    
    public LocalDateTime getCreatedTime() {
        return createdTime;
    }
    
    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }
    
    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }
    
    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }
    
    public List<AttractionResponse> getAttractions() {
        return attractions;
    }
    
    public void setAttractions(List<AttractionResponse> attractions) {
        this.attractions = attractions;
    }
    
    public List<String> getPhotoList() {
        return photoList;
    }
    
    public void setPhotoList(List<String> photoList) {
        this.photoList = photoList;
    }
    
    @Override
    public String toString() {
        return "TravelItineraryResponse{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", travelDate=" + travelDate +
                ", dayNumber=" + dayNumber +
                ", weatherCondition='" + weatherCondition + '\'' +
                ", weatherIcon='" + weatherIcon + '\'' +
                ", temperatureMin=" + temperatureMin +
                ", temperatureMax=" + temperatureMax +
                ", notes='" + notes + '\'' +
                ", travelFeelings='" + travelFeelings + '\'' +
                ", photoList=" + photoList +
                ", createdTime=" + createdTime +
                ", updatedTime=" + updatedTime +
                ", attractions=" + attractions +
                '}';
    }
    
    /**
     * 景点响应DTO - 内部类
     */
    @Data
    public static class AttractionResponse {
        
        /**
         * 景点ID
         */
        private Long id;
        
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
        public AttractionResponse() {}
 
        @Override
        public String toString() {
            return "AttractionResponse{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
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
