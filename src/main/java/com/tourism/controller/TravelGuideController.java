package com.tourism.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.tourism.entity.TravelGuide;
import com.tourism.entity.TravelGuideItem;
import com.tourism.mapper.TravelGuideMapper;
import com.tourism.mapper.TravelGuideItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 旅游指南控制器
 * 提供旅游指南相关的REST API接口
 * 
 * @author tourism
 * @since 2025-01-27
 */
@RestController
@RequestMapping("/api/travel/guide")
@CrossOrigin(origins = "*")
public class TravelGuideController {
    
    @Autowired
    private TravelGuideMapper travelGuideMapper;
    
    @Autowired
    private TravelGuideItemMapper travelGuideItemMapper;
    
    /**
     * 更新我的注意事项（保存到数据库）
     * 
     * @param request 请求体，包含userId、myNotes
     * @return 更新结果
     */
    @PostMapping("/my-notes/update")
    public ResponseEntity<Map<String, Object>> updateMyNotes(@RequestBody Map<String, Object> request) {
        try {
            // 获取请求参数
            String userId = (String) request.get("userId");
            String myNotes = (String) request.get("myNotes");
            
            // 参数验证
            if (userId == null || userId.trim().isEmpty()) {
                userId = "default_user"; // 使用默认用户ID
            }
            if (myNotes == null) {
                myNotes = "";
            }
            
            // 更新我的注意事项（保存到数据库）
            boolean success = updateMyNotesToDatabase(userId, myNotes);
            
            // 返回成功响应
            Map<String, Object> result = new HashMap<>();
            if (success) {
                result.put("code", 200);
                result.put("message", "我的注意事项更新成功");
                result.put("data", myNotes);
            } else {
                result.put("code", 500);
                result.put("message", "更新失败");
                result.put("data", null);
            }
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            // 返回错误响应
            Map<String, Object> result = new HashMap<>();
            result.put("code", 500);
            result.put("message", "更新失败: " + e.getMessage());
            result.put("data", null);
            
            return ResponseEntity.status(500).body(result);
        }
    }
    
    /**
     * 获取我的注意事项（从数据库读取）
     * 
     * @param userId 用户ID（可选，不传则使用默认用户）
     * @return 注意事项内容
     */
    @GetMapping("/my-notes")
    public ResponseEntity<Map<String, Object>> getMyNotes(@RequestParam(required = false) String userId) {
        try {
            // 使用默认用户ID如果未提供
            if (userId == null || userId.trim().isEmpty()) {
                userId = "default_user";
            }
            
            // 从数据库获取注意事项内容
            String myNotes = getMyNotesFromDatabase(userId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", myNotes != null ? myNotes : "");
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("code", 500);
            result.put("message", "获取失败: " + e.getMessage());
            result.put("data", "");
            
            return ResponseEntity.status(500).body(result);
        }
    }
    
    /**
     * 获取完整的旅游指南信息（包含我的注意事项）
     * 
     * @param userId 用户ID（可选，不传则使用默认用户）
     * @return 完整的旅游指南信息
     */
    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getGuideOverview(@RequestParam(required = false) String userId) {
        try {
            // 使用默认用户ID如果未提供
            if (userId == null || userId.trim().isEmpty()) {
                userId = "default_user";
            }
            
            // 获取用户的旅游指南
            List<TravelGuide> guides = travelGuideMapper.findByUserId(userId);
            TravelGuide guide = guides.isEmpty() ? null : guides.get(0);
            
            // 获取我的注意事项
            String myNotes = getMyNotesFromDatabase(userId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", "获取成功");
            
            Map<String, Object> data = new HashMap<>();
            data.put("guide", guide);
            data.put("myNotes", myNotes != null ? myNotes : "");
            result.put("data", data);
            
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
     * 更新我的注意事项到数据库
     * 
     * @param userId 用户ID
     * @param myNotes 注意事项内容
     * @return 是否更新成功
     */
    private boolean updateMyNotesToDatabase(String userId, String myNotes) {
        try {
            // 查找用户的旅游指南记录
            List<TravelGuide> guides = travelGuideMapper.findByUserId(userId);
            
            if (guides.isEmpty()) {
                // 如果不存在旅游指南记录，创建一个新的
                TravelGuide guide = new TravelGuide();
                guide.setUserId(userId);
                guide.setGuideName("我的旅游指南");
                guide.setDestination("南京");
                guide.setDescription("个人旅游准备清单");
                guide.setMyNotes(myNotes);
                guide.setStatus("draft");
                guide.setIsPublic(false);
                
                int result = travelGuideMapper.insert(guide);
                return result > 0;
            } else {
                // 更新现有旅游指南的我的注意事项
                TravelGuide guide = guides.get(0);
                
                LambdaUpdateWrapper<TravelGuide> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(TravelGuide::getId, guide.getId())
                            .set(TravelGuide::getMyNotes, myNotes);
                
                int result = travelGuideMapper.update(null, updateWrapper);
                return result > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 从数据库获取我的注意事项内容
     * 
     * @param userId 用户ID
     * @return 注意事项内容，如果不存在则返回null
     */
    private String getMyNotesFromDatabase(String userId) {
        try {
            // 获取用户的旅游指南
            List<TravelGuide> guides = travelGuideMapper.findByUserId(userId);
            if (guides.isEmpty()) {
                return null;
            }
            
            TravelGuide guide = guides.get(0);
            return guide.getMyNotes();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 更新准备事项（保存到数据库）
     * 
     * @param request 请求体，包含userId、preparationItems
     * @return 更新结果
     */
    @PostMapping("/preparation-items/update")
    public ResponseEntity<Map<String, Object>> updatePreparationItems(@RequestBody Map<String, Object> request) {
        try {
            System.out.println("=== 收到更新请求 ===");
            System.out.println("请求数据: " + request);
            
            // 获取请求参数
            String userId = (String) request.get("userId");
            @SuppressWarnings("unchecked")
            Map<String, Object> preparationItems = (Map<String, Object>) request.get("preparationItems");
            
            System.out.println("用户ID: " + userId);
            System.out.println("准备事项数据: " + preparationItems);
            
            // 参数验证
            if (userId == null || userId.trim().isEmpty()) {
                userId = "default_user";
            }
            if (preparationItems == null) {
                preparationItems = new HashMap<>();
            }
            
            // 更新准备事项到数据库
            boolean success = updatePreparationItemsToDatabase(userId, preparationItems);
            System.out.println("更新结果: " + success);
            
            // 返回成功响应
            Map<String, Object> result = new HashMap<>();
            if (success) {
                result.put("code", 200);
                result.put("message", "准备事项更新成功");
                result.put("data", preparationItems);
            } else {
                result.put("code", 500);
                result.put("message", "更新失败");
                result.put("data", null);
            }
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            // 返回错误响应
            Map<String, Object> result = new HashMap<>();
            result.put("code", 500);
            result.put("message", "更新失败: " + e.getMessage());
            result.put("data", null);
            
            return ResponseEntity.status(500).body(result);
        }
    }
    
    /**
     * 获取行程信息（从数据库读取）
     * 
     * @param userId 用户ID（可选，不传则使用默认用户）
     * @return 行程信息数据
     */
    @GetMapping("/travel-info")
    public ResponseEntity<Map<String, Object>> getTravelInfo(@RequestParam(required = false) String userId) {
        try {
            // 参数验证
            if (userId == null || userId.trim().isEmpty()) {
                userId = "default_user";
            }
            
            // 获取用户的旅游指南
            List<TravelGuide> guides = travelGuideMapper.findByUserId(userId);
            if (guides.isEmpty()) {
                // 如果没有记录，创建默认记录
                TravelGuide defaultGuide = new TravelGuide();
                defaultGuide.setUserId(userId);
                defaultGuide.setGuideName("南京古都5日深度游");
                defaultGuide.setDestination("江苏南京");
                defaultGuide.setStartDate(java.time.LocalDate.of(2025, 8, 21));
                defaultGuide.setEndDate(java.time.LocalDate.of(2025, 8, 25));
                defaultGuide.setDurationDays(5);
                defaultGuide.setDurationDesc("5天4晚");
                defaultGuide.setDescription("漫步古都南京,感受深厚的历史文化底蕴。从庄严肃穆的中山陵到繁华热闹的夫子庙,从古朴厚重的明城墙到秀美宁静的玄武湖,体验金陵城的独特魅力。");
                defaultGuide.setMyNotes("暂无注意事项");
                defaultGuide.setStatus("active");
                defaultGuide.setIsPublic(false);
                
                travelGuideMapper.insert(defaultGuide);
                guides = travelGuideMapper.findByUserId(userId);
            }
            
            TravelGuide guide = guides.get(0);
            
            // 构建返回数据
            Map<String, Object> travelInfo = new HashMap<>();
            travelInfo.put("guideName", guide.getGuideName());
            travelInfo.put("destination", guide.getDestination());
            travelInfo.put("startDate", guide.getStartDate());
            travelInfo.put("endDate", guide.getEndDate());
            travelInfo.put("durationDays", guide.getDurationDays());
            travelInfo.put("durationDesc", guide.getDurationDesc());
            travelInfo.put("description", guide.getDescription());
            travelInfo.put("myNotes", guide.getMyNotes());
            
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", travelInfo);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> result = new HashMap<>();
            result.put("code", 500);
            result.put("message", "获取失败: " + e.getMessage());
            result.put("data", null);
            
            return ResponseEntity.status(500).body(result);
        }
    }
    
    /**
     * 更新行程信息（保存到数据库）
     * 
     * @param request 请求体，包含userId、travelInfo
     * @return 更新结果
     */
    @PostMapping("/travel-info/update")
    public ResponseEntity<Map<String, Object>> updateTravelInfo(@RequestBody Map<String, Object> request) {
        try {
            System.out.println("=== 收到行程信息更新请求 ===");
            System.out.println("请求数据: " + request);
            
            // 获取请求参数
            String userId = (String) request.get("userId");
            @SuppressWarnings("unchecked")
            Map<String, Object> travelInfo = (Map<String, Object>) request.get("travelInfo");
            
            System.out.println("用户ID: " + userId);
            System.out.println("行程信息数据: " + travelInfo);
            
            // 参数验证
            if (userId == null || userId.trim().isEmpty()) {
                userId = "default_user";
            }
            if (travelInfo == null) {
                travelInfo = new HashMap<>();
            }
            
            // 获取用户的旅游指南
            List<TravelGuide> guides = travelGuideMapper.findByUserId(userId);
            if (guides.isEmpty()) {
                Map<String, Object> result = new HashMap<>();
                result.put("code", 404);
                result.put("message", "未找到旅游指南记录");
                result.put("data", null);
                return ResponseEntity.status(404).body(result);
            }
            
            TravelGuide guide = guides.get(0);
            
            // 更新字段
            if (travelInfo.containsKey("guideName")) {
                guide.setGuideName((String) travelInfo.get("guideName"));
            }
            if (travelInfo.containsKey("destination")) {
                guide.setDestination((String) travelInfo.get("destination"));
            }
            if (travelInfo.containsKey("startDate")) {
                String startDateStr = (String) travelInfo.get("startDate");
                if (startDateStr != null && !startDateStr.isEmpty()) {
                    guide.setStartDate(java.time.LocalDate.parse(startDateStr));
                }
            }
            if (travelInfo.containsKey("endDate")) {
                String endDateStr = (String) travelInfo.get("endDate");
                if (endDateStr != null && !endDateStr.isEmpty()) {
                    guide.setEndDate(java.time.LocalDate.parse(endDateStr));
                }
            }
            if (travelInfo.containsKey("durationDays")) {
                guide.setDurationDays((Integer) travelInfo.get("durationDays"));
            }
            if (travelInfo.containsKey("durationDesc")) {
                guide.setDurationDesc((String) travelInfo.get("durationDesc"));
            }
            if (travelInfo.containsKey("description")) {
                guide.setDescription((String) travelInfo.get("description"));
            }
            if (travelInfo.containsKey("myNotes")) {
                guide.setMyNotes((String) travelInfo.get("myNotes"));
            }
            
            // 保存到数据库
            int updateResult = travelGuideMapper.updateById(guide);
            System.out.println("更新结果: " + updateResult);
            
            Map<String, Object> result = new HashMap<>();
            if (updateResult > 0) {
                result.put("code", 200);
                result.put("message", "行程信息更新成功");
                result.put("data", travelInfo);
            } else {
                result.put("code", 500);
                result.put("message", "更新失败");
                result.put("data", null);
            }
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> result = new HashMap<>();
            result.put("code", 500);
            result.put("message", "更新失败: " + e.getMessage());
            result.put("data", null);
            
            return ResponseEntity.status(500).body(result);
        }
    }
    
    /**
     * 获取准备事项（从数据库读取）
     * 
     * @param userId 用户ID（可选，不传则使用默认用户）
     * @return 准备事项内容
     */
    @GetMapping("/preparation-items")
    public ResponseEntity<Map<String, Object>> getPreparationItems(@RequestParam(required = false) String userId) {
        try {
            // 使用默认用户ID如果未提供
            if (userId == null || userId.trim().isEmpty()) {
                userId = "default_user";
            }
            
            System.out.println("获取准备事项，用户ID: " + userId);
            
            // 从数据库获取准备事项内容
            Map<String, Object> preparationItems = getPreparationItemsFromDatabase(userId);
            
            System.out.println("准备事项数据: " + preparationItems);
            
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", preparationItems);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> result = new HashMap<>();
            result.put("code", 500);
            result.put("message", "获取失败: " + e.getMessage());
            result.put("data", null);
            
            return ResponseEntity.status(500).body(result);
        }
    }
    
    /**
     * 更新准备事项到数据库
     * 
     * @param userId 用户ID
     * @param preparationItems 准备事项数据
     * @return 是否更新成功
     */
    private boolean updatePreparationItemsToDatabase(String userId, Map<String, Object> preparationItems) {
        try {
            // 查找用户的旅游指南记录
            List<TravelGuide> guides = travelGuideMapper.findByUserId(userId);
            Long guideId;
            
            if (guides.isEmpty()) {
                // 如果不存在旅游指南记录，创建一个新的
                TravelGuide guide = new TravelGuide();
                guide.setUserId(userId);
                guide.setGuideName("我的旅游指南");
                guide.setDestination("南京");
                guide.setDescription("个人旅游准备清单");
                guide.setMyNotes("");
                guide.setStatus("draft");
                guide.setIsPublic(false);
                
                int result = travelGuideMapper.insert(guide);
                if (result <= 0) {
                    return false;
                }
                guideId = guide.getId();
            } else {
                guideId = guides.get(0).getId();
            }
            
            // 获取现有的准备事项
            List<TravelGuideItem> existingItems = travelGuideItemMapper.findByGuideId(guideId);
            Map<String, TravelGuideItem> existingItemMap = new HashMap<>();
            for (TravelGuideItem item : existingItems) {
                String key = item.getCategory() + ":" + item.getItemName();
                existingItemMap.put(key, item);
            }
            
            // 处理新的准备事项数据
            Set<String> processedKeys = new HashSet<>();
            for (Map.Entry<String, Object> entry : preparationItems.entrySet()) {
                String category = entry.getKey();
                @SuppressWarnings("unchecked")
                Map<String, Object> categoryData = (Map<String, Object>) entry.getValue();
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> items = (List<Map<String, Object>>) categoryData.get("items");
                
                if (items != null) {
                    for (Map<String, Object> item : items) {
                        String itemName = (String) item.get("name");
                        String key = category + ":" + itemName;
                        processedKeys.add(key);
                        
                        boolean isCompleted = Boolean.TRUE.equals(item.get("completed"));
                        String status = isCompleted ? "completed" : "pending";
                        
                        System.out.println("处理项目: " + itemName + ", 完成状态: " + isCompleted + ", 数据库状态: " + status);
                        
                        if (existingItemMap.containsKey(key)) {
                            // 更新现有项目
                            TravelGuideItem existingItem = existingItemMap.get(key);
                            System.out.println("现有项目状态: " + existingItem.getStatus() + ", 新状态: " + status);
                            
                            if (!status.equals(existingItem.getStatus())) {
                                System.out.println("状态需要更新，从 " + existingItem.getStatus() + " 到 " + status);
                                existingItem.setStatus(status);
                                // 确保更新时间被设置
                                existingItem.setUpdatedTime(java.time.LocalDateTime.now());
                                int updateResult = travelGuideItemMapper.updateById(existingItem);
                                System.out.println("更新结果: " + updateResult);
                            } else {
                                System.out.println("状态相同，无需更新");
                            }
                        } else {
                            // 插入新项目
                            TravelGuideItem newItem = new TravelGuideItem();
                            newItem.setGuideId(guideId);
                            newItem.setCategory(category);
                            newItem.setItemName(itemName);
                            newItem.setStatus(status);
                            newItem.setCreatedTime(java.time.LocalDateTime.now());
                            newItem.setUpdatedTime(java.time.LocalDateTime.now());
                            int insertResult = travelGuideItemMapper.insert(newItem);
                            System.out.println("插入新项目结果: " + insertResult);
                        }
                    }
                }
            }
            
            // 删除不再存在的项目
            for (Map.Entry<String, TravelGuideItem> entry : existingItemMap.entrySet()) {
                if (!processedKeys.contains(entry.getKey())) {
                    travelGuideItemMapper.deleteById(entry.getValue().getId());
                }
            }
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 从数据库获取准备事项内容
     * 
     * @param userId 用户ID
     * @return 准备事项内容，优先返回数据库数据，如果不存在则返回默认数据
     */
    private Map<String, Object> getPreparationItemsFromDatabase(String userId) {
        try {
            // 获取默认数据作为基础
            Map<String, Object> result = getDefaultPreparationItems();
            
            // 获取用户的旅游指南
            List<TravelGuide> guides = travelGuideMapper.findByUserId(userId);
            if (guides.isEmpty()) {
                System.out.println("用户 " + userId + " 没有旅游指南记录，返回默认数据");
                return result;
            }
            
            Long guideId = guides.get(0).getId();
            System.out.println("找到旅游指南，ID: " + guideId);
            
            // 获取所有准备事项
            List<TravelGuideItem> items = travelGuideItemMapper.findByGuideId(guideId);
            System.out.println("找到 " + items.size() + " 个准备事项");
            
            if (items.isEmpty()) {
                System.out.println("旅游指南中没有准备事项，返回默认数据");
                return result;
            }
            
            // 按类别分组
            Map<String, List<Map<String, Object>>> categoryItems = new HashMap<>();
            
            for (TravelGuideItem item : items) {
                String category = item.getCategory();
                if (!categoryItems.containsKey(category)) {
                    categoryItems.put(category, new ArrayList<>());
                }
                
                Map<String, Object> itemData = new HashMap<>();
                itemData.put("name", item.getItemName());
                itemData.put("completed", "completed".equals(item.getStatus()));
                itemData.put("editing", false);
                
                categoryItems.get(category).add(itemData);
            }
            
            // 更新结果数据，用数据库中的数据覆盖默认数据
            for (Map.Entry<String, List<Map<String, Object>>> entry : categoryItems.entrySet()) {
                String category = entry.getKey();
                List<Map<String, Object>> categoryItemList = entry.getValue();
                
                Map<String, Object> categoryData = new HashMap<>();
                int completed = (int) categoryItemList.stream().filter(item -> (Boolean) item.get("completed")).count();
                int total = categoryItemList.size();
                
                categoryData.put("completed", completed);
                categoryData.put("total", total);
                categoryData.put("percentage", total > 0 ? Math.round((completed * 100.0) / total) : 0);
                categoryData.put("items", categoryItemList);
                
                result.put(category, categoryData);
            }
            
            System.out.println("返回的准备事项数据: " + result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("获取准备事项时发生异常: " + e.getMessage());
            return getDefaultPreparationItems();
        }
    }
    
    /**
     * 获取默认的准备事项数据
     */
    private Map<String, Object> getDefaultPreparationItems() {
        Map<String, Object> defaultItems = new HashMap<>();
        
        // 交通准备
        Map<String, Object> transport = new HashMap<>();
        transport.put("completed", 1);
        transport.put("total", 1);
        transport.put("percentage", 100);
        List<Map<String, Object>> transportItems = new ArrayList<>();
        Map<String, Object> metroCard = new HashMap<>();
        metroCard.put("name", "南京地铁卡");
        metroCard.put("completed", true);
        metroCard.put("editing", false);
        transportItems.add(metroCard);
        transport.put("items", transportItems);
        defaultItems.put("交通准备", transport);
        
        // 住宿安排
        Map<String, Object> accommodation = new HashMap<>();
        accommodation.put("completed", 1);
        accommodation.put("total", 2);
        accommodation.put("percentage", 50);
        List<Map<String, Object>> accommodationItems = new ArrayList<>();
        Map<String, Object> hotel = new HashMap<>();
        hotel.put("name", "夫子庙酒店确认");
        hotel.put("completed", true);
        hotel.put("editing", false);
        accommodationItems.add(hotel);
        Map<String, Object> breakfast = new HashMap<>();
        breakfast.put("name", "酒店早餐确认");
        breakfast.put("completed", false);
        breakfast.put("editing", false);
        accommodationItems.add(breakfast);
        accommodation.put("items", accommodationItems);
        defaultItems.put("住宿安排", accommodation);
        
        // 应用准备
        Map<String, Object> apps = new HashMap<>();
        apps.put("completed", 1);
        apps.put("total", 1);
        apps.put("percentage", 100);
        List<Map<String, Object>> appItems = new ArrayList<>();
        Map<String, Object> amap = new HashMap<>();
        amap.put("name", "高德地图下载");
        amap.put("completed", true);
        amap.put("editing", false);
        appItems.add(amap);
        apps.put("items", appItems);
        defaultItems.put("应用准备", apps);
        
        // 物品准备
        Map<String, Object> items = new HashMap<>();
        items.put("completed", 2);
        items.put("total", 4);
        items.put("percentage", 50);
        List<Map<String, Object>> itemList = new ArrayList<>();
        Map<String, Object> powerBank = new HashMap<>();
        powerBank.put("name", "充电宝");
        powerBank.put("completed", true);
        powerBank.put("editing", false);
        itemList.add(powerBank);
        Map<String, Object> shoes = new HashMap<>();
        shoes.put("name", "舒适步行鞋");
        shoes.put("completed", false);
        shoes.put("editing", false);
        itemList.add(shoes);
        Map<String, Object> umbrella = new HashMap<>();
        umbrella.put("name", "雨伞");
        umbrella.put("completed", false);
        umbrella.put("editing", false);
        itemList.add(umbrella);
        Map<String, Object> medicine = new HashMap<>();
        medicine.put("name", "常用药品");
        medicine.put("completed", true);
        medicine.put("editing", false);
        itemList.add(medicine);
        items.put("items", itemList);
        defaultItems.put("物品准备", items);
        
        // 预订信息
        Map<String, Object> bookings = new HashMap<>();
        bookings.put("completed", 2);
        bookings.put("total", 4);
        bookings.put("percentage", 50);
        List<Map<String, Object>> bookingItems = new ArrayList<>();
        Map<String, Object> mingXiaoLing = new HashMap<>();
        mingXiaoLing.put("name", "明孝陵门票");
        mingXiaoLing.put("completed", true);
        mingXiaoLing.put("editing", false);
        bookingItems.add(mingXiaoLing);
        Map<String, Object> zhongShanLing = new HashMap<>();
        zhongShanLing.put("name", "中山陵门票预约");
        zhongShanLing.put("completed", false);
        zhongShanLing.put("editing", false);
        bookingItems.add(zhongShanLing);
        Map<String, Object> museum = new HashMap<>();
        museum.put("name", "南京博物院预约");
        museum.put("completed", false);
        museum.put("editing", false);
        bookingItems.add(museum);
        Map<String, Object> president = new HashMap<>();
        president.put("name", "总统府门票");
        president.put("completed", true);
        president.put("editing", false);
        bookingItems.add(president);
        bookings.put("items", bookingItems);
        defaultItems.put("预订信息", bookings);
        
        return defaultItems;
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
        response.put("message", "旅游指南服务运行正常");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
}
