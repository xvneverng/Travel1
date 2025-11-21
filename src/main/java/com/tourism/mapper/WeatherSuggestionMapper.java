package com.tourism.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tourism.entity.WeatherSuggestion;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 天气建议Mapper接口
 * 提供天气建议相关的数据库操作
 * 继承BaseMapper获得基础的CRUD操作
 * 
 * @author tourism
 * @since 2025-01-27
 */
@Mapper
public interface WeatherSuggestionMapper extends BaseMapper<WeatherSuggestion> {
    
    /**
     * 根据建议类型查询天气建议列表
     * 
     * @param suggestionType 建议类型
     * @return 天气建议列表
     */
    default List<WeatherSuggestion> findBySuggestionType(String suggestionType) {
        LambdaQueryWrapper<WeatherSuggestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WeatherSuggestion::getSuggestionType, suggestionType)
               .eq(WeatherSuggestion::getIsActive, true)
               .orderByAsc(WeatherSuggestion::getPriority);
        return selectList(wrapper);
    }
    
    /**
     * 根据天气条件查询天气建议列表
     * 
     * @param weatherCondition 天气条件
     * @return 天气建议列表
     */
    default List<WeatherSuggestion> findByWeatherCondition(String weatherCondition) {
        LambdaQueryWrapper<WeatherSuggestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WeatherSuggestion::getWeatherCondition, weatherCondition)
               .eq(WeatherSuggestion::getIsActive, true)
               .orderByAsc(WeatherSuggestion::getPriority);
        return selectList(wrapper);
    }
    
    /**
     * 查询所有启用的天气建议
     * 
     * @return 天气建议列表
     */
    default List<WeatherSuggestion> findActiveSuggestions() {
        LambdaQueryWrapper<WeatherSuggestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WeatherSuggestion::getIsActive, true)
               .orderByAsc(WeatherSuggestion::getPriority)
               .orderByAsc(WeatherSuggestion::getId);
        return selectList(wrapper);
    }
    
    /**
     * 根据关键词搜索天气建议
     * 
     * @param keyword 关键词
     * @return 天气建议列表
     */
    default List<WeatherSuggestion> searchByKeyword(String keyword) {
        LambdaQueryWrapper<WeatherSuggestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(WeatherSuggestion::getContent, keyword)
               .eq(WeatherSuggestion::getIsActive, true)
               .orderByAsc(WeatherSuggestion::getPriority);
        return selectList(wrapper);
    }
}
