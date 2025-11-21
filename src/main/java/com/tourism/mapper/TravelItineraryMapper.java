package com.tourism.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tourism.entity.TravelItinerary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 旅游行程Mapper接口
 * 提供行程相关的数据库操作
 * 继承BaseMapper获得基础的CRUD操作
 * 
 * @author tourism
 * @since 2025-01-27
 */
@Mapper
public interface TravelItineraryMapper extends BaseMapper<TravelItinerary> {
    
    /**
     * 根据用户ID和日期查询行程
     * 使用MyBatis Plus的LambdaQueryWrapper进行查询
     * 
     * @param userId 用户ID
     * @param travelDate 旅行日期
     * @return 行程信息
     */
    default TravelItinerary findByUserIdAndDate(String userId, LocalDate travelDate) {
        LambdaQueryWrapper<TravelItinerary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelItinerary::getUserId, userId)
               .eq(TravelItinerary::getTravelDate, travelDate);
        return selectOne(wrapper);
    }
    
    /**
     * 根据用户ID和天数查询行程
     * 
     * @param userId 用户ID
     * @param dayNumber 第几天
     * @return 行程信息
     */
    default TravelItinerary findByUserIdAndDayNumber(String userId, Integer dayNumber) {
        LambdaQueryWrapper<TravelItinerary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelItinerary::getUserId, userId)
               .eq(TravelItinerary::getDayNumber, dayNumber);
        return selectOne(wrapper);
    }
    
    /**
     * 根据用户ID查询所有行程
     * 
     * @param userId 用户ID
     * @return 行程列表
     */
    default List<TravelItinerary> findByUserId(String userId) {
        LambdaQueryWrapper<TravelItinerary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelItinerary::getUserId, userId)
               .orderByAsc(TravelItinerary::getTravelDate);
        return selectList(wrapper);
    }
    
    /**
     * 根据用户ID查询行程数量
     * 
     * @param userId 用户ID
     * @return 行程数量
     */
    default Long countByUserId(String userId) {
        LambdaQueryWrapper<TravelItinerary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelItinerary::getUserId, userId);
        return selectCount(wrapper);
    }
    
    /**
     * 根据用户ID和日期范围查询行程
     * 
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 行程列表
     */
    default List<TravelItinerary> findByUserIdAndDateRange(String userId, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<TravelItinerary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelItinerary::getUserId, userId)
               .between(TravelItinerary::getTravelDate, startDate, endDate)
               .orderByAsc(TravelItinerary::getTravelDate);
        return selectList(wrapper);
    }
    
    /**
     * 检查用户是否存在指定日期的行程
     * 
     * @param userId 用户ID
     * @param travelDate 旅行日期
     * @return 是否存在
     */
    default boolean existsByUserIdAndDate(String userId, LocalDate travelDate) {
        LambdaQueryWrapper<TravelItinerary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelItinerary::getUserId, userId)
               .eq(TravelItinerary::getTravelDate, travelDate);
        return selectCount(wrapper) > 0;
    }
    
    /**
     * 更新行程照片URL
     * 
     * @param id 行程ID
     * @param photoUrls 照片URL列表（JSON格式）
     * @return 影响行数
     */
    int updatePhotoUrls(@Param("id") Long id, @Param("photoUrls") String photoUrls);
    
    /**
     * 添加照片URL到现有列表
     * 
     * @param id 行程ID
     * @param photoUrl 照片URL
     * @return 影响行数
     */
    int addPhotoUrl(@Param("id") Long id, @Param("photoUrl") String photoUrl);
    
    /**
     * 删除照片URL
     * 
     * @param id 行程ID
     * @param photoUrls 更新后的照片URL列表（JSON格式）
     * @return 影响行数
     */
    int removePhotoUrl(@Param("id") Long id, @Param("photoUrls") String photoUrls);
    
    /**
     * 清空所有照片URL
     * 
     * @param id 行程ID
     * @return 影响行数
     */
    int clearPhotoUrls(@Param("id") Long id);
}
