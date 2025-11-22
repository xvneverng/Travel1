package com.tourism.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.tourism.config.AmapConfig;
import com.tourism.dto.AmapDirectionResponse;
import com.tourism.dto.AmapGeocodeResponse;
import com.tourism.dto.MapResponse;
import com.tourism.entity.Location;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 高德地图API工具类（Util层）
 * 
 * 作用：
 * - 封装高德地图API调用逻辑
 * - 处理HTTP请求和响应
 * - 数据格式转换和解析
 * - 异常处理和错误重试
 * 
 * 职责：
 * - 地理编码：将地址转换为坐标
 * - 路径规划：计算多个地点间的最优路线
 * - 批量处理：支持多个地址同时处理
 * - 数据转换：将API响应转换为业务对象
 * 
 * 核心方法：
 * - geocode() - 单个地址地理编码
 * - batchGeocode() - 批量地址地理编码
 * - planRoute() - 路径规划
 * 
 * 技术特点：
 * - 使用WebClient进行异步HTTP调用
 * - 使用Jackson进行JSON解析
 * - 完整的异常处理和日志记录
 * - 支持多种路线策略（驾车、步行、公交）
 * 
 * 依赖：
 * - AmapConfig - 高德地图API配置
 * - WebClient - HTTP客户端
 * - ObjectMapper - JSON处理
 * 
 * 注解说明：
 * @Component - 标识为Spring组件
 * @Slf4j - Lombok注解，自动生成日志对象
 */
@Slf4j
@Component
public class AmapApiUtil {
    
    @Autowired
    private AmapConfig amapConfig;
    
    @Autowired
    private WebClient.Builder webClientBuilder;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 地理编码 - 将地址转换为坐标
     * @param address 地址
     * @return 坐标信息
     */
    public Location geocode(String address) {
        try {
            log.info("开始地理编码查询: {}", address);
            
            String url = amapConfig.getBaseUrl() + amapConfig.getGeocodeUrl() + 
                        "?key=" + amapConfig.getKey() + 
                        "&address=" + address + 
                        "&output=json";
            
            WebClient webClient = webClientBuilder.build();
            log.debug("请求URL: {}", url);
            
            Mono<String> response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class);
            
            String responseBody = response.block();
            log.debug("地理编码响应: {}", responseBody);
            //反序列化
            AmapGeocodeResponse geocodeResponse = objectMapper.readValue(responseBody, AmapGeocodeResponse.class);
            
            if ("1".equals(geocodeResponse.getStatus()) && 
                geocodeResponse.getGeocodes() != null && 
                !geocodeResponse.getGeocodes().isEmpty()) {
                
                AmapGeocodeResponse.Geocode geocode = geocodeResponse.getGeocodes().get(0);
                String[] location = geocode.getLocation().split(",");
                
                Location locationEntity = new Location();
                locationEntity.setName(address);
                locationEntity.setLongitude(Double.parseDouble(location[0]));
                locationEntity.setLatitude(Double.parseDouble(location[1]));
                locationEntity.setAddress(geocode.getFormatted_address());
                locationEntity.setCity(geocode.getCity());
                
                log.info("地理编码成功: {} -> ({}, {})", address, locationEntity.getLongitude(), locationEntity.getLatitude());
                return locationEntity;
            } else {
                log.error("地理编码失败: {} - {}", geocodeResponse.getInfo(), geocodeResponse.getInfocode());
                throw new RuntimeException("地理编码失败: " + geocodeResponse.getInfo());
            }
            
        } catch (Exception e) {
            log.error("地理编码异常: {} - 地址: {}", e.getMessage(), address, e);
            throw new RuntimeException("地理编码异常: " + e.getMessage());
        }
    }
    
    /**
     * 根据关键词和城市检索POI，并返回第一张照片URL（如有）
     * @param keyword 关键词，如“中山陵”
     * @param city 城市名，如“南京”
     * @return 照片URL，若无则返回null
     */
    public String getPoiFirstPhotoUrl(String keyword, String city) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                throw new IllegalArgumentException("keyword不能为空");
            }
            StringBuilder url = new StringBuilder();
            url.append(amapConfig.getBaseUrl())
               .append(amapConfig.getPlaceSearchUrl())
               .append("?key=").append(amapConfig.getKey())
               .append("&keywords=").append(java.net.URLEncoder.encode(keyword, "UTF-8"))
               .append("&extensions=all")
               .append("&output=json");
            if (city != null && !city.trim().isEmpty()) {
                // 高德地图API城市参数格式：使用城市代码或城市名称
                // 南京的城市代码是025，也可以使用"南京"
                String cityParam = city.contains("南京") ? "025" : city;
                url.append("&city=").append(java.net.URLEncoder.encode(cityParam, "UTF-8"));
            }
            
            log.info("POI搜索请求 - 关键词: {}, 城市: {}", keyword, city);
            log.info("POI搜索URL: {}", url.toString());

            WebClient webClient = webClientBuilder.build();
            Mono<String> response = webClient.get()
                    .uri(url.toString())
                    .retrieve()
                    .bodyToMono(String.class);
            String body = response.block();
            log.info("POI搜索响应: {}", body);
            
            if (body == null || body.isEmpty()) {
                log.warn("POI搜索响应为空");
                return null;
            }
            JsonNode root = objectMapper.readTree(body);
            if (!"1".equals(root.path("status").asText())) {
                log.warn("POI搜索失败 - status: {}, info: {}", root.path("status").asText(), root.path("info").asText());
                return null;
            }
            JsonNode pois = root.path("pois");
            log.info("找到POI数量: {}", pois.isArray() ? pois.size() : 0);
            
            if (pois.isArray() && pois.size() > 0) {
                for (JsonNode poi : pois) {
                    String poiName = poi.path("name").asText("");
                    String poiCity = poi.path("cityname").asText("");
                    String poiAdname = poi.path("adname").asText(""); // 区县名称
                    
                    log.info("检查POI: {} (城市: {}, 区县: {})", poiName, poiCity, poiAdname);
                    
                    // 确保POI在指定城市范围内
                    if (city != null && !city.trim().isEmpty()) {
                        String targetCity = city.replace("市", "").replace("025", "南京");
                        if (!poiCity.contains(targetCity) && !poiAdname.contains(targetCity)) {
                            log.info("跳过非目标城市的POI: {} (城市: {})", poiName, poiCity);
                            continue;
                        }
                    }
                    
                    JsonNode photos = poi.path("photos");
                    if (photos.isArray() && photos.size() > 0) {
                        String urlStr = photos.get(0).path("url").asText(null);
                        if (urlStr != null && !urlStr.isEmpty()) {
                            log.info("找到符合条件的POI照片: {} (POI: {}, 城市: {})", urlStr, poiName, poiCity);
                            return urlStr;
                        }
                    }
                }
            }
            log.warn("未找到任何POI照片");
            return null;
        } catch (Exception e) {
            log.error("POI照片检索异常: {} [keyword={}, city={}]", e.getMessage(), keyword, city, e);
            return null;
        }
    }

    
    /**
     * 批量地理编码
     * @param addresses 地址列表
     * @return 坐标列表
     */
    public List<Location> batchGeocode(List<String> addresses) {
        log.info("开始批量地理编码: {} 个地址", addresses.size());
        
        List<Location> locations = addresses.stream()
                .map(this::geocode)
                .collect(Collectors.toList());
        
        log.info("批量地理编码完成: {} 个地址", locations.size());
        return locations;
    }
    
    /**
     * 生成地图数据
     * @param locations 地点列表
     * @param strategy 路线策略
     * @return 地图响应数据
     */
    public MapResponse generateMap(List<Location> locations, String strategy) {
        try {
            log.info("开始生成地图数据: {} 个地点, strategy={}", locations.size(), strategy);
            
            // 1. 计算地图中心点
            MapResponse.CenterPoint center = calculateMapCenter(locations);
            
            // 2. 创建地图标记
            List<MapResponse.MapMarker> markers = createMapMarkers(locations);
            
            // 3. 进行路径规划（轻量级 marker 策略直接跳过路线规划）
            MapResponse.RouteInfo routeInfo = null;
            log.info("开始路径规划检查: 地点数量={}, 策略={}", locations.size(), strategy);
            if (!"marker".equals(strategy) && locations.size() >= 2) {
                log.info("满足路径规划条件，开始调用planRoute方法");
                routeInfo = planRoute(locations, strategy);
                if (routeInfo != null) {
                    log.info("路径规划成功: 距离={}米, 耗时={}秒, 路径点数={}", 
                            routeInfo.getDistance(), routeInfo.getDuration(), 
                            routeInfo.getPoints() != null ? routeInfo.getPoints().size() : 0);
                } else {
                    log.warn("路径规划失败，将只显示标记点");
                }
            } else if ("marker".equals(strategy)) {
                log.info("检测到 marker 策略，跳过路径规划，仅生成标记点地图URL");
            } else {
                log.warn("地点数量不足，跳过路径规划: {}", locations.size());
            }
            
            // 4. 生成地图URL
            String mapUrl = generateMapUrl(locations, center, routeInfo);
            
            // 5. 构建地图数据
            MapResponse.MapData mapData = new MapResponse.MapData();
            mapData.setMapId(UUID.randomUUID().toString());
            mapData.setCenter(center);
            mapData.setZoom(calculateOptimalZoom(locations));
            mapData.setMarkers(markers);
            mapData.setRoute(routeInfo);
            mapData.setMapUrl(mapUrl);
            
            log.info("设置routeInfo到mapData: routeInfo={}", routeInfo != null ? "非空" : "空");
            
            log.info("地图数据生成完成: 中心点({}, {}), 标记数: {}, 路径: {}", 
                    center.getLongitude(), center.getLatitude(), markers.size(), 
                    routeInfo != null ? "已规划" : "无路径");
            
            return MapResponse.success(mapData);
            
        } catch (Exception e) {
            log.error("生成地图数据异常: {}", e.getMessage(), e);
            return MapResponse.error(500, "生成地图数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 路径规划
     * @param locations 地点列表
     * @param strategy 路线策略
     * @return 路线信息
     */
    private MapResponse.RouteInfo planRoute(List<Location> locations, String strategy) {
        try {
            log.info("开始路径规划: {} 个地点, 策略: {}", locations.size(), strategy);
            
            // 如果只有两个地点，使用简单的路径规划
            if (locations.size() == 2) {
                return planSimpleRoute(locations, strategy);
            }
            
            // 多个地点，使用途经点规划
            return planMultiPointRoute(locations, strategy);
            
        } catch (Exception e) {
            log.error("路径规划异常: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 简单路径规划（两个地点）
     */
    private MapResponse.RouteInfo planSimpleRoute(List<Location> locations, String strategy) {
        try {
            // 根据官方文档构建URL - 使用v5 API
            StringBuilder urlBuilder = new StringBuilder("https://restapi.amap.com/v5/direction/driving");
            urlBuilder.append("?key=").append(amapConfig.getKey());
            urlBuilder.append("&origin=").append(locations.get(0).getLongitude()).append(",").append(locations.get(0).getLatitude());
            urlBuilder.append("&destination=").append(locations.get(1).getLongitude()).append(",").append(locations.get(1).getLatitude());
            urlBuilder.append("&output=json");
            
            String url = urlBuilder.toString();
            log.info("简单路径规划URL: {}", url);
            
            return callDirectionApi(url, strategy);
            
        } catch (Exception e) {
            log.error("简单路径规划异常: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 多点路径规划（多个地点）
     */
    private MapResponse.RouteInfo planMultiPointRoute(List<Location> locations, String strategy) {
        try {
            // 根据官方文档构建URL - 使用v5 API
            StringBuilder urlBuilder = new StringBuilder("https://restapi.amap.com/v5/direction/driving");
            urlBuilder.append("?key=").append(amapConfig.getKey());
            urlBuilder.append("&origin=").append(locations.get(0).getLongitude()).append(",").append(locations.get(0).getLatitude());
            urlBuilder.append("&destination=").append(locations.get(locations.size() - 1).getLongitude()).append(",").append(locations.get(locations.size() - 1).getLatitude());
             
            // 添加途经点 - 根据官方文档格式：waypoints=经度1,纬度1;经度2,纬度2
            if (locations.size() > 2) {
                urlBuilder.append("&waypoints=");
                for (int i = 1; i < locations.size() - 1; i++) {
                    if (i > 1) urlBuilder.append(";");
                    urlBuilder.append(locations.get(i).getLongitude()).append(",").append(locations.get(i).getLatitude());
                }
            }
            
            urlBuilder.append("&output=json");
            
            String url = urlBuilder.toString();
            log.info("多点路径规划URL: {}", url);
            
            return callDirectionApi(url, strategy);
            
        } catch (Exception e) {
            log.error("多点路径规划异常: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 调用高德地图路径规划API
     */
    private MapResponse.RouteInfo callDirectionApi(String url, String strategy) {
        try {
            WebClient webClient = webClientBuilder.build();
            Mono<String> response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class);
            
            String responseBody = response.block();
            log.info("路径规划API响应长度da'ddad: {}", responseBody != null ? responseBody.length() : 0);
            log.info("路径规划API响应内容: {}", responseBody);
            
            AmapDirectionResponse directionResponse = objectMapper.readValue(responseBody, AmapDirectionResponse.class);
            
            if ("1".equals(directionResponse.getStatus()) && 
                directionResponse.getRoute() != null && 
                directionResponse.getRoute().getPaths() != null &&
                !directionResponse.getRoute().getPaths().isEmpty()) {
                
                AmapDirectionResponse.Route.Path path = directionResponse.getRoute().getPaths().get(0);
                
                MapResponse.RouteInfo routeInfo = new MapResponse.RouteInfo();
                routeInfo.setRouteId(UUID.randomUUID().toString());
                routeInfo.setDistance(path.getDistance() != null ? Integer.parseInt(path.getDistance()) : 0);
                routeInfo.setDuration(path.getDuration() != null ? Integer.parseInt(path.getDuration()) : 0);
                routeInfo.setStrategy(strategy);
                
                // 转换路径点 - 从polyline解析坐标
                List<MapResponse.RoutePoint> routePoints = new ArrayList<>();
                if (path.getPolyline() != null && !path.getPolyline().isEmpty()) {
                    String[] coords = path.getPolyline().split(";");
                    for (String coord : coords) {
                        String[] latLng = coord.split(",");
                        if (latLng.length >= 2) {
                            MapResponse.RoutePoint routePoint = new MapResponse.RoutePoint();
                            routePoint.setLongitude(Double.parseDouble(latLng[0]));
                            routePoint.setLatitude(Double.parseDouble(latLng[1]));
                            routePoints.add(routePoint);
                        }
                    }
                }
                routeInfo.setPoints(routePoints);
                
                // 转换路径步骤
                List<MapResponse.RouteStep> routeSteps = new ArrayList<>();
                if (path.getSteps() != null) {
                    for (AmapDirectionResponse.Route.Path.Step step : path.getSteps()) {
                        MapResponse.RouteStep routeStep = new MapResponse.RouteStep();
                        routeStep.setInstruction(step.getInstruction());
                        routeStep.setDistance(step.getStep_distance() != null ? Integer.parseInt(step.getStep_distance()) : 0);
                        routeStep.setDuration(0); // v5 API的steps中没有duration字段
                        routeStep.setRoad(step.getRoad_name() != null ? step.getRoad_name() : ""); // 使用road_name字段
                        routeStep.setDirection(step.getOrientation());
                        routeSteps.add(routeStep);
                    }
                }
                routeInfo.setSteps(routeSteps);
                
                log.info("路径规划成功: 距离={}米, 耗时={}秒, 路径点数={}", 
                        path.getDistance(), path.getDuration(), routePoints.size());
                return routeInfo;
                
            } else {
                log.warn("路径规划失败: status={}, info={}, infocode={}", 
                        directionResponse.getStatus(), directionResponse.getInfo(), directionResponse.getInfocode());
                return null;
            }
            
        } catch (Exception e) {
            log.error("调用路径规划API异常: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 计算地图中心点
     */
    private MapResponse.CenterPoint calculateMapCenter(List<Location> locations) {
        if (locations.isEmpty()) {
            return new MapResponse.CenterPoint();
        }
        
        double totalLng = 0.0;
        double totalLat = 0.0;
        
        for (Location location : locations) {
            totalLng += location.getLongitude();
            totalLat += location.getLatitude();
        }
        
        MapResponse.CenterPoint center = new MapResponse.CenterPoint();
        center.setLongitude(totalLng / locations.size());
        center.setLatitude(totalLat / locations.size());
        
        return center;
    }
    
    /**
     * 创建地图标记
     */
    private List<MapResponse.MapMarker> createMapMarkers(List<Location> locations) {
        List<MapResponse.MapMarker> markers = new ArrayList<>();
        
        for (int i = 0; i < locations.size(); i++) {
            Location location = locations.get(i);
            MapResponse.MapMarker marker = new MapResponse.MapMarker();
            
            marker.setMarkerId("marker_" + i);
            marker.setName(location.getName());
            marker.setLongitude(location.getLongitude());
            marker.setLatitude(location.getLatitude());
            marker.setAddress(location.getAddress());
            
            // 设置标记类型
            if (i == 0) {
                marker.setType(location.getName());
                marker.setIconUrl("https://webapi.amap.com/theme/v1.3/markers/n/mark_r.png");
            } else if (i == locations.size() - 1) {
                marker.setType(location.getName());
                marker.setIconUrl("https://webapi.amap.com/theme/v1.3/markers/n/mark_g.png");
            } else {
                marker.setType("途经点");
                marker.setIconUrl("https://webapi.amap.com/theme/v1.3/markers/n/mark_b.png");
            }
            
            markers.add(marker);
        }
        
        return markers;
    }
    
    
    /**
     * 生成地图URL
     */
    private String generateMapUrl(List<Location> locations, MapResponse.CenterPoint center, MapResponse.RouteInfo routeInfo) {
        try {
            // 优先生成包含路线的地图URL
            if (routeInfo != null && routeInfo.getPoints() != null && !routeInfo.getPoints().isEmpty()) {
                // 使用高德地图Web版URL，能更好地显示路线和标记点
                return generateAmapWebUrl(locations, routeInfo);
            }
            
            // 否则生成包含所有标记点的地图
            return generateMarkerMapUrl(locations, center);
            
        } catch (Exception e) {
            log.error("生成地图URL异常: {}", e.getMessage(), e);
            return "https://uri.amap.com/marker?position=116.397428,39.90923&name=地图";
        }
    }
    
    /**
     * 生成包含标记点的地图URL（只显示起点和终点）
     */
    private String generateMarkerMapUrl(List<Location> locations, MapResponse.CenterPoint center) {
        try {
            // 如果没有路线信息，生成只显示起点和终点的地图
            if (locations.size() >= 2) {
                return generateSimpleRouteUrl(locations);
            }
            
            // 如果只有一个地点，显示该地点
            if (locations.size() == 1) {
                Location location = locations.get(0);
                StringBuilder url = new StringBuilder("https://uri.amap.com/marker?");
                url.append("position=").append(location.getLongitude()).append(",").append(location.getLatitude());
                url.append("&name=").append(java.net.URLEncoder.encode(location.getName(), "UTF-8"));
                url.append("&src=").append("tourism-backend");
                
                log.info("生成单点地图URL: {}", url.toString());
                return url.toString();
            }
            
            // 默认返回高德地图首页
            return "https://ditu.amap.com/";
            
        } catch (Exception e) {
            log.error("生成标记点地图URL异常: {}", e.getMessage(), e);
            return "https://ditu.amap.com/";
        }
    }
    
    /**
     * 生成包含路线的地图URL
     */
    private String generateRouteMapUrl(List<Location> locations, MapResponse.RouteInfo routeInfo) {
        try {
            // 使用高德地图的Web版路线规划URL，支持多点路线
            StringBuilder url = new StringBuilder("https://uri.amap.com/navigation?");
            
            // 添加起点
            if (!locations.isEmpty()) {
                Location start = locations.get(0);
                url.append("from=").append(start.getLongitude()).append(",").append(start.getLatitude());
                url.append("&fromName=").append(java.net.URLEncoder.encode(start.getName(), "UTF-8"));
            }
            
            // 添加终点
            if (locations.size() > 1) {
                Location end = locations.get(locations.size() - 1);
                url.append("&to=").append(end.getLongitude()).append(",").append(end.getLatitude());
                url.append("&toName=").append(java.net.URLEncoder.encode(end.getName(), "UTF-8"));
            }
            
            // 添加途经点 - 修正格式，使用分号分隔
            if (locations.size() > 2) {
                url.append("&waypoints=");
                for (int i = 1; i < locations.size() - 1; i++) {
                    if (i > 1) url.append(";");
                    Location waypoint = locations.get(i);
                    url.append(waypoint.getLongitude()).append(",").append(waypoint.getLatitude());
                }
            }
            
            // 根据路线策略设置模式
            String mode = "car";
            if ("walking".equals(routeInfo.getStrategy())) {
                mode = "walk";
            } else if ("transit".equals(routeInfo.getStrategy())) {
                mode = "bus";
            }
            
            url.append("&src=").append("tourism-backend");
            url.append("&policy=").append("LEAST_TIME"); // 最少时间策略
            url.append("&mode=").append(mode); // 根据策略设置模式
            
            log.info("生成路线地图URL: {}", url.toString());
            return url.toString();
            
        } catch (Exception e) {
            log.error("生成路线地图URL异常: {}", e.getMessage(), e);
            // 如果路线URL生成失败，回退到标记点地图
            return generateMarkerMapUrl(locations, calculateMapCenter(locations));
        }
    }
    
    /**
     * 生成高德地图Web版URL（只显示起点终点和路径，显示地点名称）
     */
    private String generateAmapWebUrl(List<Location> locations, MapResponse.RouteInfo routeInfo) {
        try {
            // 使用高德地图的路线规划URL，只显示起点、终点和路径
            StringBuilder url = new StringBuilder("https://uri.amap.com/navigation?");
            
            // 只添加起点和终点，不添加中心点，确保显示地点名称
            if (!locations.isEmpty()) {
                Location start = locations.get(0);
                url.append("from=").append(start.getLongitude()).append(",").append(start.getLatitude());
                // 确保地点名称正确编码和显示
                String startName = start.getName(); //!= null ? start.getName() : "起点";
                url.append("&fromName=").append(java.net.URLEncoder.encode(startName, "UTF-8"));
                log.debug("设置起点名称: {}", startName);
            }
            
            if (locations.size() > 1) {
                Location end = locations.get(locations.size() - 1);
                url.append("&to=").append(end.getLongitude()).append(",").append(end.getLatitude());
                // 确保地点名称正确编码和显示
                String endName = end.getName(); //!= null ? end.getName() : "终点";
                url.append("&toName=").append(java.net.URLEncoder.encode(endName, "UTF-8"));
                log.debug("设置终点名称: {}", endName);
            }
            
            // 添加途经点（如果有的话）
            if (locations.size() > 2) {
                url.append("&waypoints=");
                for (int i = 1; i < locations.size() - 1; i++) {
                    if (i > 1) url.append(";");
                    Location waypoint = locations.get(i);
                    url.append(waypoint.getLongitude()).append(",").append(waypoint.getLatitude());
                }
            }
            
            // 根据路线策略设置模式
            String mode = "car";
            if ("walking".equals(routeInfo.getStrategy())) {
                mode = "walk";
            } else if ("transit".equals(routeInfo.getStrategy())) {
                mode = "bus";
            }
            
            url.append("&src=").append("tourism-backend");
            url.append("&policy=").append("LEAST_TIME");
            url.append("&mode=").append(mode);
            
            log.info("生成路线地图URL（显示地点名称）: {}", url.toString());
            return url.toString();
            
        } catch (Exception e) {
            log.error("生成路线地图URL异常: {}", e.getMessage(), e);
            return generateSimpleRouteUrl(locations);
        }
    }
    
    /**
     * 生成简单的路线URL（只有起点和终点，显示地点名称）
     */
    private String generateSimpleRouteUrl(List<Location> locations) {
        try {
            if (locations.size() < 2) {
                return "https://ditu.amap.com/";
            }
            
            StringBuilder url = new StringBuilder("https://uri.amap.com/navigation?");
            Location start = locations.get(0);
            Location end = locations.get(locations.size() - 1);
            
            // 设置起点坐标和名称
            url.append("from=").append(start.getLongitude()).append(",").append(start.getLatitude());
            String startName = start.getName(); //!= null ? start.getName() : "起点";
            url.append("&fromName=").append(java.net.URLEncoder.encode(startName, "UTF-8"));
            
            // 设置终点坐标和名称
            url.append("&to=").append(end.getLongitude()).append(",").append(end.getLatitude());
            String endName = end.getName();
            url.append("&toName=").append(java.net.URLEncoder.encode(endName, "UTF-8"));
            
            url.append("&src=").append("tourism-backend");
            url.append("&mode=").append("car");
            
            log.info("生成简单路线URL（显示地点名称）: {}", url.toString());
            return url.toString();
            
        } catch (Exception e) {
            log.error("生成简单路线URL异常: {}", e.getMessage(), e);
            return "https://ditu.amap.com/";
        }
    }
    
    /**
     * 计算最优缩放级别
     */
    private Integer calculateOptimalZoom(List<Location> locations) {
        if (locations.size() <= 1) {
            return 15;
        }
        
        // 计算所有点之间的最大距离
        double maxDistance = 0.0;
        for (int i = 0; i < locations.size(); i++) {
            for (int j = i + 1; j < locations.size(); j++) {
                double distance = calculateDistance(
                    locations.get(i).getLatitude(), locations.get(i).getLongitude(),
                    locations.get(j).getLatitude(), locations.get(j).getLongitude()
                );
                maxDistance = Math.max(maxDistance, distance);
            }
        }
        
        // 根据距离计算缩放级别
        if (maxDistance < 1000) return 15;      // 1公里内
        if (maxDistance < 5000) return 13;      // 5公里内
        if (maxDistance < 20000) return 11;     // 20公里内
        if (maxDistance < 100000) return 9;     // 100公里内
        return 7;                               // 更远距离
    }
    
    /**
     * 计算两点间距离（公里）
     */
    private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        final int R = 6371; // 地球半径（公里）
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
    
    /**
     * 测试API连接
     * @return 测试结果
     */
    public String testApiConnection() {
        try {
            log.info("开始测试API连接...");
            
            String url = amapConfig.getBaseUrl() + amapConfig.getGeocodeUrl() + 
                        "?key=" + amapConfig.getKey() + 
                        "&address=北京&output=json";
            
            log.info("测试URL: {}", url);
            
            WebClient webClient = webClientBuilder.build();
            Mono<String> response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class);
            
            String responseBody = response.block();
            log.info("API测试响应: {}", responseBody);
            
            return "API连接成功: " + responseBody;
            
        } catch (Exception e) {
            log.error("API连接测试失败: {}", e.getMessage(), e);
            return "API连接失败: " + e.getMessage();
        }
    }
}
