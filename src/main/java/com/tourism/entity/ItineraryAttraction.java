package com.tourism.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;

/**
 * 行程景点关联实体类
 * 对应数据库表: itinerary_attraction
 * 用于存储行程中的景点顺序和详细信息
 * 
 * @author tourism
 * @since 2025-01-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("itinerary_attraction")
public class ItineraryAttraction {
    
    /**
     * 关联ID - 主键，自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 行程ID - 外键关联travel_itinerary表
     */
    @TableField("itinerary_id")
    @NotNull(message = "行程ID不能为空")
    private Long itineraryId;
    
    /**
     * 景点ID - 外键关联attraction表
     */
    @TableField("attraction_id")
    @NotNull(message = "景点ID不能为空")
    private Long attractionId;
    
    /**
     * 景点顺序 - 在行程中的游览顺序
     */
    @TableField("sequence_order")
    @NotNull(message = "景点顺序不能为空")
    @Min(value = 1, message = "景点顺序必须大于0")
    private Integer sequenceOrder;
    
    /**
     * 实际游览时长 - 可能不同于建议时长
     */
    @TableField("visit_duration")
    private String visitDuration;
    
    /**
     * 游览备注 - 用户添加的备注信息
     */
    @TableField("visit_notes")
    private String visitNotes;
    
    /**
     * 从上一个景点的交通方式
     */
    @TableField("transportation_from_previous")
    private String transportationFromPrevious;
    
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
     * 关联的景点信息 - 非数据库字段，用于关联查询
     */
    @TableField(exist = false)
    private Attraction attraction;
    
    // 构造函数
    public ItineraryAttraction() {}
    
    public ItineraryAttraction(Long itineraryId, Long attractionId, Integer sequenceOrder) {
        this.itineraryId = itineraryId;
        this.attractionId = attractionId;
        this.sequenceOrder = sequenceOrder;
    }
    
    
    @Override
    public String toString() {
        return "ItineraryAttraction{" +
                "id=" + id +
                ", itineraryId=" + itineraryId +
                ", attractionId=" + attractionId +
                ", sequenceOrder=" + sequenceOrder +
                ", visitDuration='" + visitDuration + '\'' +
                ", visitNotes='" + visitNotes + '\'' +
                ", transportationFromPrevious='" + transportationFromPrevious + '\'' +
                ", createdTime=" + createdTime +
                ", updatedTime=" + updatedTime +
                '}';
    }
}
