package com.tourism.service;

import com.tourism.dto.MapResponse;
import com.tourism.dto.RouteRequest;
import com.tourism.entity.Attraction;
import com.tourism.entity.Location;
import com.tourism.mapper.AttractionMapper;
import com.tourism.util.AmapApiUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 地图生成服务类（Service层）
 * 
 * 作用：
 * - 处理业务逻辑和业务规则
 * - 协调Controller层和Util层
 * - 参数验证和业务验证
 * 
 * 职责：
 * - 调用高德地图API进行地理编码
 * - 业务逻辑处理和数据转换
 * - 异常处理和错误响应构建
 * 
 * 核心方法：
 * - generateMap() - 主要业务方法，生成地图数据
 * - validateRequest() - 参数验证
 * 
 * 注解说明：
 * @Service - 标识为Spring服务组件
 * @Slf4j - Lombok注解，自动生成日志对象
 */
@Slf4j
@Service
public class RoutePlanningService {
    
    @Autowired
    private AmapApiUtil amapApiUtil;
    
    @Autowired
    private AttractionMapper attractionMapper;
    
    
    /**
     * 验证请求参数
     * @param request 请求对象
     */
    private void validateRequest(RouteRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("请求参数不能为空");
        }
        
        if (request.getLocations() == null || request.getLocations().isEmpty()) {
            throw new IllegalArgumentException("地点列表不能为空");
        }
        
        if (request.getLocations().size() < 2) {
            throw new IllegalArgumentException("地点数量不能少于2个");
        }
        
        if (request.getLocations().size() > 10) {
            throw new IllegalArgumentException("地点数量不能超过10个");
        }
        
        // 验证地点名称不能为空
        for (String location : request.getLocations()) {
            if (location == null || location.trim().isEmpty()) {
                throw new IllegalArgumentException("地点名称不能为空");
            }
        }
        
        // 验证路线策略
        if (request.getStrategy() != null && 
            !isValidStrategy(request.getStrategy())) {
            throw new IllegalArgumentException("无效的路线策略: " + request.getStrategy());
        }
    }
    
    /**
     * 验证路线策略是否有效
     * @param strategy 策略名称
     * @return 是否有效
     */
    private boolean isValidStrategy(String strategy) {
        return "driving".equals(strategy) || 
               "walking".equals(strategy) || 
               "transit".equals(strategy) ||
               // marker: 仅生成标记点/简单路线URL，用于轻量级展示
               "marker".equals(strategy);
    }
    
    
    /**
     * 生成地图数据（优化版本 - 支持经纬度缓存）
     * @param request 路线规划请求
     * @return 地图响应数据
     */
    public MapResponse generateMap(RouteRequest request) {
        try {
            log.info("开始生成地图: {}", request.getLocations());
            
            // 1. 参数验证
            validateRequest(request);
            
            // 2. 优先从数据库获取经纬度，缺失的通过API获取
            List<Location> locations = getLocationsWithCache(request.getLocations());
            log.info("地理编码完成，获得 {} 个有效坐标", locations.size());
            
            // 3. 使用高德地图API生成地图数据
            MapResponse mapResponse = amapApiUtil.generateMap(locations, request.getStrategy());
            log.info("地图生成完成");
            
            return mapResponse;
            
        } catch (Exception e) {
            log.error("生成地图异常: {}", e.getMessage(), e);
            return MapResponse.error(500, "生成地图失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取地点坐标（优先从数据库缓存获取，缺失的通过API获取并保存）
     * @param locationNames 地点名称列表
     * @return 坐标列表
     */
    private List<Location> getLocationsWithCache(List<String> locationNames) {
        List<Location> locations = new ArrayList<>();
        List<String> missingLocations = new ArrayList<>();
        
        log.info("开始检查数据库中的经纬度缓存，地点数量: {}", locationNames.size());
        
        // 1. 优先从数据库获取已缓存的经纬度
        for (String locationName : locationNames) {
            Attraction attraction = attractionMapper.findByName(locationName);
            if (attraction != null && attraction.getLatitude() != null && attraction.getLongitude() != null) {
                // 从数据库获取到经纬度
                Location location = new Location();
                location.setName(locationName);
                location.setLatitude(attraction.getLatitude().doubleValue());
                location.setLongitude(attraction.getLongitude().doubleValue());
                location.setAddress(attraction.getAddress());
                location.setCity(attraction.getAddress()); // 使用地址作为城市信息
                locations.add(location);
                log.info("从数据库缓存获取坐标: {} -> ({}, {})", 
                        locationName, attraction.getLongitude(), attraction.getLatitude());
            } else {
                // 数据库中无经纬度，需要调用API获取
                missingLocations.add(locationName);
                log.info("数据库中无坐标缓存，需要API获取: {}", locationName);
            }
        }
        
        // 2. 对缺失的地点调用API获取经纬度
        if (!missingLocations.isEmpty()) {
            log.info("开始调用API获取 {} 个地点的经纬度", missingLocations.size());
            List<Location> apiLocations = amapApiUtil.batchGeocode(missingLocations);
            
            // 3. 将API获取的经纬度保存到数据库
            for (Location apiLocation : apiLocations) {
                locations.add(apiLocation);
                saveLocationToDatabase(apiLocation);
            }
        }
        
        log.info("坐标获取完成: 总数={}, 缓存命中={}, API调用={}", 
                locations.size(), locations.size() - missingLocations.size(), missingLocations.size());
        
        return locations;
    }
    
    /**
     * 将地点坐标保存到数据库
     * @param location 地点坐标信息
     */
    private void saveLocationToDatabase(Location location) {
        try {
            // 检查景点是否已存在
            Attraction existingAttraction = attractionMapper.findByName(location.getName());
            
            if (existingAttraction != null) {
                // 更新现有景点的经纬度
                existingAttraction.setLatitude(BigDecimal.valueOf(location.getLatitude()));
                existingAttraction.setLongitude(BigDecimal.valueOf(location.getLongitude()));
                if (location.getAddress() != null) {
                    existingAttraction.setAddress(location.getAddress());
                }
                attractionMapper.updateById(existingAttraction);
                log.info("更新景点坐标到数据库: {} -> ({}, {})", 
                        location.getName(), location.getLongitude(), location.getLatitude());
            } else {
                // 创建新景点记录
                Attraction newAttraction = new Attraction();
                newAttraction.setName(location.getName());
                newAttraction.setLatitude(BigDecimal.valueOf(location.getLatitude()));
                newAttraction.setLongitude(BigDecimal.valueOf(location.getLongitude()));
                newAttraction.setAddress(location.getAddress());
                newAttraction.setDescription("通过地图API自动获取的景点信息");
                newAttraction.setIcon("📍");
                attractionMapper.insert(newAttraction);
                log.info("新增景点坐标到数据库: {} -> ({}, {})", 
                        location.getName(), location.getLongitude(), location.getLatitude());
            }
        } catch (Exception e) {
            log.error("保存景点坐标到数据库失败: {} - {}", location.getName(), e.getMessage(), e);
            // 不抛出异常，避免影响地图生成
        }
    }
    
    
    /**
     * 测试API连接
     * @return 测试结果
     */
    public String testApiConnection() {
        try {
            return amapApiUtil.testApiConnection();
        } catch (Exception e) {
            log.error("API连接测试异常: {}", e.getMessage(), e);
            return "API连接测试异常: " + e.getMessage();
        }
    }
    
    /**
     * 地理编码 - 将地址转换为坐标
     * @param address 地址
     * @return 坐标信息
     */
    public Location geocodeAddress(String address) {
        try {
            log.info("开始地理编码: {}", address);
            Location location = amapApiUtil.geocode(address);
            log.info("地理编码成功: {} -> ({}, {})", address, location.getLongitude(), location.getLatitude());
            return location;
        } catch (Exception e) {
            log.error("地理编码异常: {}", e.getMessage(), e);
            throw new RuntimeException("地理编码失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试地理编码
     * @param address 地址
     * @return 测试结果
     */
    public String testGeocode(String address) {
        try {
            log.info("开始测试地理编码: {}", address);
            Location location = amapApiUtil.geocode(address);
            return "地理编码成功: " + location.getName() + " -> (" + 
                   location.getLongitude() + ", " + location.getLatitude() + ")";
        } catch (Exception e) {
            log.error("地理编码测试异常: {}", e.getMessage(), e);
            return "地理编码测试异常: " + e.getMessage();
        }
    }
    
    /**
     * 批量更新景点经纬度（手动触发缓存更新）
     * @param attractionNames 景点名称列表
     * @return 更新结果
     */
    public String batchUpdateCoordinates(List<String> attractionNames) {
        try {
            log.info("开始批量更新景点经纬度: {}", attractionNames);
            
            int successCount = 0;
            int failCount = 0;
            
            for (String attractionName : attractionNames) {
                try {
                    // 调用API获取经纬度
                    Location location = amapApiUtil.geocode(attractionName);
                    
                    // 保存到数据库
                    saveLocationToDatabase(location);
                    successCount++;
                    
                } catch (Exception e) {
                    log.error("更新景点 {} 经纬度失败: {}", attractionName, e.getMessage());
                    failCount++;
                }
            }
            
            String result = String.format("批量更新完成: 成功=%d, 失败=%d", successCount, failCount);
            log.info(result);
            return result;
            
        } catch (Exception e) {
            log.error("批量更新景点经纬度异常: {}", e.getMessage(), e);
            return "批量更新异常: " + e.getMessage();
        }
    }
    
    /**
     * 获取景点坐标缓存状态
     * @param attractionNames 景点名称列表
     * @return 缓存状态信息
     */
    public String getCacheStatus(List<String> attractionNames) {
        try {
            log.info("检查景点坐标缓存状态: {}", attractionNames);
            
            int cachedCount = 0;
            int missingCount = 0;
            StringBuilder status = new StringBuilder();
            
            for (String attractionName : attractionNames) {
                Attraction attraction = attractionMapper.findByName(attractionName);
                if (attraction != null && attraction.getLatitude() != null && attraction.getLongitude() != null) {
                    cachedCount++;
                    status.append(String.format("✅ %s: (%s, %s)\n", 
                            attractionName, attraction.getLongitude(), attraction.getLatitude()));
                } else {
                    missingCount++;
                    status.append(String.format("❌ %s: 无缓存\n", attractionName));
                }
            }
            
            String result = String.format("缓存状态: 已缓存=%d, 缺失=%d\n%s", 
                    cachedCount, missingCount, status.toString());
            log.info("缓存状态检查完成: 已缓存={}, 缺失={}", cachedCount, missingCount);
            return result;
            
        } catch (Exception e) {
            log.error("检查缓存状态异常: {}", e.getMessage(), e);
            return "检查缓存状态异常: " + e.getMessage();
        }
    }
}
