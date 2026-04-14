package com.jovi.service;

import com.jovi.pojo.CommentVO;
import com.jovi.pojo.Location;

import java.util.List;

public interface CommentService {
    List<CommentVO> getComments(Integer itemId, Integer itemType);
    Integer addComment(Integer userId, Integer itemId, Integer itemType, String content);
}
