package com.tourism.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tourism.entity.TravelGuideTemplate;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 路书模板Mapper接口
 * 提供路书模板相关的数据库操作
 * 继承BaseMapper获得基础的CRUD操作
 * 
 * @author tourism
 * @since 2025-01-27
 */
@Mapper
public interface TravelGuideTemplateMapper extends BaseMapper<TravelGuideTemplate> {
    
    /**
     * 根据模板名称查询模板
     * 
     * @param templateName 模板名称
     * @return 模板列表
     */
    default List<TravelGuideTemplate> findByTemplateName(String templateName) {
        LambdaQueryWrapper<TravelGuideTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(TravelGuideTemplate::getTemplateName, templateName)
               .orderByAsc(TravelGuideTemplate::getCreatedTime);
        return selectList(wrapper);
    }
    
    /**
     * 根据目的地查询模板
     * 
     * @param destination 目的地
     * @return 模板列表
     */
    default List<TravelGuideTemplate> findByDestination(String destination) {
        LambdaQueryWrapper<TravelGuideTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelGuideTemplate::getDestination, destination)
               .orderByAsc(TravelGuideTemplate::getCreatedTime);
        return selectList(wrapper);
    }
    
    /**
     * 根据是否系统模板查询
     * 
     * @param isSystem 是否系统模板
     * @return 模板列表
     */
    default List<TravelGuideTemplate> findByIsSystem(Boolean isSystem) {
        LambdaQueryWrapper<TravelGuideTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelGuideTemplate::getIsSystem, isSystem)
               .orderByAsc(TravelGuideTemplate::getCreatedTime);
        return selectList(wrapper);
    }
    
    /**
     * 根据创建者查询模板
     * 
     * @param createdBy 创建者
     * @return 模板列表
     */
    default List<TravelGuideTemplate> findByCreatedBy(String createdBy) {
        LambdaQueryWrapper<TravelGuideTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelGuideTemplate::getCreatedBy, createdBy)
               .orderByAsc(TravelGuideTemplate::getCreatedTime);
        return selectList(wrapper);
    }
    
    /**
     * 根据目的地和是否系统模板查询
     * 
     * @param destination 目的地
     * @param isSystem 是否系统模板
     * @return 模板列表
     */
    default List<TravelGuideTemplate> findByDestinationAndIsSystem(String destination, Boolean isSystem) {
        LambdaQueryWrapper<TravelGuideTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelGuideTemplate::getDestination, destination)
               .eq(TravelGuideTemplate::getIsSystem, isSystem)
               .orderByAsc(TravelGuideTemplate::getCreatedTime);
        return selectList(wrapper);
    }
    
    /**
     * 根据模板名称检查是否存在
     * 
     * @param templateName 模板名称
     * @return 是否存在
     */
    default boolean existsByTemplateName(String templateName) {
        LambdaQueryWrapper<TravelGuideTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelGuideTemplate::getTemplateName, templateName);
        return selectCount(wrapper) > 0;
    }
    
    /**
     * 根据目的地统计模板数量
     * 
     * @param destination 目的地
     * @return 模板数量
     */
    default Long countByDestination(String destination) {
        LambdaQueryWrapper<TravelGuideTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelGuideTemplate::getDestination, destination);
        return selectCount(wrapper);
    }
    
    /**
     * 根据是否系统模板统计数量
     * 
     * @param isSystem 是否系统模板
     * @return 模板数量
     */
    default Long countByIsSystem(Boolean isSystem) {
        LambdaQueryWrapper<TravelGuideTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelGuideTemplate::getIsSystem, isSystem);
        return selectCount(wrapper);
    }
}
