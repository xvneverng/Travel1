package com.tourism.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 高德地图地理编码响应DTO
 */
@Data
public class AmapGeocodeResponse {
    
    private String status;
    private String info;
    private String infocode;
    private String count;
    private List<Geocode> geocodes;
    
    @Data
    public static class Geocode {
        private String formatted_address;
        private String country;
        private String province;
        private String citycode;
        private String city;
        
        @JsonProperty("district")
        private Object district; // 原始数据，可能是字符串或数组
        
        private List<String> township;
        private String adcode;
        private List<String> street;
        private List<String> number;
        private String location;
        private String level;
        private Neighborhood neighborhood;
        private Building building;
        
        // 获取district的字符串值
        public String getDistrictString() {
            if (district == null) {
                return null;
            }
            if (district instanceof String) {
                return (String) district;
            }
            if (district instanceof List) {
                List<?> list = (List<?>) district;
                if (!list.isEmpty() && list.get(0) instanceof String) {
                    return (String) list.get(0);
                }
            }
            return district.toString();
        }
    }
    
    @Data
    public static class Neighborhood {
        private List<String> name;
        private List<String> type;
    }
    
    @Data
    public static class Building {
        private List<String> name;
        private List<String> type;
    }
}
