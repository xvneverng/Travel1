package com.tourism.service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tourism.dto.TravelItineraryRequest;
import com.tourism.dto.TravelItineraryResponse;
import com.tourism.entity.Attraction;
import com.tourism.entity.ItineraryAttraction;
import com.tourism.entity.TravelItinerary;
import com.tourism.mapper.AttractionMapper;
import com.tourism.mapper.ItineraryAttractionMapper;
import com.tourism.mapper.TravelItineraryMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 旅游行程服务类
 * 处理行程相关的业务逻辑
 * 继承ServiceImpl获得MyBatis Plus的基础服务功能
 * 
 * @author tourism
 * @since 2025-01-27
 */
@Slf4j
@Service
public class TravelItineraryService extends ServiceImpl<TravelItineraryMapper, TravelItinerary> {
    
    @Autowired
    private AttractionMapper attractionMapper;
    
    @Autowired
    private ItineraryAttractionMapper itineraryAttractionMapper;
    
    /**
     * 保存或更新行程
     * 
     * @param request 行程请求数据
     * @return 保存后的行程响应数据
     */
    @Transactional(rollbackFor = Exception.class)
    public TravelItineraryResponse saveOrUpdateItinerary(@Valid TravelItineraryRequest request) {
        // 1. 检查是否已存在相同用户、日期和天数的行程
        TravelItinerary existingItinerary = baseMapper.findByUserIdAndDate(
                request.getUserId(), request.getTravelDate());
        
        TravelItinerary itinerary;
        if (existingItinerary != null) {
            // 更新现有行程
            itinerary = existingItinerary;
            // 手动更新所有字段，确保travelFeelings等字段正确更新
            itinerary.setUserId(request.getUserId());
            itinerary.setTravelDate(request.getTravelDate());
            itinerary.setDayNumber(request.getDayNumber());
            itinerary.setWeatherCondition(request.getWeatherCondition());
            itinerary.setWeatherIcon(request.getWeatherIcon());
            itinerary.setTemperatureMin(request.getTemperatureMin());
            itinerary.setTemperatureMax(request.getTemperatureMax());
            // 注意：不更新notes字段，保持数据库中的原始值
            // itinerary.setNotes(request.getNotes()); // 注释掉，不保存注意事项到数据库
            itinerary.setTravelFeelings(request.getTravelFeelings());
        } else {
            // 创建新行程
            itinerary = new TravelItinerary();
            BeanUtils.copyProperties(request, itinerary);
            // 清空notes字段，不保存注意事项到数据库
            itinerary.setNotes(null);
        }
        
        // 2. 保存行程基本信息
        if (itinerary.getId() == null) {
            save(itinerary);
            log.info("创建新行程成功，ID: {}", itinerary.getId());
        } else {
            updateById(itinerary);
            log.info("更新行程成功，ID: {}, 旅行感受: {}", itinerary.getId(), itinerary.getTravelFeelings());
        }
        
        // 3. 处理景点信息
        if (!CollectionUtils.isEmpty(request.getAttractions())) {
            // 删除现有的景点关联
            itineraryAttractionMapper.deleteByItineraryId(itinerary.getId());
            
            // 批量保存新的景点关联
            List<ItineraryAttraction> itineraryAttractions = new ArrayList<>();
            for (int i = 0; i < request.getAttractions().size(); i++) {
                TravelItineraryRequest.AttractionRequest attractionRequest = request.getAttractions().get(i);
                
                // 保存或更新景点信息
                Attraction attraction = saveOrUpdateAttraction(attractionRequest);
                
                // 创建行程景点关联
                ItineraryAttraction itineraryAttraction = new ItineraryAttraction();
                itineraryAttraction.setItineraryId(itinerary.getId());
                itineraryAttraction.setAttractionId(attraction.getId());
                
                // 设置景点顺序，如果前端没有提供则使用索引+1
                Integer sequenceOrder = attractionRequest.getSequenceOrder();
                if (sequenceOrder == null || sequenceOrder <= 0) {
                    sequenceOrder = i + 1; // 从1开始
                }
                itineraryAttraction.setSequenceOrder(sequenceOrder);
                
                itineraryAttraction.setVisitDuration(attractionRequest.getVisitDuration());
                itineraryAttraction.setVisitNotes(attractionRequest.getVisitNotes());
                itineraryAttraction.setTransportationFromPrevious(attractionRequest.getTransportationFromPrevious());
                
                itineraryAttractions.add(itineraryAttraction);
            }
            
            // 批量插入景点关联
            if (!itineraryAttractions.isEmpty()) {
                itineraryAttractionMapper.insertBatch(itineraryAttractions);
            }
        }
        
        // 4. 返回完整的行程响应数据
        return getItineraryById(itinerary.getId());
    }
    
    /**
     * 根据ID获取行程详情
     * 
     * @param id 行程ID
     * @return 行程响应数据
     */
    public TravelItineraryResponse getItineraryById(Long id) {
        TravelItinerary itinerary = getById(id);
        if (itinerary == null) {
            return null;
        }
        
        return convertToResponse(itinerary);
    }
    
    /**
     * 根据用户ID和日期获取行程
     * 
     * @param userId 用户ID
     * @param travelDate 旅行日期
     * @return 行程响应数据
     */
    public TravelItineraryResponse getItineraryByUserIdAndDate(String userId, LocalDate travelDate) {
        TravelItinerary itinerary = baseMapper.findByUserIdAndDate(userId, travelDate);
        if (itinerary == null) {
            return null;
        }
        
        return convertToResponse(itinerary);
    }
    
    /**
     * 根据用户ID和天数获取行程
     * 
     * @param userId 用户ID
     * @param dayNumber 第几天
     * @return 行程响应数据
     */
    public TravelItineraryResponse getItineraryByUserIdAndDayNumber(String userId, Integer dayNumber) {
        TravelItinerary itinerary = baseMapper.findByUserIdAndDayNumber(userId, dayNumber);
        if (itinerary == null) {
            return null;
        }
        
        return convertToResponse(itinerary);
    }
    
    /**
     * 根据用户ID获取所有行程
     * 
     * @param userId 用户ID
     * @return 行程列表
     */
    public List<TravelItineraryResponse> getItinerariesByUserId(String userId) {
        List<TravelItinerary> itineraries = baseMapper.findByUserId(userId);
        return itineraries.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据用户ID和日期范围获取行程
     * 
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 行程列表
     */
    public List<TravelItineraryResponse> getItinerariesByUserIdAndDateRange(String userId, LocalDate startDate, LocalDate endDate) {
        List<TravelItinerary> itineraries = baseMapper.findByUserIdAndDateRange(userId, startDate, endDate);
        return itineraries.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 删除行程
     * 
     * @param id 行程ID
     * @return 是否删除成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteItinerary(Long id) {
        // 删除景点关联（外键约束会自动处理）
        itineraryAttractionMapper.deleteByItineraryId(id);
        
        // 删除行程
        return removeById(id);
    }
    
    /**
     * 批量删除行程
     * 
     * @param ids 行程ID列表
     * @return 是否删除成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteItineraries(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return false;
        }
        
        // 批量删除景点关联
        for (Long id : ids) {
            itineraryAttractionMapper.deleteByItineraryId(id);
        }
        
        // 批量删除行程
        return removeByIds(ids);
    }
    
    /**
     * 保存或更新景点信息
     * 
     * @param attractionRequest 景点请求数据
     * @return 景点实体
     */
    private Attraction saveOrUpdateAttraction(TravelItineraryRequest.AttractionRequest attractionRequest) {
        // 检查景点是否已存在
        Attraction existingAttraction = attractionMapper.findByName(attractionRequest.getName());
        
        Attraction attraction;
        if (existingAttraction != null) {
            // 更新现有景点
            attraction = existingAttraction;
            attraction.setDuration(attractionRequest.getDuration());
            
            // 如果现有景点缺少描述等属性，则自动生成
            if (isAttractionAttributesMissing(attraction)) {
                log.info("景点属性不完整，自动生成: {}", attraction.getName());
                generateAttractionAttributes(attraction);
                attractionMapper.updateById(attraction);
                log.info("景点属性已更新: {} - {}", attraction.getName(), attraction.getDescription());
            } else {
                attractionMapper.updateById(attraction);
            }
        } else {
            // 创建新景点，自动生成各种属性
            attraction = new Attraction();
            attraction.setName(attractionRequest.getName());
            attraction.setDuration(attractionRequest.getDuration());
            
            // 自动生成景点属性
            generateAttractionAttributes(attraction);
            
            attractionMapper.insert(attraction);
            log.info("自动生成景点属性成功: {} - {}", attraction.getName(), attraction.getDescription());
        }
        
        return attraction;
    }
    
    /**
     * 检查景点属性是否缺失
     * 
     * @param attraction 景点对象
     * @return 是否缺失属性
     */
    private boolean isAttractionAttributesMissing(Attraction attraction) {
        return attraction.getDescription() == null || attraction.getDescription().trim().isEmpty() ||
               attraction.getIcon() == null || attraction.getIcon().trim().isEmpty() ||
               attraction.getTags() == null || attraction.getTags().trim().isEmpty() ||
               attraction.getTips() == null || attraction.getTips().trim().isEmpty() ||
               attraction.getTransportationInfo() == null || attraction.getTransportationInfo().trim().isEmpty() ||
               attraction.getAddress() == null || attraction.getAddress().trim().isEmpty();
    }
    
    /**
     * 自动生成景点属性
     * 根据景点名称生成描述、图标、标签、提示等信息
     * 
     * @param attraction 景点对象
     */
    private void generateAttractionAttributes(Attraction attraction) {
        String name = attraction.getName();
        
        // 根据景点名称生成描述
        attraction.setDescription(generateDescription(name));
        
        // 根据景点类型生成图标
        attraction.setIcon(generateIcon(name));
        
        // 生成标签
        attraction.setTags(generateTags(name));
        
        // 生成游览提示
        attraction.setTips(generateTips(name));
        
        // 生成交通信息
        attraction.setTransportationInfo(generateTransportationInfo(name));
        
        // 生成地址（基于景点名称推测）
        attraction.setAddress(generateAddress(name));
    }
    
    /**
     * 生成景点描述
     */
    private String generateDescription(String name) {
        // 根据景点名称生成描述
        if (name.contains("站") || name.contains("火车站") || name.contains("高铁站")) {
            return name + "是重要的交通枢纽，连接着城市与城市之间的重要纽带。车站设施完善，服务周到，为旅客提供便捷的出行体验。";
        } else if (name.contains("陵") || name.contains("墓")) {
            return name + "是重要的历史文化遗产，承载着深厚的历史文化内涵。这里环境庄严肃穆，是缅怀历史、感受文化的重要场所。";
        } else if (name.contains("庙") || name.contains("寺") || name.contains("观")) {
            return name + "是著名的宗教文化场所，建筑精美，香火鼎盛。这里不仅是信仰的圣地，也是了解传统文化的重要窗口。";
        } else if (name.contains("博物馆") || name.contains("院")) {
            return name + "是重要的文化教育场所，收藏着丰富的文物珍品。博物馆建筑典雅，展陈精美，是学习历史文化的好去处。";
        } else if (name.contains("公园") || name.contains("园")) {
            return name + "是城市中的绿色明珠，环境优美，空气清新。这里适合休闲散步，是市民放松身心的理想场所。";
        } else if (name.contains("山") || name.contains("峰")) {
            return name + "是著名的自然景观，山势雄伟，风景秀丽。登高望远，可以俯瞰城市美景，是登山爱好者的好去处。";
        } else if (name.contains("湖") || name.contains("海") || name.contains("江")) {
            return name + "是美丽的自然水域，波光粼粼，景色宜人。这里适合休闲娱乐，是亲近自然的好地方。";
        } else {
            return name + "是当地著名的旅游景点，具有独特的魅力和特色。这里风景优美，文化底蕴深厚，是值得一游的好地方。";
        }
    }
    
    /**
     * 生成景点图标
     */
    private String generateIcon(String name) {
        if (name.contains("站") || name.contains("火车站") || name.contains("高铁站")) {
            return "🚄";
        } else if (name.contains("陵") || name.contains("墓")) {
            return "🏛️";
        } else if (name.contains("庙") || name.contains("寺") || name.contains("观")) {
            return "🏮";
        } else if (name.contains("博物馆") || name.contains("院")) {
            return "🏛️";
        } else if (name.contains("公园") || name.contains("园")) {
            return "🌳";
        } else if (name.contains("山") || name.contains("峰")) {
            return "⛰️";
        } else if (name.contains("湖") || name.contains("海") || name.contains("江")) {
            return "🌊";
        } else if (name.contains("塔") || name.contains("楼")) {
            return "🗼";
        } else if (name.contains("桥")) {
            return "🌉";
        } else {
            return "📍";
        }
    }
    
    /**
     * 生成景点标签
     */
    private String generateTags(String name) {
        List<String> tags = new ArrayList<>();
        
        if (name.contains("站") || name.contains("火车站") || name.contains("高铁站")) {
            tags.add("交通设施");
            tags.add("火车站");
        } else if (name.contains("陵") || name.contains("墓")) {
            tags.add("历史遗迹");
            tags.add("文化景点");
        } else if (name.contains("庙") || name.contains("寺") || name.contains("观")) {
            tags.add("宗教文化");
            tags.add("古建筑");
        } else if (name.contains("博物馆") || name.contains("院")) {
            tags.add("文化教育");
            tags.add("博物馆");
        } else if (name.contains("公园") || name.contains("园")) {
            tags.add("自然景观");
            tags.add("休闲娱乐");
        } else if (name.contains("山") || name.contains("峰")) {
            tags.add("自然景观");
            tags.add("登山");
        } else if (name.contains("湖") || name.contains("海") || name.contains("江")) {
            tags.add("自然景观");
            tags.add("水域");
        } else {
            tags.add("旅游景点");
            tags.add("观光");
        }
        
        return String.join(",", tags);
    }
    
    /**
     * 生成游览提示
     */
    private String generateTips(String name) {
        if (name.contains("站") || name.contains("火车站") || name.contains("高铁站")) {
            return "建议提前30分钟到达，注意车次信息，保管好车票和身份证件。";
        } else if (name.contains("陵") || name.contains("墓")) {
            return "参观时请保持安静，尊重历史，注意文物保护，穿舒适的鞋子。";
        } else if (name.contains("庙") || name.contains("寺") || name.contains("观")) {
            return "进入时请保持肃静，遵守宗教礼仪，不要大声喧哗，可以准备一些香火。";
        } else if (name.contains("博物馆") || name.contains("院")) {
            return "免费参观，需提前预约，周一闭馆，请勿触摸展品，保持安静。";
        } else if (name.contains("公园") || name.contains("园")) {
            return "适合休闲散步，注意保护环境，不要乱扔垃圾，可以准备一些食物和水。";
        } else if (name.contains("山") || name.contains("峰")) {
            return "登山时注意安全，穿合适的登山鞋，带足水和食物，注意天气变化。";
        } else if (name.contains("湖") || name.contains("海") || name.contains("江")) {
            return "注意安全，不要独自下水，可以准备防晒用品，注意保护环境。";
        } else {
            return "游览时注意安全，保护环境，可以准备相机记录美好时光。";
        }
    }
    
    /**
     * 生成交通信息
     */
    private String generateTransportationInfo(String name) {
        if (name.contains("站") || name.contains("火车站") || name.contains("高铁站")) {
            return "可乘坐地铁、公交或出租车到达，建议提前查询路线和班次。";
        } else if (name.contains("陵") || name.contains("墓")) {
            return "可乘坐地铁、公交或出租车前往，建议提前查询开放时间。";
        } else if (name.contains("庙") || name.contains("寺") || name.contains("观")) {
            return "可乘坐地铁、公交或出租车前往，建议提前查询开放时间。";
        } else if (name.contains("博物馆") || name.contains("院")) {
            return "可乘坐地铁、公交或出租车前往，建议提前预约参观。";
        } else if (name.contains("公园") || name.contains("园")) {
            return "可乘坐地铁、公交或出租车前往，建议选择天气好的时候游览。";
        } else if (name.contains("山") || name.contains("峰")) {
            return "可乘坐地铁、公交或出租车前往，建议选择天气好的时候登山。";
        } else if (name.contains("湖") || name.contains("海") || name.contains("江")) {
            return "可乘坐地铁、公交或出租车前往，建议选择天气好的时候游览。";
        } else {
            return "可乘坐地铁、公交或出租车前往，建议提前查询路线和开放时间。";
        }
    }
    
    /**
     * 生成地址信息
     */
    private String generateAddress(String name) {
        // 基于景点名称推测地址
        if (name.contains("南京")) {
            return "江苏省南京市";
        } else if (name.contains("北京")) {
            return "北京市";
        } else if (name.contains("上海")) {
            return "上海市";
        } else if (name.contains("杭州")) {
            return "浙江省杭州市";
        } else if (name.contains("苏州")) {
            return "江苏省苏州市";
        } else {
            return "具体地址请查询相关信息";
        }
    }
    
    /**
     * 保存或更新旅游感受
     * 将旅游感受保存到travel_itinerary表的travel_feelings字段中
     * 
     * @param userId 用户ID
     * @param dayNumber 天数
     * @param content 感受内容
     * @return 保存后的行程响应数据
     */
    @Transactional(rollbackFor = Exception.class)
    public TravelItineraryResponse saveOrUpdateTravelFeelings(String userId, Integer dayNumber, String content) {
        // 查找或创建对应的行程记录
        TravelItinerary itinerary = baseMapper.findByUserIdAndDayNumber(userId, dayNumber);
        
        if (itinerary == null) {
            // 如果不存在行程记录，创建一个新的
            itinerary = new TravelItinerary();
            itinerary.setUserId(userId);
            itinerary.setDayNumber(dayNumber);
            itinerary.setTravelDate(LocalDate.now()); // 使用当前日期作为默认值
            itinerary.setTravelFeelings(content);
            save(itinerary);
        } else {
            // 更新现有行程的旅游感受
            LambdaUpdateWrapper<TravelItinerary> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(TravelItinerary::getId, itinerary.getId())
                        .set(TravelItinerary::getTravelFeelings, content);
            update(updateWrapper);
        }
        
        return getItineraryById(itinerary.getId());
    }
    
    /**
     * 获取旅游感受
     * 从travel_itinerary表的travel_feelings字段中获取感受内容
     * 
     * @param userId 用户ID
     * @param dayNumber 天数
     * @return 旅游感受内容，如果不存在则返回null
     */
    public String getTravelFeelings(String userId, Integer dayNumber) {
        TravelItinerary itinerary = baseMapper.findByUserIdAndDayNumber(userId, dayNumber);
        return itinerary != null ? itinerary.getTravelFeelings() : null;
    }
    
    /**
     * 删除旅游感受
     * 将travel_itinerary表的travel_feelings字段设置为null
     * 
     * @param userId 用户ID
     * @param dayNumber 天数
     * @return 是否删除成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTravelFeelings(String userId, Integer dayNumber) {
        TravelItinerary itinerary = baseMapper.findByUserIdAndDayNumber(userId, dayNumber);
        if (itinerary == null) {
            return false;
        }
        
        LambdaUpdateWrapper<TravelItinerary> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(TravelItinerary::getId, itinerary.getId())
                    .set(TravelItinerary::getTravelFeelings, null);
        
        return update(updateWrapper);
    }
    
    /**
     * 检查旅游感受是否存在
     * 
     * @param userId 用户ID
     * @param dayNumber 天数
     * @return 是否存在
     */
    public boolean existsTravelFeelings(String userId, Integer dayNumber) {
        String feelings = getTravelFeelings(userId, dayNumber);
        return feelings != null && !feelings.trim().isEmpty();
    }
    
    /**
     * 更新注意事项（不保存到数据库）
     * 只更新内存中的数据，用于临时编辑，不持久化
     * 
     * @param userId 用户ID
     * @param dayNumber 天数
     * @param notes 注意事项内容
     * @return 更新后的行程响应数据（仅内存中的临时数据）
     */
    public TravelItineraryResponse updateNotesOnly(String userId, Integer dayNumber, String notes) {
        // 查找对应的行程记录
        TravelItinerary itinerary = baseMapper.findByUserIdAndDayNumber(userId, dayNumber);
        
        if (itinerary == null) {
            // 如果不存在行程记录，创建一个临时的（不保存到数据库）
            itinerary = new TravelItinerary();
            itinerary.setUserId(userId);
            itinerary.setDayNumber(dayNumber);
            itinerary.setTravelDate(LocalDate.now()); // 使用当前日期作为默认值
            itinerary.setNotes(notes);
            // 注意：这里不调用save()方法，所以不会保存到数据库
        } else {
            // 更新现有行程的注意事项（仅内存中）
            itinerary.setNotes(notes);
            // 注意：这里不调用updateById()方法，所以不会保存到数据库
        }
        
        // 返回转换后的响应数据（基于内存中的数据）
        return convertToResponse(itinerary);
    }
    
    /**
     * 获取注意事项
     * 从数据库中获取注意事项内容
     * 
     * @param userId 用户ID
     * @param dayNumber 天数
     * @return 注意事项内容，如果不存在则返回null
     */
    public String getNotes(String userId, Integer dayNumber) {
        TravelItinerary itinerary = baseMapper.findByUserIdAndDayNumber(userId, dayNumber);
        return itinerary != null ? itinerary.getNotes() : null;
    }
    
    /**
     * 生成默认用户ID（基于设备信息）
     * 在实际应用中，这里应该根据用户登录信息或其他方式获取真实的用户ID
     * 
     * @return 默认用户ID
     */
    public String getDefaultUserId() {
        // 这里使用一个固定的默认用户ID
        // 在实际应用中，应该从用户会话、JWT token或其他方式获取
        return "default_user";
    }
    
    /**
     * 添加照片到行程
     * 
     * @param id 行程ID
     * @param photoUrl 照片URL
     * @return 是否添加成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean addPhoto(Long id, String photoUrl) {
        TravelItinerary itinerary = getById(id);
        if (itinerary == null) {
            return false;
        }
        
        // 获取当前照片列表
        List<String> currentPhotos = itinerary.getPhotoList();
        if (currentPhotos == null) {
            currentPhotos = new ArrayList<>();
        }
        
        // 添加新照片
        currentPhotos.add(photoUrl);
        
        // 将更新后的列表转换为JSON并保存
        String updatedPhotoUrls = convertPhotoListToJson(currentPhotos);
        int result = baseMapper.updatePhotoUrls(id, updatedPhotoUrls);
        return result > 0;
    }
    
    /**
     * 删除行程中的照片
     * 
     * @param id 行程ID
     * @param photoUrl 照片URL
     * @return 是否删除成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean removePhoto(Long id, String photoUrl) {
        TravelItinerary itinerary = getById(id);
        if (itinerary == null) {
            return false;
        }
        
        // 获取当前照片列表
        List<String> currentPhotos = itinerary.getPhotoList();
        if (currentPhotos == null || currentPhotos.isEmpty()) {
            return false;
        }
        
        // 从列表中移除指定的照片URL
        boolean removed = currentPhotos.remove(photoUrl);
        if (!removed) {
            return false;
        }
        
        // 将更新后的列表转换为JSON并保存
        String updatedPhotoUrls = convertPhotoListToJson(currentPhotos);
        int result = baseMapper.removePhotoUrl(id, updatedPhotoUrls);
        return result > 0;
    }
    
    /**
     * 更新行程照片列表
     * 
     * @param id 行程ID
     * @param photoUrls 照片URL列表
     * @return 是否更新成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePhotos(Long id, List<String> photoUrls) {
        TravelItinerary itinerary = getById(id);
        if (itinerary == null) {
            return false;
        }
        
        // 将List转换为JSON字符串
        String photoUrlsJson = convertPhotoListToJson(photoUrls);
        int result = baseMapper.updatePhotoUrls(id, photoUrlsJson);
        return result > 0;
    }
    
    /**
     * 清空行程所有照片
     * 
     * @param id 行程ID
     * @return 是否清空成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean clearAllPhotos(Long id) {
        TravelItinerary itinerary = getById(id);
        if (itinerary == null) {
            return false;
        }
        
        int result = baseMapper.clearPhotoUrls(id);
        return result > 0;
    }
    
    /**
     * 获取行程照片列表
     * 
     * @param id 行程ID
     * @return 照片URL列表
     */
    public List<String> getPhotos(Long id) {
        TravelItinerary itinerary = getById(id);
        if (itinerary == null) {
            return new ArrayList<>();
        }
        
        return itinerary.getPhotoList();
    }
    
    /**
     * 将照片URL列表转换为JSON字符串
     * 
     * @param photoUrls 照片URL列表
     * @return JSON字符串
     */
    private String convertPhotoListToJson(List<String> photoUrls) {
        try {
            // 使用Jackson ObjectMapper进行正确的JSON序列化
            com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return objectMapper.writeValueAsString(photoUrls);
        } catch (Exception e) {
            // 如果JSON序列化失败，返回空数组
            return "[]";
        }
    }
    
    /**
     * 将行程实体转换为响应DTO
     * 
     * @param itinerary 行程实体
     * @return 行程响应DTO
     */
    private TravelItineraryResponse convertToResponse(TravelItinerary itinerary) {
        TravelItineraryResponse response = new TravelItineraryResponse();
        BeanUtils.copyProperties(itinerary, response);
        
        // 设置照片列表
        response.setPhotoList(itinerary.getPhotoList());
        
        // 获取景点列表
        List<ItineraryAttraction> itineraryAttractions = itineraryAttractionMapper
                .findByItineraryIdWithAttraction(itinerary.getId());
        
        List<TravelItineraryResponse.AttractionResponse> attractionResponses = new ArrayList<>();
        for (ItineraryAttraction itineraryAttraction : itineraryAttractions) {
            TravelItineraryResponse.AttractionResponse attractionResponse = new TravelItineraryResponse.AttractionResponse();
            
            // 复制景点基本信息
            if (itineraryAttraction.getAttraction() != null) {
                Attraction attraction = itineraryAttraction.getAttraction();
                
                // 手动设置所有属性，确保数据完整
                attractionResponse.setId(attraction.getId());
                attractionResponse.setName(attraction.getName());
                attractionResponse.setDescription(attraction.getDescription());
                attractionResponse.setIcon(attraction.getIcon());
                attractionResponse.setDuration(attraction.getDuration());
                attractionResponse.setTags(attraction.getTags());
                attractionResponse.setTips(attraction.getTips());
                attractionResponse.setTransportationInfo(attraction.getTransportationInfo());
                attractionResponse.setAddress(attraction.getAddress());
                
                log.debug("转换景点数据: {} - 描述: {}, 标签: {}, 提示: {}", 
                    attraction.getName(), attraction.getDescription(), 
                    attraction.getTags(), attraction.getTips());
            }
            
            // 复制关联信息
            attractionResponse.setSequenceOrder(itineraryAttraction.getSequenceOrder());
            attractionResponse.setVisitDuration(itineraryAttraction.getVisitDuration());
            attractionResponse.setVisitNotes(itineraryAttraction.getVisitNotes());
            attractionResponse.setTransportationFromPrevious(itineraryAttraction.getTransportationFromPrevious());
            
            attractionResponses.add(attractionResponse);
        }
        
        response.setAttractions(attractionResponses);
        return response;
    }
}
