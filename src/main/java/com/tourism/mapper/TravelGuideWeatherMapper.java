package com.tourism.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tourism.entity.TravelGuideWeather;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 路书与天气建议关联Mapper接口
 * 提供路书与天气建议关联相关的数据库操作
 * 继承BaseMapper获得基础的CRUD操作
 * 
 * @author tourism
 * @since 2025-01-27
 */
@Mapper
public interface TravelGuideWeatherMapper extends BaseMapper<TravelGuideWeather> {
    
    /**
     * 根据路书ID查询关联的天气建议列表
     * 
     * @param guideId 路书ID
     * @return 关联列表
     */
    default List<TravelGuideWeather> findByGuideId(Long guideId) {
        LambdaQueryWrapper<TravelGuideWeather> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelGuideWeather::getGuideId, guideId)
               .orderByAsc(TravelGuideWeather::getCreatedTime);
        return selectList(wrapper);
    }
    
    /**
     * 根据天气建议ID查询关联的路书列表
     * 
     * @param weatherSuggestionId 天气建议ID
     * @return 关联列表
     */
    default List<TravelGuideWeather> findByWeatherSuggestionId(Long weatherSuggestionId) {
        LambdaQueryWrapper<TravelGuideWeather> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelGuideWeather::getWeatherSuggestionId, weatherSuggestionId)
               .orderByAsc(TravelGuideWeather::getCreatedTime);
        return selectList(wrapper);
    }
    
    /**
     * 根据路书ID和天气建议ID查询关联信息
     * 
     * @param guideId 路书ID
     * @param weatherSuggestionId 天气建议ID
     * @return 关联信息
     */
    default TravelGuideWeather findByGuideIdAndWeatherSuggestionId(Long guideId, Long weatherSuggestionId) {
        LambdaQueryWrapper<TravelGuideWeather> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelGuideWeather::getGuideId, guideId)
               .eq(TravelGuideWeather::getWeatherSuggestionId, weatherSuggestionId);
        return selectOne(wrapper);
    }
    
    /**
     * 根据路书ID查询已应用的天气建议
     * 
     * @param guideId 路书ID
     * @return 已应用的关联列表
     */
    default List<TravelGuideWeather> findAppliedByGuideId(Long guideId) {
        LambdaQueryWrapper<TravelGuideWeather> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelGuideWeather::getGuideId, guideId)
               .eq(TravelGuideWeather::getIsApplied, true)
               .orderByDesc(TravelGuideWeather::getAppliedTime);
        return selectList(wrapper);
    }
    
    /**
     * 根据路书ID查询未应用的天气建议
     * 
     * @param guideId 路书ID
     * @return 未应用的关联列表
     */
    default List<TravelGuideWeather> findNotAppliedByGuideId(Long guideId) {
        LambdaQueryWrapper<TravelGuideWeather> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelGuideWeather::getGuideId, guideId)
               .eq(TravelGuideWeather::getIsApplied, false)
               .orderByAsc(TravelGuideWeather::getCreatedTime);
        return selectList(wrapper);
    }
    
    /**
     * 根据路书ID删除所有关联
     * 
     * @param guideId 路书ID
     * @return 删除的记录数
     */
    default int deleteByGuideId(Long guideId) {
        LambdaQueryWrapper<TravelGuideWeather> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelGuideWeather::getGuideId, guideId);
        return delete(wrapper);
    }
    
    /**
     * 根据天气建议ID删除所有关联
     * 
     * @param weatherSuggestionId 天气建议ID
     * @return 删除的记录数
     */
    default int deleteByWeatherSuggestionId(Long weatherSuggestionId) {
        LambdaQueryWrapper<TravelGuideWeather> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelGuideWeather::getWeatherSuggestionId, weatherSuggestionId);
        return delete(wrapper);
    }
    
    /**
     * 根据路书ID和天气建议ID删除关联
     * 
     * @param guideId 路书ID
     * @param weatherSuggestionId 天气建议ID
     * @return 删除的记录数
     */
    default int deleteByGuideIdAndWeatherSuggestionId(Long guideId, Long weatherSuggestionId) {
        LambdaQueryWrapper<TravelGuideWeather> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelGuideWeather::getGuideId, guideId)
               .eq(TravelGuideWeather::getWeatherSuggestionId, weatherSuggestionId);
        return delete(wrapper);
    }
    
    /**
     * 检查路书和天气建议的关联是否存在
     * 
     * @param guideId 路书ID
     * @param weatherSuggestionId 天气建议ID
     * @return 是否存在
     */
    default boolean existsByGuideIdAndWeatherSuggestionId(Long guideId, Long weatherSuggestionId) {
        LambdaQueryWrapper<TravelGuideWeather> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelGuideWeather::getGuideId, guideId)
               .eq(TravelGuideWeather::getWeatherSuggestionId, weatherSuggestionId);
        return selectCount(wrapper) > 0;
    }
    
    /**
     * 根据路书ID统计关联的天气建议数量
     * 
     * @param guideId 路书ID
     * @return 关联数量
     */
    default Long countByGuideId(Long guideId) {
        LambdaQueryWrapper<TravelGuideWeather> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelGuideWeather::getGuideId, guideId);
        return selectCount(wrapper);
    }
    
    /**
     * 根据路书ID统计已应用的天气建议数量
     * 
     * @param guideId 路书ID
     * @return 已应用数量
     */
    default Long countAppliedByGuideId(Long guideId) {
        LambdaQueryWrapper<TravelGuideWeather> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelGuideWeather::getGuideId, guideId)
               .eq(TravelGuideWeather::getIsApplied, true);
        return selectCount(wrapper);
    }
}
