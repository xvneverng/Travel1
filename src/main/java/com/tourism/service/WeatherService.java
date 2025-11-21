package com.tourism.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tourism.config.AmapConfig;
import com.tourism.dto.WeatherResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 天气服务类
 * 
 * @author tourism
 * @since 2025-01-27
 */
@Slf4j
@Service
public class WeatherService {
    
    @Autowired
    private AmapConfig amapConfig;
    
    @Autowired
    private WebClient.Builder webClientBuilder;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 获取天气预报
     * 
     * @param city 城市名称
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 天气预报数据
     */
    public Map<String, Object> getWeatherForecast(String city, String startDate, String endDate) {
        try {
            log.info("开始获取天气预报: 城市={}, 开始日期={}, 结束日期={}", city, startDate, endDate);
            
            // 调用高德天气API
            String url = "https://restapi.amap.com/v3/weather/weatherInfo" +
                        "?key=" + amapConfig.getKey() +
                        "&city=" + city +
                        "&extensions=all" +
                        "&output=json";
            
            log.debug("天气API请求URL: {}", url);
            
            WebClient webClient = webClientBuilder.build();
            Mono<String> response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class);
            
            String responseBody = response.block();
            log.debug("天气API响应: {}", responseBody);
            
            WeatherResponse weatherResponse = objectMapper.readValue(responseBody, WeatherResponse.class);
            
            if ("1".equals(weatherResponse.getStatus()) && 
                weatherResponse.getForecasts() != null && 
                !weatherResponse.getForecasts().isEmpty() &&
                weatherResponse.getForecasts().get(0).getCasts() != null) {
                
                // 过滤指定日期范围的天气数据
                List<WeatherResponse.Cast> filteredCasts = filterWeatherByDateRange(
                    weatherResponse.getForecasts().get(0).getCasts(), startDate, endDate);
                
                // 转换为前端需要的格式
                List<Map<String, Object>> weatherList = convertToFrontendFormat(filteredCasts);
                
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("data", weatherList);
                result.put("city", weatherResponse.getForecasts().get(0).getCity());
                result.put("message", "获取天气预报成功");
                
                log.info("天气预报获取成功: 城市={}, 数据条数={}", city, weatherList.size());
                return result;
                
            } else {
                log.error("天气API调用失败: status={}, info={}", 
                    weatherResponse.getStatus(), weatherResponse.getInfo());
                
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "获取天气预报失败: " + weatherResponse.getInfo());
                return result;
            }
            
        } catch (Exception e) {
            log.error("获取天气预报异常: {}", e.getMessage(), e);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "获取天气预报异常: " + e.getMessage());
            return result;
        }
    }
    
    /**
     * 根据日期范围过滤天气数据
     */
    private List<WeatherResponse.Cast> filterWeatherByDateRange(
            List<WeatherResponse.Cast> casts, String startDate, String endDate) {
        
        List<WeatherResponse.Cast> filteredCasts = new ArrayList<>();
        
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            
            for (WeatherResponse.Cast cast : casts) {
                LocalDate castDate = LocalDate.parse(cast.getDate());
                if (!castDate.isBefore(start) && !castDate.isAfter(end)) {
                    filteredCasts.add(cast);
                }
            }
            
            log.info("日期过滤完成: 原始数据={}条, 过滤后={}条", casts.size(), filteredCasts.size());
            
        } catch (Exception e) {
            log.error("日期过滤异常: {}", e.getMessage(), e);
            // 如果日期解析失败，返回所有数据
            return casts;
        }
        
        return filteredCasts;
    }
    
    /**
     * 转换为前端需要的格式
     */
    private List<Map<String, Object>> convertToFrontendFormat(List<WeatherResponse.Cast> casts) {
        List<Map<String, Object>> weatherList = new ArrayList<>();
        
        for (WeatherResponse.Cast cast : casts) {
            Map<String, Object> weatherItem = new HashMap<>();
            
            // 格式化日期
            String formattedDate = formatDate(cast.getDate(), cast.getWeek());
            weatherItem.put("date", formattedDate);
            
            // 设置天气图标
            String weatherIcon = getWeatherIcon(cast.getDayweather());
            weatherItem.put("icon", weatherIcon);
            
            // 设置温度范围
            String tempRange = cast.getDaytemp() + "-" + cast.getNighttemp() + "°C";
            weatherItem.put("temp", tempRange);
            
            // 设置天气描述
            String weatherDesc = cast.getDayweather();
            weatherItem.put("desc", weatherDesc);
            
            // 设置详细信息
            weatherItem.put("dayweather", cast.getDayweather());
            weatherItem.put("nightweather", cast.getNightweather());
            weatherItem.put("daytemp", cast.getDaytemp());
            weatherItem.put("nighttemp", cast.getNighttemp());
            weatherItem.put("daywind", cast.getDaywind());
            weatherItem.put("nightwind", cast.getNightwind());
            weatherItem.put("daypower", cast.getDaypower());
            weatherItem.put("nightpower", cast.getNightpower());
            
            weatherList.add(weatherItem);
        }
        
        return weatherList;
    }
    
    /**
     * 格式化日期
     */
    private String formatDate(String date, String week) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M月d日");
            String formattedDate = localDate.format(formatter);
            
            // 添加星期信息
            String weekText = getWeekText(week);
            return formattedDate + " " + weekText;
            
        } catch (Exception e) {
            log.error("日期格式化异常: {}", e.getMessage(), e);
            return date;
        }
    }
    
    /**
     * 获取星期文本
     */
    private String getWeekText(String week) {
        switch (week) {
            case "1": return "星期一";
            case "2": return "星期二";
            case "3": return "星期三";
            case "4": return "星期四";
            case "5": return "星期五";
            case "6": return "星期六";
            case "7": return "星期日";
            default: return "星期" + week;
        }
    }
    
    /**
     * 根据天气状况获取图标
     */
    private String getWeatherIcon(String weather) {
        if (weather == null) return "☀️";
        
        switch (weather) {
            case "晴": return "☀️";
            case "多云": return "⛅";
            case "阴": return "☁️";
            case "小雨": return "🌦️";
            case "中雨": return "🌧️";
            case "大雨": return "🌧️";
            case "暴雨": return "⛈️";
            case "雷阵雨": return "⛈️";
            case "雪": return "❄️";
            case "雾": return "🌫️";
            case "霾": return "🌫️";
            case "沙尘": return "🌪️";
            default: return "🌤️";
        }
    }
}
