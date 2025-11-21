package com.tourism.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tourism.entity.TravelGuideItinerary;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 路书与行程关联Mapper接口
 * 提供路书与行程关联相关的数据库操作
 * 继承BaseMapper获得基础的CRUD操作
 * 
 * @author tourism
 * @since 2025-01-27
 */
@Mapper
public interface TravelGuideItineraryMapper extends BaseMapper<TravelGuideItinerary> {
    
    /**
     * 根据路书ID查询关联的行程列表
     * 
     * @param guideId 路书ID
     * @return 关联的行程列表
     */
    default List<TravelGuideItinerary> findByGuideId(Long guideId) {
        LambdaQueryWrapper<TravelGuideItinerary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelGuideItinerary::getGuideId, guideId)
               .orderByAsc(TravelGuideItinerary::getCreatedTime);
        return selectList(wrapper);
    }
    
    /**
     * 根据行程ID查询关联的路书列表
     * 
     * @param itineraryId 行程ID
     * @return 关联的路书列表
     */
    default List<TravelGuideItinerary> findByItineraryId(Long itineraryId) {
        LambdaQueryWrapper<TravelGuideItinerary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelGuideItinerary::getItineraryId, itineraryId)
               .orderByAsc(TravelGuideItinerary::getCreatedTime);
        return selectList(wrapper);
    }
    
    /**
     * 根据路书ID和行程ID查询关联记录
     * 
     * @param guideId 路书ID
     * @param itineraryId 行程ID
     * @return 关联记录
     */
    default TravelGuideItinerary findByGuideIdAndItineraryId(Long guideId, Long itineraryId) {
        LambdaQueryWrapper<TravelGuideItinerary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelGuideItinerary::getGuideId, guideId)
               .eq(TravelGuideItinerary::getItineraryId, itineraryId);
        return selectOne(wrapper);
    }
    
    /**
     * 根据路书ID删除所有关联记录
     * 
     * @param guideId 路书ID
     * @return 删除的记录数
     */
    default int deleteByGuideId(Long guideId) {
        LambdaQueryWrapper<TravelGuideItinerary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelGuideItinerary::getGuideId, guideId);
        return delete(wrapper);
    }
    
    /**
     * 根据行程ID删除所有关联记录
     * 
     * @param itineraryId 行程ID
     * @return 删除的记录数
     */
    default int deleteByItineraryId(Long itineraryId) {
        LambdaQueryWrapper<TravelGuideItinerary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelGuideItinerary::getItineraryId, itineraryId);
        return delete(wrapper);
    }
    
    /**
     * 根据路书ID和行程ID删除关联记录
     * 
     * @param guideId 路书ID
     * @param itineraryId 行程ID
     * @return 删除的记录数
     */
    default int deleteByGuideIdAndItineraryId(Long guideId, Long itineraryId) {
        LambdaQueryWrapper<TravelGuideItinerary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelGuideItinerary::getGuideId, guideId)
               .eq(TravelGuideItinerary::getItineraryId, itineraryId);
        return delete(wrapper);
    }
    
    /**
     * 根据路书ID统计关联的行程数量
     * 
     * @param guideId 路书ID
     * @return 关联的行程数量
     */
    default Long countByGuideId(Long guideId) {
        LambdaQueryWrapper<TravelGuideItinerary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelGuideItinerary::getGuideId, guideId);
        return selectCount(wrapper);
    }
    
    /**
     * 根据行程ID统计关联的路书数量
     * 
     * @param itineraryId 行程ID
     * @return 关联的路书数量
     */
    default Long countByItineraryId(Long itineraryId) {
        LambdaQueryWrapper<TravelGuideItinerary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelGuideItinerary::getItineraryId, itineraryId);
        return selectCount(wrapper);
    }
    
    /**
     * 检查路书和行程是否已关联
     * 
     * @param guideId 路书ID
     * @param itineraryId 行程ID
     * @return 是否已关联
     */
    default boolean existsByGuideIdAndItineraryId(Long guideId, Long itineraryId) {
        LambdaQueryWrapper<TravelGuideItinerary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelGuideItinerary::getGuideId, guideId)
               .eq(TravelGuideItinerary::getItineraryId, itineraryId);
        return selectCount(wrapper) > 0;
    }
}
