package com.jovi.service.impl;

import com.jovi.mapper.CommentMapper;
import com.jovi.mapper.MessageMapper;
import com.jovi.mapper.UserMapper;
import com.jovi.pojo.*;
import com.jovi.service.CommentService;
import com.jovi.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public Integer sendMessage(Integer fromUserId, Integer toUserId, String content) {
        // 不能给自己发私信
        if (fromUserId.equals(toUserId)) {
            throw new RuntimeException("不能给自己发私信");
        }

        Message message = new Message();
        message.setFromUserId(fromUserId);
        message.setToUserId(toUserId);
        message.setContent(content);

        messageMapper.insert(message);
        log.info("用户 {} 发送私信给用户 {}，ID: {}", fromUserId, toUserId, message.getId());

        return message.getId();
    }

    @Override
    public List<MessageVO> getMessageList(Integer userId) {
        List<Message> messages = messageMapper.selectByUserId(userId);
        List<MessageVO> result = new ArrayList<>();

        for (Message msg : messages) {
            // 获取对方用户信息
            Integer otherUserId = msg.getFromUserId().equals(userId) ? msg.getToUserId() : msg.getFromUserId();
            User otherUser = userMapper.selectById(otherUserId);

            MessageVO vo = new MessageVO();
            vo.setId(msg.getId());
            vo.setType("private");
            vo.setFromUserId(msg.getFromUserId());
            vo.setFromUserNickname(otherUser != null ? otherUser.getNickname() : "无名氏");
            vo.setFromUserAvatar(otherUser != null ? otherUser.getAvatar() : "/default-avatar.png");
            vo.setContent(msg.getContent());
            vo.setIsRead(msg.getIsRead() == 1);
            vo.setCreateTime(msg.getCreateTime());

            result.add(vo);
        }

        return result;
    }

    @Override
    public Integer getUnreadCount(Integer userId) {
        return messageMapper.countUnread(userId);
    }

    @Override
    public boolean markMessageRead(Integer messageId, Integer userId) {
        int rows = messageMapper.markAsRead(messageId, userId);
        return rows > 0;
    }
}