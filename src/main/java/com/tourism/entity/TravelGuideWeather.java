package com.tourism.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 路书与天气建议关联实体类
 * 对应数据库表: travel_guide_weather
 * 
 * @author tourism
 * @since 2025-01-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("travel_guide_weather")
public class TravelGuideWeather {
    
    /**
     * 关联ID - 主键，自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 路书ID
     */
    @TableField("guide_id")
    @NotNull(message = "路书ID不能为空")
    private Long guideId;
    
    /**
     * 天气建议ID
     */
    @TableField("weather_suggestion_id")
    @NotNull(message = "天气建议ID不能为空")
    private Long weatherSuggestionId;
    
    /**
     * 是否已应用
     */
    @TableField("is_applied")
    private Boolean isApplied;
    
    /**
     * 应用时间
     */
    @TableField("applied_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime appliedTime;
    
    /**
     * 创建时间
     */
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    
    // 构造函数
    public TravelGuideWeather() {}
    
    public TravelGuideWeather(Long guideId, Long weatherSuggestionId) {
        this.guideId = guideId;
        this.weatherSuggestionId = weatherSuggestionId;
        this.isApplied = false;
    }
    
    @Override
    public String toString() {
        return "TravelGuideWeather{" +
                "id=" + id +
                ", guideId=" + guideId +
                ", weatherSuggestionId=" + weatherSuggestionId +
                ", isApplied=" + isApplied +
                ", appliedTime=" + appliedTime +
                ", createdTime=" + createdTime +
                '}';
    }
}
