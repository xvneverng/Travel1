package com.tourism.controller;

import com.tourism.entity.WeatherSuggestion;
import com.tourism.service.WeatherSuggestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 天气建议控制器
 * 提供天气建议相关的REST API接口
 * 
 * @author tourism
 * @since 2025-01-27
 */
@RestController
@RequestMapping("/api/weather/suggestions")
@CrossOrigin(origins = "*")
public class WeatherSuggestionController {
    
    @Autowired
    private WeatherSuggestionService weatherSuggestionService;
    
    /**
     * 获取所有启用的天气建议
     * 
     * @return 天气建议列表
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllSuggestions() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<WeatherSuggestion> suggestions = weatherSuggestionService.getAllActiveSuggestions();
            
            response.put("success", true);
            response.put("data", suggestions);
            response.put("message", "获取天气建议成功");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取天气建议失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * 根据ID获取天气建议
     * 
     * @param id 建议ID
     * @return 天气建议详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getSuggestionById(
            @PathVariable @NotNull @Positive Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            WeatherSuggestion suggestion = weatherSuggestionService.getById(id);
            
            if (suggestion != null) {
                response.put("success", true);
                response.put("data", suggestion);
                response.put("message", "获取天气建议成功");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "天气建议不存在");
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取天气建议失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * 根据建议类型获取天气建议
     * 
     * @param type 建议类型
     * @return 天气建议列表
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<Map<String, Object>> getSuggestionsByType(
            @PathVariable String type) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<WeatherSuggestion> suggestions = weatherSuggestionService.getSuggestionsByType(type);
            
            response.put("success", true);
            response.put("data", suggestions);
            response.put("message", "获取天气建议成功");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取天气建议失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * 根据天气条件获取天气建议
     * 
     * @param weather 天气条件
     * @return 天气建议列表
     */
    @GetMapping("/weather/{weather}")
    public ResponseEntity<Map<String, Object>> getSuggestionsByWeather(
            @PathVariable String weather) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<WeatherSuggestion> suggestions = weatherSuggestionService.getSuggestionsByWeather(weather);
            
            response.put("success", true);
            response.put("data", suggestions);
            response.put("message", "获取天气建议成功");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取天气建议失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * 搜索天气建议
     * 
     * @param keyword 关键词
     * @return 天气建议列表
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchSuggestions(
            @RequestParam String keyword) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<WeatherSuggestion> suggestions = weatherSuggestionService.searchSuggestions(keyword);
            
            response.put("success", true);
            response.put("data", suggestions);
            response.put("message", "搜索天气建议成功");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "搜索天气建议失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * 添加新的天气建议
     * 
     * @param suggestion 天气建议
     * @return 添加结果
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> addSuggestion(
            @Valid @RequestBody WeatherSuggestion suggestion) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean result = weatherSuggestionService.addSuggestion(suggestion);
            
            if (result) {
                response.put("success", true);
                response.put("data", suggestion);
                response.put("message", "添加天气建议成功");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "添加天气建议失败");
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "添加天气建议失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * 更新天气建议
     * 
     * @param id 建议ID
     * @param suggestion 天气建议
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateSuggestion(
            @PathVariable @NotNull @Positive Long id,
            @Valid @RequestBody WeatherSuggestion suggestion) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            suggestion.setId(id);
            boolean result = weatherSuggestionService.updateSuggestion(suggestion);
            
            if (result) {
                response.put("success", true);
                response.put("data", suggestion);
                response.put("message", "更新天气建议成功");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "天气建议不存在或更新失败");
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "更新天气建议失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * 删除天气建议
     * 
     * @param id 建议ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteSuggestion(
            @PathVariable @NotNull @Positive Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean result = weatherSuggestionService.deleteSuggestion(id);
            
            if (result) {
                response.put("success", true);
                response.put("message", "删除天气建议成功");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "天气建议不存在或删除失败");
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "删除天气建议失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * 启用/禁用天气建议
     * 
     * @param id 建议ID
     * @param request 包含isActive字段的请求体
     * @return 更新结果
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> toggleSuggestionStatus(
            @PathVariable @NotNull @Positive Long id,
            @RequestBody Map<String, Boolean> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Boolean isActive = request.get("isActive");
            if (isActive == null) {
                response.put("success", false);
                response.put("message", "isActive字段不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            
            boolean result = weatherSuggestionService.toggleSuggestionStatus(id, isActive);
            
            if (result) {
                response.put("success", true);
                response.put("message", isActive ? "启用天气建议成功" : "禁用天气建议成功");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "天气建议不存在或更新失败");
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "更新天气建议状态失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * 批量更新天气建议
     * 
     * @param suggestions 天气建议列表
     * @return 更新结果
     */
    @PutMapping("/batch")
    public ResponseEntity<Map<String, Object>> batchUpdateSuggestions(
            @Valid @RequestBody List<WeatherSuggestion> suggestions) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean result = weatherSuggestionService.batchUpdateSuggestions(suggestions);
            
            if (result) {
                response.put("success", true);
                response.put("data", suggestions);
                response.put("message", "批量更新天气建议成功");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "批量更新天气建议失败");
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "批量更新天气建议失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
