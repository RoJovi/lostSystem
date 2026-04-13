package com.jovi.service.impl;

import com.jovi.mapper.FoundItemMapper;
import com.jovi.mapper.LocationMapper;
import com.jovi.mapper.LostItemMapper;
import com.jovi.mapper.UserMapper;
import com.jovi.pojo.*;
import com.jovi.service.FoundItemService;
import com.jovi.service.LostItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LostItemServicerImpl implements LostItemService {

    @Autowired
    private LocationMapper locationMapper;

    @Autowired
    private LostItemMapper lostItemMapper;

    @Autowired
    private UserMapper userMapper;

    // 发布失物
    @Override
    public LostItem submitLost(Integer userId, LostDTO lostDTO) {
        // 获取地点名称
        Location location = locationMapper.selectById(lostDTO.getLocationId());
        String locationName = location != null ? location.getName() : "未知地点";

        LostItem item = new LostItem();
        item.setUserId(userId);
        item.setTitle(lostDTO.getTitle());
        item.setLocationId(lostDTO.getLocationId());
        item.setLocationName(locationName);
        item.setLostTime(lostDTO.getTime());
        item.setDescription(lostDTO.getDescription());
        item.setImageUrl(lostDTO.getImageUrl());
        item.setStatus(0);      // 进行中
        item.setViewCount(0);   // 初始浏览0
        item.setIsTop(0);       // 未置顶

        lostItemMapper.insert(item);

        log.info("用户 {} 发布了失物帖子，ID: {}", userId, item.getId());

        return item;
    }

    // 获取失物详情
    @Override
    public LostDetailVO getDetail(Integer id) {
        // 1. 查询失物信息
        LostItem item = lostItemMapper.selectById(id);
        if (item == null) {
            return null;
        }

        // 2. 查询发布者信息
        User user = userMapper.selectById(item.getUserId());

        // 3. 组装返回数据
        LostDetailVO vo = new LostDetailVO();
        vo.setId(item.getId());
        vo.setType(0);  // 失物类型固定为0
        vo.setTitle(item.getTitle());
        vo.setDescription(item.getDescription());
        vo.setLocationId(item.getLocationId());
        vo.setLocationName(item.getLocationName());
        vo.setLostTime(item.getLostTime());
        vo.setImageUrl(item.getImageUrl());
        vo.setStatus(item.getStatus());
        vo.setViewCount(item.getViewCount());
        vo.setUserId(item.getUserId());
        vo.setUserNickname(user != null ? user.getNickname() : "无名氏");
        vo.setUserAvatar(user != null ? user.getAvatar() : "/default-avatar.png");

        // 4. 增加浏览次数
        lostItemMapper.incrementViewCount(id);

        return vo;
    }
}
