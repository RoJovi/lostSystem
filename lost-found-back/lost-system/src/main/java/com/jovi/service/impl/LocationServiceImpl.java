package com.jovi.service.impl;

import com.jovi.mapper.AdminMappper;
import com.jovi.mapper.LocationMapper;
import com.jovi.pojo.Admin;
import com.jovi.pojo.Location;
import com.jovi.pojo.LocationVO;
import com.jovi.pojo.LoginAdmin;
import com.jovi.service.AdminService;
import com.jovi.service.LocationService;
import com.jovi.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LocationServiceImpl implements LocationService {

    @Autowired
    private LocationMapper locationMapper;

    @Override
    public List<Location> getTreeList() {
        // 1. 查询所有地点
        List<Location> allLocations = locationMapper.selectAll();
/*
        // 2. 转换为 VO
        List<LocationVO> voList = allLocations.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        // 3. 构建父子关系（用 Map 快速查找）
        Map<Integer, LocationVO> map = new HashMap<>();
        for (LocationVO vo : voList) {
            map.put(vo.getId(), vo);
        }

        // 4. 组装树形结构
        List<LocationVO> treeList = new ArrayList<>();
        for (LocationVO vo : voList) {
            Integer parentId = getParentId(vo.getId(), allLocations);
            if (parentId == null || parentId == 0) {
                // 顶级节点
                treeList.add(vo);
            } else {
                // 子节点，添加到父节点的 children 中
                LocationVO parent = map.get(parentId);
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(vo);
                }
            }
        }*/

        return allLocations;
    }

    @Override
    public Integer addLocation(String name, Integer parentId, Integer userId) {
        Location location = new Location();
        location.setName(name);
        location.setParentId(parentId != null ? parentId : 0);
        location.setUserId(userId);

        locationMapper.insert(location);
        log.info("用户 {} 添加自定义地点: {}, 父级: {}", userId, name, parentId);

        return location.getId();
    }

    private LocationVO convertToVO(Location location) {
        LocationVO vo = new LocationVO();
        vo.setId(location.getId());
        vo.setName(location.getName());
        return vo;
    }

    private Integer getParentId(Integer id, List<Location> allLocations) {
        for (Location loc : allLocations) {
            if (loc.getId().equals(id)) {
                return loc.getParentId();
            }
        }
        return null;
    }
}
