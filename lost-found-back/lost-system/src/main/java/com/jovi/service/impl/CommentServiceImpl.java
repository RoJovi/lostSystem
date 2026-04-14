package com.jovi.service.impl;

import com.jovi.mapper.CommentMapper;
import com.jovi.mapper.LocationMapper;
import com.jovi.mapper.UserMapper;
import com.jovi.pojo.*;
import com.jovi.service.CommentService;
import com.jovi.service.LocationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<CommentVO> getComments(Integer itemId, Integer itemType) {
        List<Comment> comments = commentMapper.selectByItem(itemId, itemType);
        List<CommentVO> result = new ArrayList<>();

        for (Comment comment : comments) {
            User user = userMapper.selectById(comment.getUserId());

            CommentVO vo = new CommentVO();
            vo.setId(comment.getId());
            vo.setUserId(comment.getUserId());
            vo.setUserNickname(user != null ? user.getNickname() : "无名氏");
            vo.setUserAvatar(user != null ? user.getAvatar() : "/default-avatar.png");
            vo.setContent(comment.getContent());
            vo.setCreateTime(comment.getCreateTime());

            result.add(vo);
        }

        return result;
    }

    @Override
    @Transactional
    public Integer addComment(Integer userId, Integer itemId, Integer itemType, String content) {
        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setItemId(itemId);
        comment.setItemType(itemType);
        comment.setContent(content);

        commentMapper.insert(comment);

        // 可选：增加用户的评论计数
        userMapper.incrementCommentCount(userId);

        log.info("用户 {} 评论了帖子 {}-{}", userId, itemId, itemType);
        return comment.getId();
    }
}