package com.tourism.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 景点信息实体类
 * 对应数据库表: attraction
 * 
 * @author tourism
 * @since 2025-01-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("attraction")
public class Attraction {
    
    /**
     * 景点ID - 主键，自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 景点名称
     */
    @TableField("name")
    @NotBlank(message = "景点名称不能为空")
    private String name;
    
    /**
     * 景点描述
     */
    @TableField("description")
    private String description;
    
    /**
     * 景点图标 - 如: 🏢、🏛️、🏮等
     */
    @TableField("icon")
    private String icon;
    
    /**
     * 建议游览时长 - 如: 30分钟、2小时等
     */
    @TableField("duration")
    private String duration;
    
    /**
     * 景点类别 - 如: 风景名胜、交通设施、科教文化等
     * 注意：数据库表中暂无此字段，暂时注释
     */
    // @TableField("category")
    // private String category;
    
    /**
     * 景点标签 - JSON格式存储，如: ["风景名胜", "国家级景点"]
     */
    @TableField("tags")
    private String tags;
    
    /**
     * 游览提示
     */
    @TableField("tips")
    private String tips;
    
    /**
     * 交通信息
     */
    @TableField("transportation_info")
    private String transportationInfo;
    
    /**
     * 纬度
     */
    @TableField("latitude")
    private BigDecimal latitude;
    
    /**
     * 经度
     */
    @TableField("longitude")
    private BigDecimal longitude;
    
    /**
     * 详细地址
     */
    @TableField("address")
    private String address;
    
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
    public Attraction() {}
    
    public Attraction(String name, String description, String icon) {
        this.name = name;
        this.description = description;
        this.icon = icon;
    }
    
    
    @Override
    public String toString() {
        return "Attraction{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", icon='" + icon + '\'' +
                ", duration='" + duration + '\'' +
                ", tags='" + tags + '\'' +
                ", tips='" + tips + '\'' +
                ", transportationInfo='" + transportationInfo + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", address='" + address + '\'' +
                ", createdTime=" + createdTime +
                ", updatedTime=" + updatedTime +
                '}';
    }
}
