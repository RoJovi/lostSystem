package com.jovi.service.impl;

import com.jovi.mapper.FoundItemMapper;
import com.jovi.mapper.LocationMapper;
import com.jovi.mapper.PostMapper;
import com.jovi.mapper.UserMapper;
import com.jovi.pojo.*;
import com.jovi.service.FoundItemService;
import com.jovi.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class FoundItemServicerImpl implements FoundItemService {

    @Autowired
    private LocationMapper locationMapper;

    @Autowired
    private FoundItemMapper foundItemMapper;

    @Autowired
    private UserMapper userMapper;

    //拾物帖详情
    @Override
    public FoundDetailVO getDetail(Integer id) {

        // 1. 查询拾物信息
        FoundItem item = foundItemMapper.selectById(id);
        if (item == null) {
            return null;
        }

        // 2. 查询发布者信息
        User user = userMapper.selectById(item.getUserId());

        // 3. 组装返回数据
        FoundDetailVO vo = new FoundDetailVO();
        vo.setId(item.getId());
        vo.setType(1);  // 拾物类型固定为1
        vo.setTitle(item.getTitle());
        vo.setDescription(item.getDescription());
        vo.setLocationId(item.getLocationId());
        vo.setLocationName(item.getLocationName());
        vo.setFoundTime(item.getFoundTime());
        vo.setImageUrl(item.getImageUrl());
        vo.setStatus(item.getStatus());
        vo.setViewCount(item.getViewCount());
        vo.setUserId(item.getUserId());
        vo.setUserNickname(user != null ? user.getNickname() : "无名氏");
        vo.setUserAvatar(user != null ? user.getAvatar() : "/default-avatar.png");

        // 4. 增加浏览次数
        foundItemMapper.incrementViewCount(id);

        return vo;
    }

    //发帖子
    @Override
    public FoundItem submitFound(Integer userId, FoundDTO foundDTO) {
        // 获取地点名称
        Location location = locationMapper.selectById(foundDTO.getLocationId());
        String locationName = location != null ? location.getName() : "地球";

        FoundItem item = new FoundItem();
        item.setUserId(userId);
        item.setTitle(foundDTO.getTitle());
        item.setLocationId(foundDTO.getLocationId());
        item.setLocationName(locationName);
        item.setFoundTime(foundDTO.getTime());
        item.setDescription(foundDTO.getDescription());
        item.setImageUrl(foundDTO.getImageUrl());

        foundItemMapper.insert(item);

        userMapper.incrementPostCount(userId);

        log.info("用户 {} 发布了失物帖子，ID: {}", userId, item.getId());

        return item;
    }


}
