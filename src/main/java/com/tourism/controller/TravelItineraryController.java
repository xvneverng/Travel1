package com.tourism.controller;

import com.tourism.dto.TravelItineraryRequest;
import com.tourism.dto.TravelItineraryResponse;
import com.tourism.service.TravelItineraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 旅游行程控制器
 * 提供行程相关的REST API接口
 * 
 * @author tourism
 * @since 2025-01-27
 */
@RestController
@RequestMapping("/api/travel/itinerary")
@CrossOrigin(origins = "*")
@Validated
public class TravelItineraryController {
    
    @Autowired
    private TravelItineraryService travelItineraryService;
    
    /**
     * 保存或更新行程
     * 
     * @param request 行程请求数据
     * @return 保存结果
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> saveOrUpdateItinerary(@RequestBody TravelItineraryRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 参数验证
            if (request.getUserId() == null || request.getUserId().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "用户ID不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (request.getTravelDate() == null) {
                response.put("success", false);
                response.put("message", "旅行日期不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (request.getDayNumber() == null || request.getDayNumber() <= 0) {
                response.put("success", false);
                response.put("message", "天数必须大于0");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 保存行程
            TravelItineraryResponse result = travelItineraryService.saveOrUpdateItinerary(request);
            
            response.put("success", true);
            response.put("message", "行程保存成功");
            response.put("data", result);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "保存行程失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * 根据ID获取行程详情
     * 
     * @param id 行程ID
     * @return 行程详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getItineraryById(@PathVariable @NotNull @Positive Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            TravelItineraryResponse result = travelItineraryService.getItineraryById(id);
            
            if (result != null) {
                response.put("success", true);
                response.put("message", "获取行程成功");
                response.put("data", result);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "行程不存在");
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取行程失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * 根据用户ID和日期获取行程
     * 
     * @param userId 用户ID
     * @param travelDate 旅行日期
     * @return 行程详情
     */
    @GetMapping("/user/{userId}/date/{travelDate}")
    public ResponseEntity<Map<String, Object>> getItineraryByUserIdAndDate(
            @PathVariable @NotBlank String userId,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate travelDate) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            TravelItineraryResponse result = travelItineraryService.getItineraryByUserIdAndDate(userId, travelDate);
            
            if (result != null) {
                response.put("success", true);
                response.put("message", "获取行程成功");
                response.put("data", result);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "该日期没有行程");
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取行程失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * 根据用户ID和天数获取行程
     * 
     * @param userId 用户ID
     * @param dayNumber 第几天
     * @return 行程详情
     */
    @GetMapping("/user/{userId}/day/{dayNumber}")
    public ResponseEntity<Map<String, Object>> getItineraryByUserIdAndDayNumber(
            @PathVariable @NotBlank String userId,
            @PathVariable @NotNull @Positive Integer dayNumber) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            TravelItineraryResponse result = travelItineraryService.getItineraryByUserIdAndDayNumber(userId, dayNumber);
            
            if (result != null) {
                response.put("success", true);
                response.put("message", "获取行程成功");
                response.put("data", result);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "该天数没有行程");
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取行程失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * 根据用户ID获取所有行程
     * 
     * @param userId 用户ID
     * @return 行程列表
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getItinerariesByUserId(@PathVariable @NotBlank String userId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<TravelItineraryResponse> result = travelItineraryService.getItinerariesByUserId(userId);
            
            response.put("success", true);
            response.put("message", "获取行程列表成功");
            response.put("data", result);
            response.put("count", result.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取行程列表失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * 删除行程
     * 
     * @param id 行程ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteItinerary(@PathVariable @NotNull @Positive Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean result = travelItineraryService.deleteItinerary(id);
            
            if (result) {
                response.put("success", true);
                response.put("message", "删除行程成功");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "行程不存在或删除失败");
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "删除行程失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * 保存或更新旅游感受
     * 将旅游感受保存到travel_itinerary表的travel_feelings字段中
     * 
     * @param request 请求体，包含userId、dayNumber、content
     * @return 保存结果
     */
    @PostMapping("/feelings")
    public ResponseEntity<Map<String, Object>> saveTravelFeelings(@RequestBody Map<String, Object> request) {
        try {
            // 获取请求参数
            String userId = (String) request.get("userId");
            Integer dayNumber = (Integer) request.get("dayNumber");
            String content = (String) request.get("content");
            
            // 参数验证
            if (userId == null || userId.trim().isEmpty()) {
                userId = travelItineraryService.getDefaultUserId();
            }
            if (dayNumber == null) {
                dayNumber = 1; // 默认为DAY1
            }
            if (content == null) {
                content = "";
            }
            
            // 保存或更新感受
            TravelItineraryResponse response = travelItineraryService.saveOrUpdateTravelFeelings(userId, dayNumber, content);
            
            // 返回成功响应
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", "旅游感受保存成功");
            result.put("data", response);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            // 返回错误响应
            Map<String, Object> result = new HashMap<>();
            result.put("code", 500);
            result.put("message", "保存失败: " + e.getMessage());
            result.put("data", null);
            
            return ResponseEntity.status(500).body(result);
        }
    }
    
    /**
     * 获取旅游感受
     * 从travel_itinerary表的travel_feelings字段中获取感受内容
     * 
     * @param dayNumber 天数
     * @param userId 用户ID（可选，不传则使用默认用户）
     * @return 旅游感受内容
     */
    @GetMapping("/feelings/{dayNumber}")
    public ResponseEntity<Map<String, Object>> getTravelFeelings(
            @PathVariable @Positive(message = "天数必须为正数") Integer dayNumber,
            @RequestParam(required = false) String userId) {
        try {
            // 使用默认用户ID如果未提供
            if (userId == null || userId.trim().isEmpty()) {
                userId = travelItineraryService.getDefaultUserId();
            }
            
            // 获取感受内容
            String content = travelItineraryService.getTravelFeelings(userId, dayNumber);
            
            Map<String, Object> result = new HashMap<>();
            if (content != null && !content.trim().isEmpty()) {
                result.put("code", 200);
                result.put("message", "获取成功");
                result.put("data", content);
            } else {
                result.put("code", 404);
                result.put("message", "未找到旅游感受");
                result.put("data", "");
            }
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("code", 500);
            result.put("message", "获取失败: " + e.getMessage());
            result.put("data", null);
            
            return ResponseEntity.status(500).body(result);
        }
    }
    
    /**
     * 删除旅游感受
     * 将travel_itinerary表的travel_feelings字段设置为null
     * 
     * @param dayNumber 天数
     * @param userId 用户ID（可选）
     * @return 删除结果
     */
    @DeleteMapping("/feelings/{dayNumber}")
    public ResponseEntity<Map<String, Object>> deleteTravelFeelings(
            @PathVariable @Positive(message = "天数必须为正数") Integer dayNumber,
            @RequestParam(required = false) String userId) {
        try {
            if (userId == null || userId.trim().isEmpty()) {
                userId = travelItineraryService.getDefaultUserId();
            }
            
            boolean deleted = travelItineraryService.deleteTravelFeelings(userId, dayNumber);
            
            Map<String, Object> result = new HashMap<>();
            if (deleted) {
                result.put("code", 200);
                result.put("message", "删除成功");
                result.put("data", null);
            } else {
                result.put("code", 404);
                result.put("message", "未找到要删除的旅游感受");
                result.put("data", null);
            }
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("code", 500);
            result.put("message", "删除失败: " + e.getMessage());
            result.put("data", null);
            
            return ResponseEntity.status(500).body(result);
        }
    }
    
    /**
     * 添加照片到行程
     * 
     * @param id 行程ID
     * @param file 上传的照片文件
     * @return 添加结果
     */
    @PostMapping("/{id}/photos")
    public ResponseEntity<Map<String, Object>> addPhoto(
            @PathVariable @NotNull @Positive Long id,
            @RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (file == null || file.isEmpty()) {
                response.put("success", false);
                response.put("message", "请选择要上传的照片");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                response.put("success", false);
                response.put("message", "请上传图片文件");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 验证文件大小 (5MB)
            if (file.getSize() > 5 * 1024 * 1024) {
                response.put("success", false);
                response.put("message", "图片大小不能超过5MB");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 将文件转换为Base64
            byte[] fileBytes = file.getBytes();
            String base64Photo = "data:" + contentType + ";base64," + 
                java.util.Base64.getEncoder().encodeToString(fileBytes);
            
            boolean result = travelItineraryService.addPhoto(id, base64Photo);
            
            if (result) {
                response.put("success", true);
                response.put("message", "照片添加成功");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "行程不存在或添加失败");
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "添加照片失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * 删除行程中的照片
     * 
     * @param id 行程ID
     * @param request 包含照片URL的请求体
     * @return 删除结果
     */
    @DeleteMapping("/{id}/photos")
    public ResponseEntity<Map<String, Object>> removePhoto(
            @PathVariable @NotNull @Positive Long id,
            @RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String photoUrl = request.get("photoUrl");
            if (photoUrl == null || photoUrl.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "照片URL不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            
            boolean result = travelItineraryService.removePhoto(id, photoUrl.trim());
            
            if (result) {
                response.put("success", true);
                response.put("message", "照片删除成功");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "照片不存在或删除失败");
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "删除照片失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * 更新行程照片列表
     * 
     * @param id 行程ID
     * @param request 包含照片URL列表的请求体
     * @return 更新结果
     */
    @PutMapping("/{id}/photos")
    public ResponseEntity<Map<String, Object>> updatePhotos(
            @PathVariable @NotNull @Positive Long id,
            @RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            @SuppressWarnings("unchecked")
            List<String> photoUrls = (List<String>) request.get("photoUrls");
            if (photoUrls == null) {
                photoUrls = new ArrayList<>();
            }
            
            boolean result = travelItineraryService.updatePhotos(id, photoUrls);
            
            if (result) {
                response.put("success", true);
                response.put("message", "照片列表更新成功");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "行程不存在或更新失败");
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "更新照片列表失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * 清空行程所有照片
     * 
     * @param id 行程ID
     * @return 清空结果
     */
    @DeleteMapping("/{id}/photos/all")
    public ResponseEntity<Map<String, Object>> clearAllPhotos(
            @PathVariable @NotNull @Positive Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean result = travelItineraryService.clearAllPhotos(id);
            
            if (result) {
                response.put("success", true);
                response.put("message", "所有照片已清空");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "行程不存在或清空失败");
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "清空照片失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * 获取行程照片列表
     * 
     * @param id 行程ID
     * @return 照片列表
     */
    @GetMapping("/{id}/photos")
    public ResponseEntity<Map<String, Object>> getPhotos(
            @PathVariable @NotNull @Positive Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<String> photos = travelItineraryService.getPhotos(id);
            
            response.put("success", true);
            response.put("message", "获取照片列表成功");
            response.put("data", photos);
            response.put("count", photos.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取照片列表失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * 健康检查接口
     * 
     * @return 服务状态
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "旅游行程服务运行正常");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
}
