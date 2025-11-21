package com.tourism.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * 路书模板实体类
 * 对应数据库表: travel_guide_template
 * 
 * @author tourism
 * @since 2025-01-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("travel_guide_template")
public class TravelGuideTemplate {
    
    /**
     * 模板ID - 主键，自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 模板名称
     */
    @TableField("template_name")
    @NotBlank(message = "模板名称不能为空")
    private String templateName;
    
    /**
     * 适用目的地
     */
    @TableField("destination")
    private String destination;
    
    /**
     * 模板描述
     */
    @TableField("description")
    private String description;
    
    /**
     * 类别事项配置，JSON格式
     */
    @TableField("category_items")
    private String categoryItems;
    
    /**
     * 是否系统模板
     */
    @TableField("is_system")
    private Boolean isSystem;
    
    /**
     * 创建者
     */
    @TableField("created_by")
    private String createdBy;
    
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
    public TravelGuideTemplate() {}
    
    public TravelGuideTemplate(String templateName, String destination, String description) {
        this.templateName = templateName;
        this.destination = destination;
        this.description = description;
        this.isSystem = false;
    }
    
    @Override
    public String toString() {
        return "TravelGuideTemplate{" +
                "id=" + id +
                ", templateName='" + templateName + '\'' +
                ", destination='" + destination + '\'' +
                ", description='" + description + '\'' +
                ", categoryItems='" + categoryItems + '\'' +
                ", isSystem=" + isSystem +
                ", createdBy='" + createdBy + '\'' +
                ", createdTime=" + createdTime +
                ", updatedTime=" + updatedTime +
                '}';
    }
}
