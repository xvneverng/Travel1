package com.tourism.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * 天气建议实体类
 * 对应数据库表: weather_suggestion
 * 
 * @author tourism
 * @since 2025-01-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("weather_suggestion")
public class WeatherSuggestion {
    
    /**
     * 建议ID - 主键，自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 建议类型
     */
    @TableField("suggestion_type")
    @NotBlank(message = "建议类型不能为空")
    private String suggestionType;
    
    /**
     * 建议内容（包含图标、标题和描述）
     */
    @TableField("content")
    @NotBlank(message = "建议内容不能为空")
    private String content;
    
    /**
     * 适用天气条件
     */
    @TableField("weather_condition")
    private String weatherCondition;
    
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
     * 优先级 - low:低, medium:中, high:高
     */
    @TableField("priority")
    private String priority;
    
    /**
     * 是否启用
     */
    @TableField("is_active")
    private Boolean isActive;
    
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
    
    // 构造函数
    public WeatherSuggestion() {}
    
    public WeatherSuggestion(String suggestionType, String content) {
        this.suggestionType = suggestionType;
        this.content = content;
        this.priority = "medium";
        this.isActive = true;
    }
    
    @Override
    public String toString() {
        return "WeatherSuggestion{" +
                "id=" + id +
                ", suggestionType='" + suggestionType + '\'' +
                ", content='" + content + '\'' +
                ", weatherCondition='" + weatherCondition + '\'' +
                ", temperatureMin=" + temperatureMin +
                ", temperatureMax=" + temperatureMax +
                ", priority='" + priority + '\'' +
                ", isActive=" + isActive +
                ", createdTime=" + createdTime +
                ", updatedTime=" + updatedTime +
                '}';
    }
}
