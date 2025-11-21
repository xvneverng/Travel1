package com.tourism.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * 路书信息实体类
 * 对应数据库表: travel_guide
 * 
 * @author tourism
 * @since 2025-01-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("travel_guide")
public class TravelGuide {
    
    /**
     * 路书ID - 主键，自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    @TableField("user_id")
    @NotBlank(message = "用户ID不能为空")
    private String userId;
    
    /**
     * 路书名称
     */
    @TableField("guide_name")
    @NotBlank(message = "路书名称不能为空")
    private String guideName;
    
    /**
     * 目的地
     */
    @TableField("destination")
    private String destination;
    
    /**
     * 出行开始日期
     */
    @TableField("start_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private java.time.LocalDate startDate;
    
    /**
     * 出行结束日期
     */
    @TableField("end_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private java.time.LocalDate endDate;
    
    /**
     * 行程天数
     */
    @TableField("duration_days")
    private Integer durationDays;
    
    /**
     * 行程时长描述
     */
    @TableField("duration_desc")
    private String durationDesc;
    
    /**
     * 路书描述
     */
    @TableField("description")
    private String description;
    
    /**
     * 我的注意事项
     */
    @TableField("my_notes")
    private String myNotes;
    
    /**
     * 封面图片URL
     */
    @TableField("cover_image")
    private String coverImage;
    
    /**
     * 路书状态 - draft:草稿, published:已发布, archived:已归档
     */
    @TableField("status")
    private String status;
    
    /**
     * 是否公开
     */
    @TableField("is_public")
    private Boolean isPublic;
    
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
    public TravelGuide() {}
    
    public TravelGuide(String userId, String guideName, String destination) {
        this.userId = userId;
        this.guideName = guideName;
        this.destination = destination;
        this.status = "draft";
        this.isPublic = false;
    }
    
    @Override
    public String toString() {
        return "TravelGuide{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", guideName='" + guideName + '\'' +
                ", destination='" + destination + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", durationDays=" + durationDays +
                ", durationDesc='" + durationDesc + '\'' +
                ", description='" + description + '\'' +
                ", myNotes='" + myNotes + '\'' +
                ", coverImage='" + coverImage + '\'' +
                ", status='" + status + '\'' +
                ", isPublic=" + isPublic +
                ", createdTime=" + createdTime +
                ", updatedTime=" + updatedTime +
                '}';
    }
}
