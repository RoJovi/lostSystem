package com.jovi.service;

import com.jovi.pojo.FoundDTO;
import com.jovi.pojo.FoundDetailVO;
import com.jovi.pojo.FoundItem;
import com.jovi.pojo.TopRequestDTO;

public interface TopRequestService {

    boolean applyTop(Integer userId, TopRequestDTO requestDTO);
}
