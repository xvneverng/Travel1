package com.tourism.dto;

import lombok.Data;
import java.util.List;

/**
 * 天气API响应DTO
 * 
 * @author tourism
 * @since 2025-01-27
 */
@Data
public class WeatherResponse {
    
    /**
     * 状态码
     */
    private String status;
    
    /**
     * 状态信息
     */
    private String info;
    
    /**
     * 信息码
     */
    private String infocode;
    
    /**
     * 数据条数
     */
    private String count;
    
    /**
     * 预报数据
     */
    private List<Forecasts> forecasts;
    
    @Data
    public static class Forecasts {
        /**
         * 城市信息
         */
        private String city;
        
        /**
         * 城市编码
         */
        private String adcode;
        
        /**
         * 省份
         */
        private String province;
        
        /**
         * 报告时间
         */
        private String reporttime;
        
        /**
         * 预报数据列表
         */
        private List<Cast> casts;
    }
    
    @Data
    public static class Cast {
        /**
         * 日期
         */
        private String date;
        
        /**
         * 星期
         */
        private String week;
        
        /**
         * 白天天气
         */
        private String dayweather;
        
        /**
         * 夜间天气
         */
        private String nightweather;
        
        /**
         * 白天温度
         */
        private String daytemp;
        
        /**
         * 夜间温度
         */
        private String nighttemp;

        /**
         * 白天实时温度（浮点型）
         */
        private String daytemp_float;

        /**
         * 夜间实时温度（浮点型）
         */
        private String nighttemp_float;
        
        /**
         * 白天风向
         */
        private String daywind;
        
        /**
         * 夜间风向
         */
        private String nightwind;
        
        /**
         * 白天风力
         */
        private String daypower;
        
        /**
         * 夜间风力
         */
        private String nightpower;
    }
}
