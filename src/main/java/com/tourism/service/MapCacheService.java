package com.tourism.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tourism.entity.MapCache;
import com.tourism.mapper.MapCacheMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 地图缓存服务实现类 - 简单版本
 */
@Service
public class MapCacheService extends ServiceImpl<MapCacheMapper, MapCache> {
    
    @Autowired
    private MapCacheMapper mapCacheMapper;
    
    /**
     * 生成缓存键
     * @param locations 景点名称列表
     * @return 缓存键（用逗号连接的景点名称）
     */
    public String generateCacheKey(List<String> locations) {
        return String.join(",", locations);
    }
    
    /**
     * 获取地图URL（优先从缓存获取）
     * @param locations 景点名称列表
     * @return 地图URL，如果缓存中没有则返回null
     */
    public String getMapUrl(List<String> locations) {
        String cacheKey = generateCacheKey(locations);
        return mapCacheMapper.findMapUrlByCacheKey(cacheKey);
    }
    
    /**
     * 检查地图是否已缓存
     * @param locations 景点名称列表
     * @return 是否已缓存
     */
    public boolean isCached(List<String> locations) {
        String cacheKey = generateCacheKey(locations);
        return mapCacheMapper.existsByCacheKey(cacheKey);
    }
    
    /**
     * 保存地图URL到缓存
     * @param locations 景点名称列表
     * @param mapUrl 地图URL
     * @return 是否保存成功
     */
    public boolean saveMapUrl(List<String> locations, String mapUrl) {
        String cacheKey = generateCacheKey(locations);
        String locationsStr = String.join(",", locations);
        
        MapCache mapCache = new MapCache();
        mapCache.setCacheKey(cacheKey);
        mapCache.setMapUrl(mapUrl);
        mapCache.setLocations(locationsStr);
        
        return save(mapCache);
    }
}
