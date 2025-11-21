package com.tourism.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tourism.entity.WeatherSuggestion;
import com.tourism.mapper.WeatherSuggestionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 天气建议服务类
 * 提供天气建议相关的业务逻辑
 * 
 * @author tourism
 * @since 2025-01-27
 */
@Service
public class WeatherSuggestionService extends ServiceImpl<WeatherSuggestionMapper, WeatherSuggestion> {
    
    /**
     * 获取所有启用的天气建议
     * 
     * @return 天气建议列表
     */
    public List<WeatherSuggestion> getAllActiveSuggestions() {
        return baseMapper.findActiveSuggestions();
    }
    
    /**
     * 根据建议类型获取天气建议
     * 
     * @param suggestionType 建议类型
     * @return 天气建议列表
     */
    public List<WeatherSuggestion> getSuggestionsByType(String suggestionType) {
        return baseMapper.findBySuggestionType(suggestionType);
    }
    
    /**
     * 根据天气条件获取天气建议
     * 
     * @param weatherCondition 天气条件
     * @return 天气建议列表
     */
    public List<WeatherSuggestion> getSuggestionsByWeather(String weatherCondition) {
        return baseMapper.findByWeatherCondition(weatherCondition);
    }
    
    /**
     * 根据关键词搜索天气建议
     * 
     * @param keyword 关键词
     * @return 天气建议列表
     */
    public List<WeatherSuggestion> searchSuggestions(String keyword) {
        return baseMapper.searchByKeyword(keyword);
    }
    
    /**
     * 添加新的天气建议
     * 
     * @param suggestion 天气建议
     * @return 是否添加成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean addSuggestion(WeatherSuggestion suggestion) {
        if (suggestion.getIsActive() == null) {
            suggestion.setIsActive(true);
        }
        if (suggestion.getPriority() == null) {
            suggestion.setPriority("medium");
        }
        return save(suggestion);
    }
    
    /**
     * 更新天气建议
     * 
     * @param suggestion 天气建议
     * @return 是否更新成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateSuggestion(WeatherSuggestion suggestion) {
        return updateById(suggestion);
    }
    
    /**
     * 删除天气建议（逻辑删除，设置为不启用）
     * 
     * @param id 建议ID
     * @return 是否删除成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteSuggestion(Long id) {
        WeatherSuggestion suggestion = getById(id);
        if (suggestion != null) {
            suggestion.setIsActive(false);
            return updateById(suggestion);
        }
        return false;
    }
    
    /**
     * 启用/禁用天气建议
     * 
     * @param id 建议ID
     * @param isActive 是否启用
     * @return 是否更新成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean toggleSuggestionStatus(Long id, boolean isActive) {
        WeatherSuggestion suggestion = getById(id);
        if (suggestion != null) {
            suggestion.setIsActive(isActive);
            return updateById(suggestion);
        }
        return false;
    }
    
    /**
     * 批量更新天气建议
     * 
     * @param suggestions 天气建议列表
     * @return 是否更新成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean batchUpdateSuggestions(List<WeatherSuggestion> suggestions) {
        return updateBatchById(suggestions);
    }
}
