package com.jovi.service;

import com.jovi.pojo.CommentVO;
import com.jovi.pojo.FeedPostVO;

import java.util.List;

public interface FeedService {
    List<FeedPostVO> getFeedList(String type, String keyword, String sort, Integer locationId);
}
