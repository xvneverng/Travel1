package com.tourism.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 地图缓存实体类 - 简单版本
 */
@Data
@TableName("map_cache")
public class MapCache {
    
    /**
     * 缓存ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 缓存键（基于景点名称生成）
     */
    @TableField("cache_key")
    private String cacheKey;
    
    /**
     * 地图URL
     */
    @TableField("map_url")
    private String mapUrl;
    
    /**
     * 景点名称列表，用逗号分隔
     */
    @TableField("locations")
    private String locations;
    
    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
