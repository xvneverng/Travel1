package com.tourism.controller;

import com.tourism.dto.MapResponse;
import com.tourism.dto.RouteRequest;
import com.tourism.service.RoutePlanningService;
import com.tourism.service.MapCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 地图生成控制器（Controller层）
 * 
 * 作用：
 * - 处理HTTP请求和响应
 * - 接收前端请求参数
 * - 调用Service层处理业务逻辑
 * - 返回JSON格式的响应数据
 * 
 * 职责：
 * - 参数验证和转换
 * - 异常处理和错误响应
 * - HTTP状态码管理
 * - 跨域请求处理
 * 
 * 主要接口：
 * - POST /api/route/map - 生成地图数据（JSON请求体）
 * - GET /api/route/map/quick - 快速生成地图（URL参数）
 * - GET /api/route/health - 健康检查
 * 
 * 注解说明：
 * @RestController - 组合注解，包含@Controller和@ResponseBody
 * @RequestMapping("/api/route") - 类级别的URL映射
 * @CrossOrigin - 处理跨域请求
 * @Slf4j - Lombok注解，自动生成日志对象
 */
@Slf4j
@RestController
@RequestMapping("/api/route")
@CrossOrigin(originPatterns = "*")
public class RouteController {
    
    @Autowired
    private RoutePlanningService routePlanningService;
    
    @Autowired
    private MapCacheService mapCacheService;
    
    
    /**
     * 生成地图接口
     * POST /api/route/map
     * 
     * @param request 路线规划请求
     * @return 地图响应数据
     */
    @PostMapping("/map")
    @CrossOrigin(originPatterns = "*")
    public ResponseEntity<MapResponse> generateMap(@Valid @RequestBody RouteRequest request) {
        try {
            log.info("收到地图生成请求: {}", request.getLocations());
            
            // 1. 先检查缓存（如果缓存功能不可用，则跳过）
            try {
                String cachedMapUrl = mapCacheService.getMapUrl(request.getLocations());
                if (cachedMapUrl != null && !cachedMapUrl.isEmpty()) {
                    log.info("从缓存获取地图: {}", cachedMapUrl);
                    MapResponse cachedResponse = MapResponse.success("从缓存获取地图成功", cachedMapUrl);
                    return ResponseEntity.ok(cachedResponse);
                }
            } catch (Exception cacheException) {
                log.warn("缓存读取失败，将生成新地图: {}", cacheException.getMessage());
                // 缓存失败不影响主流程，继续生成新地图
            }
            
            // 2. 缓存中没有或缓存不可用，生成新地图
            log.info("开始生成新地图");
            MapResponse response = routePlanningService.generateMap(request);
            
            if (response.getCode() == 200 && response.getData() != null) {
                // 3. 生成成功后尝试保存到缓存（不影响主流程）
                try {
                    String mapUrl = response.getData().getMapUrl();
                    if (mapUrl != null && !mapUrl.isEmpty()) {
                        boolean saved = mapCacheService.saveMapUrl(request.getLocations(), mapUrl);
                        log.info("地图生成成功: 地图ID={}, 缓存保存={}", 
                                response.getData().getMapId(), saved ? "成功" : "失败");
                    }
                } catch (Exception cacheException) {
                    log.warn("缓存保存失败，但不影响地图生成: {}", cacheException.getMessage());
                    // 缓存保存失败不影响返回结果
                }
                return ResponseEntity.ok(response);
            } else {
                log.warn("地图生成失败: {}", response.getMessage());
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            log.error("地图生成异常: {}", e.getMessage(), e);
            MapResponse errorResponse = MapResponse.error(500, "系统异常: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * 快速生成地图接口
     * GET /api/route/map/quick?locations=地点1,地点2,地点3&strategy=driving
     * 
     * @param locations 地点列表，用逗号分隔
     * @param strategy 路线策略
     * @return 地图响应数据
     */
    @GetMapping("/map/quick")
    @CrossOrigin(originPatterns = "*")
    public ResponseEntity<MapResponse> generateMapQuick(
            @RequestParam String locations,
            @RequestParam(defaultValue = "driving") String strategy) {
        try {
            log.info("收到快速地图生成请求: locations={}, strategy={}", locations, strategy);
            
            // 构建请求对象
            RouteRequest request = new RouteRequest();
            request.setLocations(java.util.Arrays.asList(locations.split(",")));
            request.setStrategy(strategy);
            
            MapResponse response = routePlanningService.generateMap(request);
            
            if (response.getCode() == 200) {
                log.info("快速地图生成成功: 地图ID={}, 地图URL={}", 
                        response.getData().getMapId(), response.getData().getMapUrl());
                return ResponseEntity.ok(response);
            } else {
                log.warn("快速地图生成失败: {}", response.getMessage());
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            log.error("快速地图生成异常: {}", e.getMessage(), e);
            MapResponse errorResponse = MapResponse.error(500, "系统异常: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    
    /**
     * 健康检查接口
     * GET /api/route/health
     * 
     * @return 健康状态
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Route Planning Service is running");
    }
    
    /**
     * 批量更新景点经纬度接口
     * POST /api/route/coordinates/update
     * 
     * @param attractionNames 景点名称列表
     * @return 更新结果
     */
    @PostMapping("/coordinates/update")
    @CrossOrigin(originPatterns = "*")
    public ResponseEntity<String> batchUpdateCoordinates(@RequestBody List<String> attractionNames) {
        try {
            log.info("收到批量更新经纬度请求: {}", attractionNames);
            
            String result = routePlanningService.batchUpdateCoordinates(attractionNames);
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("批量更新经纬度异常: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("批量更新异常: " + e.getMessage());
        }
    }
    
    /**
     * 检查景点坐标缓存状态接口
     * POST /api/route/coordinates/status
     * 
     * @param attractionNames 景点名称列表
     * @return 缓存状态
     */
    @PostMapping("/coordinates/status")
    @CrossOrigin(originPatterns = "*")
    public ResponseEntity<String> getCacheStatus(@RequestBody List<String> attractionNames) {
        try {
            log.info("收到检查缓存状态请求: {}", attractionNames);
            
            String result = routePlanningService.getCacheStatus(attractionNames);
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("检查缓存状态异常: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("检查缓存状态异常: " + e.getMessage());
        }
    }
}
