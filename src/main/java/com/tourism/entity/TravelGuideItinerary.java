package com.tourism.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 路书与行程关联实体类
 * 对应数据库表: travel_guide_itinerary
 * 
 * @author tourism
 * @since 2025-01-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("travel_guide_itinerary")
public class TravelGuideItinerary {
    
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
     * 行程ID
     */
    @TableField("itinerary_id")
    @NotNull(message = "行程ID不能为空")
    private Long itineraryId;
    
    /**
     * 创建时间
     */
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    
    // 构造函数
    public TravelGuideItinerary() {}
    
    public TravelGuideItinerary(Long guideId, Long itineraryId) {
        this.guideId = guideId;
        this.itineraryId = itineraryId;
    }
    
    @Override
    public String toString() {
        return "TravelGuideItinerary{" +
                "id=" + id +
                ", guideId=" + guideId +
                ", itineraryId=" + itineraryId +
                ", createdTime=" + createdTime +
                '}';
    }
}
