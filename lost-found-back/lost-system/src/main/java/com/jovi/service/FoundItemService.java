package com.jovi.service;

import com.jovi.pojo.FoundDTO;
import com.jovi.pojo.FoundDetailVO;
import com.jovi.pojo.FoundItem;
import com.jovi.pojo.Post;

import java.util.List;
import java.util.Map;

public interface FoundItemService {

    FoundDetailVO getDetail(Integer id);

    FoundItem submitFound(Integer userId, FoundDTO foundDTO);
}
