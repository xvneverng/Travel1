package com.tourism.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tourism.entity.TravelGuide;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 路书信息Mapper接口
 * 提供路书相关的数据库操作
 * 继承BaseMapper获得基础的CRUD操作
 * 
 * @author tourism
 * @since 2025-01-27
 */
@Mapper
public interface TravelGuideMapper extends BaseMapper<TravelGuide> {
    
    /**
     * 根据用户ID查询路书列表
     * 
     * @param userId 用户ID
     * @return 路书列表
     */
    default List<TravelGuide> findByUserId(String userId) {
        LambdaQueryWrapper<TravelGuide> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelGuide::getUserId, userId)
               .orderByDesc(TravelGuide::getCreatedTime);
        return selectList(wrapper);
    }
    
    /**
     * 根据路书名称查询路书
     * 
     * @param guideName 路书名称
     * @return 路书信息
     */
    default TravelGuide findByGuideName(String guideName) {
        LambdaQueryWrapper<TravelGuide> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelGuide::getGuideName, guideName);
        return selectOne(wrapper);
    }
    
    /**
     * 根据目的地查询路书列表
     * 
     * @param destination 目的地
     * @return 路书列表
     */
    default List<TravelGuide> findByDestination(String destination) {
        LambdaQueryWrapper<TravelGuide> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelGuide::getDestination, destination)
               .orderByDesc(TravelGuide::getCreatedTime);
        return selectList(wrapper);
    }
    
    /**
     * 根据状态查询路书列表
     * 
     * @param status 路书状态
     * @return 路书列表
     */
    default List<TravelGuide> findByStatus(String status) {
        LambdaQueryWrapper<TravelGuide> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelGuide::getStatus, status)
               .orderByDesc(TravelGuide::getCreatedTime);
        return selectList(wrapper);
    }
    
    /**
     * 查询公开的路书列表
     * 
     * @return 公开路书列表
     */
    default List<TravelGuide> findPublicGuides() {
        LambdaQueryWrapper<TravelGuide> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelGuide::getIsPublic, true)
               .eq(TravelGuide::getStatus, "published")
               .orderByDesc(TravelGuide::getCreatedTime);
        return selectList(wrapper);
    }
    
    /**
     * 根据用户ID和路书名称查询路书
     * 
     * @param userId 用户ID
     * @param guideName 路书名称
     * @return 路书信息
     */
    default TravelGuide findByUserIdAndGuideName(String userId, String guideName) {
        LambdaQueryWrapper<TravelGuide> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelGuide::getUserId, userId)
               .eq(TravelGuide::getGuideName, guideName);
        return selectOne(wrapper);
    }
    
    /**
     * 检查路书名称是否存在
     * 
     * @param guideName 路书名称
     * @return 是否存在
     */
    default boolean existsByGuideName(String guideName) {
        LambdaQueryWrapper<TravelGuide> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelGuide::getGuideName, guideName);
        return selectCount(wrapper) > 0;
    }
    
    /**
     * 根据关键词搜索路书
     * 
     * @param keyword 关键词
     * @return 路书列表
     */
    default List<TravelGuide> searchByKeyword(String keyword) {
        LambdaQueryWrapper<TravelGuide> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(TravelGuide::getGuideName, keyword)
               .or()
               .like(TravelGuide::getDestination, keyword)
               .or()
               .like(TravelGuide::getDescription, keyword)
               .orderByDesc(TravelGuide::getCreatedTime);
        return selectList(wrapper);
    }
    
    /**
     * 查询所有路书
     * 
     * @return 所有路书列表
     */
    default List<TravelGuide> findAll() {
        LambdaQueryWrapper<TravelGuide> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(TravelGuide::getCreatedTime);
        return selectList(wrapper);
    }
}
