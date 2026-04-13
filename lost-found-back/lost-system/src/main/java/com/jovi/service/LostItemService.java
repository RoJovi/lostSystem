package com.jovi.service;

import com.jovi.pojo.*;

import java.util.List;
import java.util.Map;

public interface LostItemService {
    // 发布失物
    LostItem submitLost(Integer userId, LostDTO lostDTO);

    // 获取失物详情
    LostDetailVO getDetail(Integer id);
}
