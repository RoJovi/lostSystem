package com.jovi.controller;

import com.jovi.pojo.*;
import com.jovi.service.FoundItemService;
import com.jovi.service.PostService;
import com.jovi.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping
public class FoundItemController {

    @Autowired
    private FoundItemService foundItemService;

    @Autowired
    private UserService userService;

    //发布拾物信息
    @PostMapping("/found/create")
    public Result submitFound(@RequestBody FoundDTO foundDTO, HttpServletRequest request) {
        //获得用户id
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) {
            return Result.error("未登录");
        }

        log.info("用户 {} 发布失物: {}", userId, foundDTO.getTitle());

        FoundItem foundItem = foundItemService.submitFound(userId,foundDTO);

        if (foundItem != null) {
            return Result.success(foundItem.getId());
        }
        return Result.error("发布失败，请重试");
    }

    //查看拾物帖详情
    @GetMapping("/found/{id}")
    public Result getFoundDetail(@PathVariable Integer id) {
        log.info("获取拾物详情，id: {}", id);
        FoundDetailVO  detail = foundItemService.getDetail(id);
        if (detail == null) {
            return Result.error("帖子不存在");
        }
        return Result.success(detail);
    }


}
