package com.jovi.service.impl;

import com.jovi.mapper.LocationMapper;
import com.jovi.pojo.Location;
import com.jovi.pojo.LocationVO;
import com.jovi.service.LocationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public List<LocationVO> getTreeList() {
        // 1. 查询所有地点
        List<Location> allLocations = locationMapper.selectAll();

        if (allLocations == null || allLocations.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 转换为 VO
        List<LocationVO> voList = allLocations.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        // 3. 构建 id -> VO 的映射，方便快速查找父节点
        Map<Integer, LocationVO> nodeMap = new HashMap<>();
        for (LocationVO vo : voList) {
            nodeMap.put(vo.getId(), vo);
        }

        // 4. 构建父子关系
        List<LocationVO> treeList = new ArrayList<>();
        for (Location location : allLocations) {
            LocationVO vo = nodeMap.get(location.getId());
            Integer parentId = location.getParentId();

            if (parentId == null || parentId == 0) {
                // 顶级节点
                treeList.add(vo);
            } else {
                // 子节点，找到父节点并添加
                LocationVO parent = nodeMap.get(parentId);
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(vo);
                } else {
                    // 父节点不存在（数据异常），作为顶级节点处理
                    treeList.add(vo);
                }
            }
        }

        return treeList;
    }

    @Override
    public Integer addLocation(String name, Integer parentId, Integer userId) {
        Location location = new Location();
        location.setName(name);
        location.setParentId(parentId != null ? parentId : 0);
        location.setUserId(userId);
        location.setIsCustom(1);

        locationMapper.insert(location);
        log.info("用户 {} 添加自定义地点: {}, 父级: {}", userId, name, parentId);

        return location.getId();
    }

    private LocationVO convertToVO(Location location) {
        LocationVO vo = new LocationVO();
        vo.setId(location.getId());
        vo.setName(location.getName());
        vo.setChildren(new ArrayList<>());
        return vo;
    }
}