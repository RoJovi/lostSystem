package com.jovi.service.impl;

import com.jovi.mapper.CommentMapper;
import com.jovi.mapper.FeedMapper;
import com.jovi.mapper.UserMapper;
import com.jovi.pojo.Comment;
import com.jovi.pojo.CommentVO;
import com.jovi.pojo.FeedPostVO;
import com.jovi.pojo.User;
import com.jovi.service.CommentService;
import com.jovi.service.FeedService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class FeedServiceImpl implements FeedService {

    @Autowired
    private FeedMapper feedMapper;

    @Override
    public List<FeedPostVO> getFeedList(String type, String keyword, String sort, Integer locationId) {
        return feedMapper.getFeedList(type, keyword, sort, locationId);
    }
}