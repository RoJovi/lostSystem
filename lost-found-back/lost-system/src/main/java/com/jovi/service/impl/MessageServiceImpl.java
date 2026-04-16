package com.jovi.service.impl;

import com.jovi.mapper.*;
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

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private FoundItemMapper foundItemMapper;

    @Autowired
    private LostItemMapper lostItemMapper;


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
        List<MessageVO> result = new ArrayList<>();

        // 1. 查询私信
        List<Message> messages = messageMapper.selectByUserId(userId);
        for (Message msg : messages) {
            // 获取发送者信息（不是对方）
            User fromUser = userMapper.selectById(msg.getFromUserId());

            MessageVO vo = new MessageVO();
            vo.setId(msg.getId());
            vo.setType("private");
            vo.setFromUserId(msg.getFromUserId());
            vo.setFromUserNickname(fromUser != null ? fromUser.getNickname() : "未知");
            vo.setFromUserAvatar(fromUser != null ? fromUser.getAvatar() : "/default-avatar.png");
            vo.setContent(msg.getContent());
            vo.setIsRead(msg.getIsRead() == 1);
            vo.setCreateTime(msg.getCreateTime());
            vo.setToUserId(msg.getToUserId());
            result.add(vo);
        }

        // 查询评论（作为消息）
        List<Comment> comments = commentMapper.selectCommentsOnMyPosts(userId);
        for (Comment comment : comments) {
            User commentUser = userMapper.selectById(comment.getUserId());

            MessageVO vo = new MessageVO();
            vo.setId(comment.getId());
            vo.setType("comment");
            vo.setFromUserId(comment.getUserId());
            vo.setFromUserNickname(commentUser != null ? commentUser.getNickname() : "未知");
            vo.setFromUserAvatar(commentUser != null ? commentUser.getAvatar() : "/default-avatar.png");
            vo.setContent(comment.getContent());
            vo.setIsRead(true);
            vo.setCreateTime(comment.getCreateTime());
            vo.setPostId(comment.getItemId());
            vo.setPostType(comment.getItemType());

            String title = comment.getItemType() == 0 ?
                    lostItemMapper.selectTitleById(comment.getItemId()) :
                    foundItemMapper.selectTitleById(comment.getItemId());
            vo.setPostTitle(title);
            result.add(vo);
        }

        // 按时间倒序排序
        result.sort((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()));

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