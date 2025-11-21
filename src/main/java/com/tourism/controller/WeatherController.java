package com.tourism.controller;

import com.tourism.entity.TravelItinerary;
import com.tourism.service.TravelItineraryService;
import com.tourism.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 天气控制器
 * 
 * @author tourism
 * @since 2025-01-27
 */
@RestController
@RequestMapping("/api/weather")
@CrossOrigin(origins = "*")
public class WeatherController {
    
    @Autowired
    private WeatherService weatherService;
    
    @Autowired
    private TravelItineraryService travelItineraryService;
    
    /**
     * 获取天气预报
     * 
     * @param city 城市名称
     * @param startDate 开始日期 (格式: yyyy-MM-dd)
     * @param endDate 结束日期 (格式: yyyy-MM-dd)
     * @return 天气预报数据
     */
    @GetMapping("/forecast")
    public ResponseEntity<Map<String, Object>> getWeatherForecast(
            @RequestParam String city,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 兜底：如果前端未传日期，则默认返回5天（今天~今天+4）
            if (startDate == null || startDate.trim().isEmpty()) {
                startDate = formatDate(LocalDate.now());
            }
            if (endDate == null || endDate.trim().isEmpty()) {
                // 若未传endDate，则基于startDate返回5天区间（包含起始日）
                LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                endDate = formatDate(start.plusDays(4));
            }
            Map<String, Object> result = weatherService.getWeatherForecast(city, startDate, endDate);
            
            if ((Boolean) result.get("success")) {
                response.put("success", true);
                response.put("data", result.get("data"));
                response.put("city", result.get("city"));
                response.put("message", result.get("message"));
            } else {
                response.put("success", false);
                response.put("message", result.get("message"));
            }
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取天气预报失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * 根据行程ID获取天气预报
     * 
     * @param itineraryId 行程ID
     * @return 天气预报数据
     */
    @GetMapping("/forecast/itinerary/{itineraryId}")
    public ResponseEntity<Map<String, Object>> getWeatherForecastByItinerary(
            @PathVariable Long itineraryId) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 从数据库获取行程信息
            TravelItinerary itinerary = travelItineraryService.getById(itineraryId);
            if (itinerary == null) {
                response.put("success", false);
                response.put("message", "行程不存在");
                return ResponseEntity.ok(response);
            }
            
            // 从行程信息中提取城市
            String city = extractCityFromItinerary(itinerary);
            // 使用当前日期作为开始日期，默认显示5天（今天~今天+4）
            String startDate = formatDate(LocalDate.now());
            String endDate = formatDate(LocalDate.now().plusDays(4));
            
            Map<String, Object> result = weatherService.getWeatherForecast(city, startDate, endDate);
            
            if ((Boolean) result.get("success")) {
                response.put("success", true);
                response.put("data", result.get("data"));
                response.put("city", result.get("city"));
                response.put("message", result.get("message"));
            } else {
                response.put("success", false);
                response.put("message", result.get("message"));
            }
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取天气预报失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * 从行程信息中提取城市名称
     */
    private String extractCityFromItinerary(TravelItinerary itinerary) {
        // TravelItinerary实体没有destination字段，使用默认城市
        // 可以根据实际业务需求从其他地方获取城市信息
        return "南京";
    }
    
    /**
     * 格式化日期
     */
    private String formatDate(java.time.LocalDate date) {
        if (date == null) {
            return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
