package com.tourism.dto;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.List;

/**
 * 自定义路线请求DTO
 * 用于接收前端提交的自定义路线数据
 * 
 * @author tourism
 * @since 2025-01-27
 */
@Data
public class CustomRouteRequest {
    
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
     * 路线地点列表
     */
    private List<String> locations;
    
    /**
     * 路线策略
     */
    private String strategy;
    
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
    
    // 构造函数
    public CustomRouteRequest() {}
    
    @Override
    public String toString() {
        return "CustomRouteRequest{" +
                "userId='" + userId + '\'' +
                ", travelDate=" + travelDate +
                ", dayNumber=" + dayNumber +
                ", locations=" + locations +
                ", strategy='" + strategy + '\'' +
                ", weatherCondition='" + weatherCondition + '\'' +
                ", weatherIcon='" + weatherIcon + '\'' +
                ", temperatureMin=" + temperatureMin +
                ", temperatureMax=" + temperatureMax +
                ", notes='" + notes + '\'' +
                ", travelFeelings='" + travelFeelings + '\'' +
                '}';
    }
}
