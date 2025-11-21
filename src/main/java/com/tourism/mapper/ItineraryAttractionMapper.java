package com.tourism.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tourism.entity.ItineraryAttraction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 行程景点关联Mapper接口
 * 提供行程景点关联相关的数据库操作
 * 继承BaseMapper获得基础的CRUD操作
 * 
 * @author tourism
 * @since 2025-01-27
 */
@Mapper
public interface ItineraryAttractionMapper extends BaseMapper<ItineraryAttraction> {
    
    /**
     * 根据行程ID查询景点列表（按顺序排列）
     * 使用自定义SQL查询，包含景点详细信息
     * 
     * @param itineraryId 行程ID
     * @return 景点关联列表
     */
    @Select("SELECT ia.*, a.name, a.description, a.icon, a.duration, a.category, a.tags, a.tips, a.transportation_info, a.address " +
            "FROM itinerary_attraction ia " +
            "LEFT JOIN attraction a ON ia.attraction_id = a.id " +
            "WHERE ia.itinerary_id = #{itineraryId} " +
            "ORDER BY ia.sequence_order ASC")
    List<ItineraryAttraction> findByItineraryIdWithAttraction(@Param("itineraryId") Long itineraryId);
    
    /**
     * 根据行程ID查询景点列表（简单查询）
     * 使用MyBatis Plus的LambdaQueryWrapper
     * 
     * @param itineraryId 行程ID
     * @return 景点关联列表
     */
    default List<ItineraryAttraction> findByItineraryId(Long itineraryId) {
        LambdaQueryWrapper<ItineraryAttraction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ItineraryAttraction::getItineraryId, itineraryId)
               .orderByAsc(ItineraryAttraction::getSequenceOrder);
        return selectList(wrapper);
    }
    
    /**
     * 根据行程ID和景点ID查询关联信息
     * 
     * @param itineraryId 行程ID
     * @param attractionId 景点ID
     * @return 景点关联信息
     */
    default ItineraryAttraction findByItineraryIdAndAttractionId(Long itineraryId, Long attractionId) {
        LambdaQueryWrapper<ItineraryAttraction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ItineraryAttraction::getItineraryId, itineraryId)
               .eq(ItineraryAttraction::getAttractionId, attractionId);
        return selectOne(wrapper);
    }
    
    /**
     * 根据行程ID删除所有景点关联
     * 
     * @param itineraryId 行程ID
     * @return 删除的记录数
     */
    default int deleteByItineraryId(Long itineraryId) {
        LambdaQueryWrapper<ItineraryAttraction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ItineraryAttraction::getItineraryId, itineraryId);
        return delete(wrapper);
    }
    
    /**
     * 根据行程ID和景点ID删除关联
     * 
     * @param itineraryId 行程ID
     * @param attractionId 景点ID
     * @return 删除的记录数
     */
    default int deleteByItineraryIdAndAttractionId(Long itineraryId, Long attractionId) {
        LambdaQueryWrapper<ItineraryAttraction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ItineraryAttraction::getItineraryId, itineraryId)
               .eq(ItineraryAttraction::getAttractionId, attractionId);
        return delete(wrapper);
    }
    
    /**
     * 根据行程ID查询景点数量
     * 
     * @param itineraryId 行程ID
     * @return 景点数量
     */
    default Long countByItineraryId(Long itineraryId) {
        LambdaQueryWrapper<ItineraryAttraction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ItineraryAttraction::getItineraryId, itineraryId);
        return selectCount(wrapper);
    }
    
    /**
     * 检查行程中是否存在指定景点
     * 
     * @param itineraryId 行程ID
     * @param attractionId 景点ID
     * @return 是否存在
     */
    default boolean existsByItineraryIdAndAttractionId(Long itineraryId, Long attractionId) {
        LambdaQueryWrapper<ItineraryAttraction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ItineraryAttraction::getItineraryId, itineraryId)
               .eq(ItineraryAttraction::getAttractionId, attractionId);
        return selectCount(wrapper) > 0;
    }
    
    /**
     * 批量插入行程景点关联
     * 
     * @param itineraryAttractions 行程景点关联列表
     * @return 插入的记录数
     */
    default int insertBatch(List<ItineraryAttraction> itineraryAttractions) {
        if (itineraryAttractions == null || itineraryAttractions.isEmpty()) {
            return 0;
        }
        
        int count = 0;
        for (ItineraryAttraction attraction : itineraryAttractions) {
            count += insert(attraction);
        }
        return count;
    }
}
