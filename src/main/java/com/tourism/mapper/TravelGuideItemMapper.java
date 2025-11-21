package com.tourism.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tourism.entity.TravelGuideItem;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 路书事项Mapper接口
 * 提供路书事项相关的数据库操作
 * 继承BaseMapper获得基础的CRUD操作
 * 
 * @author tourism
 * @since 2025-01-27
 */
@Mapper
public interface TravelGuideItemMapper extends BaseMapper<TravelGuideItem> {
    
    /**
     * 根据路书ID查询事项列表
     * 
     * @param guideId 路书ID
     * @return 事项列表
     */
    default List<TravelGuideItem> findByGuideId(Long guideId) {
        LambdaQueryWrapper<TravelGuideItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelGuideItem::getGuideId, guideId)
               .orderByAsc(TravelGuideItem::getId);
        return selectList(wrapper);
    }
    
    /**
     * 根据路书ID和类别查询事项列表
     * 
     * @param guideId 路书ID
     * @param category 事项类别
     * @return 事项列表
     */
    default List<TravelGuideItem> findByGuideIdAndCategory(Long guideId, String category) {
        LambdaQueryWrapper<TravelGuideItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelGuideItem::getGuideId, guideId)
               .eq(TravelGuideItem::getCategory, category)
               .orderByAsc(TravelGuideItem::getId);
        return selectList(wrapper);
    }
    
    /**
     * 根据路书ID和状态查询事项列表
     * 
     * @param guideId 路书ID
     * @param status 完成状态
     * @return 事项列表
     */
    default List<TravelGuideItem> findByGuideIdAndStatus(Long guideId, String status) {
        LambdaQueryWrapper<TravelGuideItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelGuideItem::getGuideId, guideId)
               .eq(TravelGuideItem::getStatus, status)
               .orderByAsc(TravelGuideItem::getId);
        return selectList(wrapper);
    }
    
    
    /**
     * 根据路书ID统计各类别事项数量
     * 
     * @param guideId 路书ID
     * @return 类别统计结果
     */
    default List<TravelGuideItem> countByGuideIdAndCategory(Long guideId) {
        LambdaQueryWrapper<TravelGuideItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelGuideItem::getGuideId, guideId)
               .select(TravelGuideItem::getCategory)
               .groupBy(TravelGuideItem::getCategory);
        return selectList(wrapper);
    }
    
    /**
     * 根据路书ID统计各状态事项数量
     * 
     * @param guideId 路书ID
     * @return 状态统计结果
     */
    default List<TravelGuideItem> countByGuideIdAndStatus(Long guideId) {
        LambdaQueryWrapper<TravelGuideItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelGuideItem::getGuideId, guideId)
               .select(TravelGuideItem::getStatus)
               .groupBy(TravelGuideItem::getStatus);
        return selectList(wrapper);
    }
    
    /**
     * 根据路书ID删除所有事项
     * 
     * @param guideId 路书ID
     * @return 删除的记录数
     */
    default int deleteByGuideId(Long guideId) {
        LambdaQueryWrapper<TravelGuideItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelGuideItem::getGuideId, guideId);
        return delete(wrapper);
    }
    
    /**
     * 根据路书ID和类别删除事项
     * 
     * @param guideId 路书ID
     * @param category 事项类别
     * @return 删除的记录数
     */
    default int deleteByGuideIdAndCategory(Long guideId, String category) {
        LambdaQueryWrapper<TravelGuideItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelGuideItem::getGuideId, guideId)
               .eq(TravelGuideItem::getCategory, category);
        return delete(wrapper);
    }
    
    /**
     * 批量更新事项状态
     * 
     * @param guideId 路书ID
     * @param category 事项类别
     * @param status 新状态
     * @return 更新的记录数
     */
    default int updateStatusByGuideIdAndCategory(Long guideId, String category, String status) {
        LambdaQueryWrapper<TravelGuideItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelGuideItem::getGuideId, guideId)
               .eq(TravelGuideItem::getCategory, category);
        
        TravelGuideItem updateItem = new TravelGuideItem();
        updateItem.setStatus(status);
        
        return update(updateItem, wrapper);
    }
}
