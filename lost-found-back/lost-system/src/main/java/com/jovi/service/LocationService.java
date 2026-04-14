package com.jovi.service;

import com.jovi.pojo.Admin;
import com.jovi.pojo.Location;
import com.jovi.pojo.LocationVO;
import com.jovi.pojo.LoginAdmin;

import java.util.List;

public interface LocationService {
    List<LocationVO> getTreeList();
    Integer addLocation(String name, Integer parentId, Integer userId);
}
