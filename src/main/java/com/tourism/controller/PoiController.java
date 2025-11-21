package com.tourism.controller;

import com.tourism.util.AmapApiUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/poi")
@CrossOrigin(origins = "*")
public class PoiController {

    @Autowired
    private AmapApiUtil amapApiUtil;

    /**
     * 测试POI搜索功能
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testPoiSearch() {
        Map<String, Object> resp = new HashMap<>();
        try {
            // 测试搜索南京的著名景点
            String[] testKeywords = {"中山陵", "夫子庙", "南京博物院", "总统府"};
            Map<String, String> results = new HashMap<>();
            
            for (String keyword : testKeywords) {
                String url = amapApiUtil.getPoiFirstPhotoUrl(keyword, "南京市");
                results.put(keyword, url);
            }
            
            resp.put("success", true);
            resp.put("testResults", results);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(resp);
        }
    }

    /**
     * 根据关键词和城市返回第一张POI照片URL
     */
    @GetMapping("/photo")
    public ResponseEntity<Map<String, Object>> getPoiPhoto(
            @RequestParam String keyword,
            @RequestParam(required = false) String city) {
        Map<String, Object> resp = new HashMap<>();
        try {
            String url = amapApiUtil.getPoiFirstPhotoUrl(keyword, city);
            resp.put("success", url != null);
            resp.put("url", url);
            if (url == null) {
                resp.put("message", "未找到POI照片");
            }
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(resp);
        }
    }
}


