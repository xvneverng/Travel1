package com.tourism.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tourism.entity.MapCache;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 地图缓存Mapper接口 - 简单版本
 */
@Mapper
public interface MapCacheMapper extends BaseMapper<MapCache> {
    
    /**
     * 根据缓存键查找地图URL
     * @param cacheKey 缓存键
     * @return 地图URL，如果未找到则返回null
     */
    @Select("SELECT map_url FROM map_cache WHERE cache_key = #{cacheKey}")
    String findMapUrlByCacheKey(@Param("cacheKey") String cacheKey);
    
    /**
     * 检查缓存是否存在
     * @param cacheKey 缓存键
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM map_cache WHERE cache_key = #{cacheKey}")
    boolean existsByCacheKey(@Param("cacheKey") String cacheKey);
}
