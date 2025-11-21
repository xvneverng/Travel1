package com.tourism.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Min;
import javax.validation.constraints.Max;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

/**
 * 旅游行程实体类
 * 对应数据库表: travel_itinerary
 * 
 * @author tourism
 * @since 2025-01-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("travel_itinerary")
public class TravelItinerary {
    
    /**
     * 行程ID - 主键，自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID - 标识行程所属用户
     */
    @TableField("user_id")
    @NotBlank(message = "用户ID不能为空")
    private String userId;
    
    /**
     * 旅行日期 - 格式: yyyy-MM-dd
     */
    @TableField("travel_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "旅行日期不能为空")
    private LocalDate travelDate;
    
    /**
     * 第几天 - 行程天数编号
     */
    @TableField("day_number")
    @NotNull(message = "天数不能为空")
    @Min(value = 1, message = "天数必须大于0")
    @Max(value = 365, message = "天数不能超过365")
    private Integer dayNumber;
    
    /**
     * 天气状况 - 如: 晴、阴、雨等
     */
    @TableField("weather_condition")
    private String weatherCondition;
    
    /**
     * 天气图标 - 如: ☀️、☁️、🌧️等
     */
    @TableField("weather_icon")
    private String weatherIcon;
    
    /**
     * 最低温度
     */
    @TableField("temperature_min")
    private Integer temperatureMin;
    
    /**
     * 最高温度
     */
    @TableField("temperature_max")
    private Integer temperatureMax;
    
    /**
     * 今日注意事项
     */
    @TableField("notes")
    private String notes;
    
    /**
     * 旅行感受
     */
    @TableField("travel_feelings")
    private String travelFeelings;
    
    /**
     * 照片URL列表 - JSON格式存储
     */
    @TableField("photo_urls")
    @JsonProperty("photoUrls")
    private String photoUrls;
    
    /**
     * 创建时间
     */
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    
    /**
     * 更新时间
     */
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;
    
    /**
     * 行程中的景点列表 - 非数据库字段，用于关联查询
     */
    @TableField(exist = false)
    private List<ItineraryAttraction> attractions;
    
    /**
     * 照片URL列表 - 非数据库字段，用于前端展示
     */
    @TableField(exist = false)
    @JsonProperty("photoList")
    private List<String> photoList;
    
    // 构造函数
    public TravelItinerary() {}
    
    public TravelItinerary(String userId, LocalDate travelDate, Integer dayNumber) {
        this.userId = userId;
        this.travelDate = travelDate;
        this.dayNumber = dayNumber;
    }
    
    /**
     * 获取照片URL列表
     * 将JSON字符串转换为List<String>
     */
    public List<String> getPhotoList() {
        if (photoList == null && photoUrls != null && !photoUrls.trim().isEmpty()) {
            try {
                // 使用Jackson ObjectMapper进行正确的JSON解析
                com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
                photoList = objectMapper.readValue(photoUrls, 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
            } catch (Exception e) {
                // 如果JSON解析失败，返回空列表
                photoList = new ArrayList<>();
            }
        }
        return photoList != null ? photoList : new ArrayList<>();
    }
    
    /**
     * 设置照片URL列表
     * 将List<String>转换为JSON字符串
     */
    public void setPhotoList(List<String> photoList) {
        this.photoList = photoList;
        try {
            // 使用Jackson ObjectMapper进行正确的JSON序列化
            com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            this.photoUrls = objectMapper.writeValueAsString(photoList);
        } catch (Exception e) {
            // 如果JSON序列化失败，使用空数组
            this.photoUrls = "[]";
        }
    }
    
 
    
    @Override
    public String toString() {
        return "TravelItinerary{" +
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
                ", photoUrls='" + photoUrls + '\'' +
                ", createdTime=" + createdTime +
                ", updatedTime=" + updatedTime +
                '}';
    }
}
