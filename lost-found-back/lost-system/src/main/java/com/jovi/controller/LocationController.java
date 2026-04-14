package com.jovi.controller;

import com.jovi.pojo.*;
import com.jovi.service.LocationService;
import com.jovi.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class LocationController {

    @Autowired
    private LocationService locationService;

    /**
     * 获取地点列表（树形结构）
     */
    @GetMapping("/locations")
    public Result getLocations() {
        log.info("获取地点列表");
        List<LocationVO> list = locationService.getTreeList();
        return Result.success(list);
    }

    /**
     * 添加自定义地点
     */
    @PostMapping("/location")
    public Result addLocation(@RequestBody AddLocationRequest request,
                              HttpServletRequest httpRequest) {
        Integer userId = (Integer) httpRequest.getAttribute("userId");
        if (userId == null) {
            return Result.error("未登录");
        }

        log.info("用户 {} 添加自定义地点: {}", userId, request.getName());

        Integer id = locationService.addLocation(request.getName(), request.getParentId(), userId);

        Map<String, Integer> data = new HashMap<>();
        data.put("id", id);

        return Result.success(data);
    }
}
