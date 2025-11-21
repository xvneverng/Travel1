package com.tourism.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * 路书事项实体类
 * 对应数据库表: travel_guide_item
 * 
 * @author tourism
 * @since 2025-01-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("travel_guide_item")
public class TravelGuideItem {
    
    /**
     * 事项ID - 主键，自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 路书ID
     */
    @TableField("guide_id")
    @NotBlank(message = "路书ID不能为空")
    private Long guideId;
    
    /**
     * 事项类别
     */
    @TableField("category")
    @NotBlank(message = "事项类别不能为空")
    private String category;
    
    /**
     * 事项名称
     */
    @TableField("item_name")
    @NotBlank(message = "事项名称不能为空")
    private String itemName;
    
    /**
     * 事项描述
     */
    @TableField("description")
    private String description;
    
    /**
     * 完成状态 - pending:待完成, completed:已完成, cancelled:已取消
     */
    @TableField("status")
    private String status;
    
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
    public TravelGuideItem() {}
    
    public TravelGuideItem(Long guideId, String category, String itemName) {
        this.guideId = guideId;
        this.category = category;
        this.itemName = itemName;
        this.status = "pending";
    }
    
    @Override
    public String toString() {
        return "TravelGuideItem{" +
                "id=" + id +
                ", guideId=" + guideId +
                ", category='" + category + '\'' +
                ", itemName='" + itemName + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", createdTime=" + createdTime +
                ", updatedTime=" + updatedTime +
                '}';
    }
}
