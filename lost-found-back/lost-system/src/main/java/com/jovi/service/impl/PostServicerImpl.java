package com.jovi.service.impl;

import com.jovi.mapper.*;
import com.jovi.pojo.Location;
import com.jovi.pojo.Post;
import com.jovi.pojo.UpdatePostRequest;
import com.jovi.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class PostServicerImpl implements PostService {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private FoundItemMapper foundItemMapper;

    @Autowired
    private LostItemMapper lostItemMapper;

    @Autowired
    private LocationMapper locationMapper;

    //根据用户的id获得帖子
    @Override
    public List<Post> getAll(Integer userId) {
        return postMapper.SelectPostByUserId(userId);
    }

    @Override
    public boolean deletePost(String type, Integer id, Integer userId, Integer userType) {
        if ("lost".equalsIgnoreCase(type)) {
            Integer ownerId = lostItemMapper.selectUserIdById(id);
            if (ownerId == null) {
                log.warn("失物帖子不存在，id: {}", id);
                return false;
            }
            // 管理员或作者可以删除
            if (!ownerId.equals(userId) && userType != 1) {
                log.warn("用户 {} 无权删除帖子 {}", userId, id);
                return false;
            }
            lostItemMapper.deleteById(id);
            // 只有作者删除时才减少帖子计数（管理员删除不减少）
            if (ownerId.equals(userId)) {
                userMapper.decrementPostCount(userId);
            }
            log.info("删除失物帖子成功，id: {}", id);

        } else if ("found".equalsIgnoreCase(type)) {
            Integer ownerId = foundItemMapper.selectUserIdById(id);
            if (ownerId == null) {
                log.warn("拾物帖子不存在，id: {}", id);
                return false;
            }
            if (!ownerId.equals(userId) && userType != 1) {
                log.warn("用户 {} 无权删除帖子 {}", userId, id);
                return false;
            }
            foundItemMapper.deleteById(id);
            log.info("删除拾物帖子成功，id: {}", id);

        } else {
            log.warn("无效的帖子类型: {}", type);
            return false;
        }

        return true;
    }

    @Override
    @Transactional
    public boolean updatePost(String type, Integer id, UpdatePostRequest request, Integer userId) {
        // 获取地点名称
        Location location = locationMapper.selectById(request.getLocationId());
        String locationName = location != null ? location.getName() : null;

        if ("lost".equalsIgnoreCase(type)) {
            // 检查权限
            Integer ownerId = lostItemMapper.selectUserIdById(id);
            if (ownerId == null || !ownerId.equals(userId)) {
                return false;
            }
            // 更新失物帖子
            lostItemMapper.updatePost(id, request.getTitle(), request.getLocationId(),
                    locationName, request.getTime(), request.getDescription(),request.getImageUrl());
            log.info("更新失物帖子成功，id: {}", id);

        } else if ("found".equalsIgnoreCase(type)) {
            // 检查权限
            Integer ownerId = foundItemMapper.selectUserIdById(id);
            if (ownerId == null || !ownerId.equals(userId)) {
                return false;
            }
            // 更新拾物帖子
            foundItemMapper.updatePost(id, request.getTitle(), request.getLocationId(),
                    locationName, request.getTime(), request.getDescription(),request.getImageUrl());
            log.info("更新拾物帖子成功，id: {}", id);

        } else {
            return false;
        }

        return true;
    }

    @Override
    @Transactional
    public boolean setResolved(String type, Integer id, Integer userId) {
        if ("lost".equalsIgnoreCase(type)) {
            // 检查权限
            Integer ownerId = lostItemMapper.selectUserIdById(id);
            if (ownerId == null || !ownerId.equals(userId)) {
                return false;
            }
            // 设置为已找回（status = 1）
            lostItemMapper.setResolved(id);
            log.info("失物帖子已找回，id: {}", id);

        } else if ("found".equalsIgnoreCase(type)) {
            // 检查权限
            Integer ownerId = foundItemMapper.selectUserIdById(id);
            if (ownerId == null || !ownerId.equals(userId)) {
                return false;
            }
            // 设置为已归还（status = 1）
            foundItemMapper.setResolved(id);
            log.info("拾物帖子已归还，id: {}", id);

        } else {
            return false;
        }

        return true;
    }
}
