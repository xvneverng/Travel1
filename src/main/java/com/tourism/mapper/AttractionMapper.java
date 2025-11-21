package com.tourism.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tourism.entity.Attraction;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 景点信息Mapper接口
 * 提供景点相关的数据库操作
 * 继承BaseMapper获得基础的CRUD操作
 * 
 * @author tourism
 * @since 2025-01-27
 */
@Mapper
public interface AttractionMapper extends BaseMapper<Attraction> {
    
    /**
     * 根据景点名称查询景点信息
     * 
     * @param name 景点名称
     * @return 景点信息
     */
    default Attraction findByName(String name) {
        LambdaQueryWrapper<Attraction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Attraction::getName, name);
        return selectOne(wrapper);
    }
    
    /**
     * 根据景点类别查询景点列表
     * 注意：数据库表中暂无category字段，暂时注释此方法
     * 
     * @param category 景点类别
     * @return 景点列表
     */
    /*
    default List<Attraction> findByCategory(String category) {
        LambdaQueryWrapper<Attraction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Attraction::getCategory, category)
               .orderByAsc(Attraction::getName);
        return selectList(wrapper);
    }
    */
    
    /**
     * 根据景点名称模糊查询
     * 
     * @param name 景点名称关键字
     * @return 景点列表
     */
    default List<Attraction> findByNameLike(String name) {
        LambdaQueryWrapper<Attraction> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Attraction::getName, name)
               .orderByAsc(Attraction::getName);
        return selectList(wrapper);
    }
    
    /**
     * 查询所有景点
     * 
     * @return 景点列表
     */
    default List<Attraction> findAll() {
        LambdaQueryWrapper<Attraction> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Attraction::getName);
        return selectList(wrapper);
    }
    
    /**
     * 根据景点名称列表查询景点
     * 
     * @param names 景点名称列表
     * @return 景点列表
     */
    default List<Attraction> findByNames(List<String> names) {
        LambdaQueryWrapper<Attraction> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Attraction::getName, names)
               .orderByAsc(Attraction::getName);
        return selectList(wrapper);
    }
    
    /**
     * 检查景点名称是否存在
     * 
     * @param name 景点名称
     * @return 是否存在
     */
    default boolean existsByName(String name) {
        LambdaQueryWrapper<Attraction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Attraction::getName, name);
        return selectCount(wrapper) > 0;
    }
    
    /**
     * 根据地址模糊查询景点
     * 
     * @param address 地址关键字
     * @return 景点列表
     */
    default List<Attraction> findByAddressLike(String address) {
        LambdaQueryWrapper<Attraction> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Attraction::getAddress, address)
               .orderByAsc(Attraction::getName);
        return selectList(wrapper);
    }
}
