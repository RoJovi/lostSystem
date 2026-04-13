package com.jovi.service.impl;

import com.jovi.mapper.TopRequestMapper;
import com.jovi.pojo.TopRequestDTO;
import com.jovi.pojo.TopRequest;
import com.jovi.service.TopRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class TopRequestServiceImpl implements TopRequestService {

    @Autowired
    private TopRequestMapper topRequestMapper;

    @Override
    @Transactional
    public boolean applyTop(Integer userId, TopRequestDTO requestDTO) {
        Integer itemId = requestDTO.getId();
        Integer itemType = requestDTO.getType();

        // 1. 检查帖子是否存在且状态为进行中
        boolean exists;
        if (itemType == 0) {
            exists = topRequestMapper.checkLostItemExists(itemId) > 0;
        } else {
            exists = topRequestMapper.checkFoundItemExists(itemId) > 0;
        }

        if (!exists) {
            log.warn("帖子不存在或已关闭，用户: {}, 帖子ID: {}", userId, itemId);
            return false;
        }

        // 2. 检查是否已有待审批的置顶申请
        int pendingCount = topRequestMapper.checkPendingRequest(itemId, itemType);
        if (pendingCount > 0) {
            log.warn("已有待审批的置顶申请，用户: {}, 帖子ID: {}", userId, itemId);
            return false;
        }

        // 3. 插入置顶申请
        TopRequest topRequest = new TopRequest();
        topRequest.setItemId(itemId);
        topRequest.setItemType(itemType);
        topRequest.setRequesterId(userId);
        topRequest.setDurationHours(requestDTO.getHours());

        topRequestMapper.insert(topRequest);

        log.info("置顶申请提交成功，用户: {}, 帖子ID: {}, 申请ID: {}", userId, itemId, topRequest.getId());
        return true;
    }
}