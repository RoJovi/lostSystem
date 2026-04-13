package com.jovi.controller;

import com.jovi.pojo.*;
import com.jovi.service.FoundItemService;
import com.jovi.service.LostItemService;
import com.jovi.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping
public class LostItemController {

    @Autowired
    private LostItemService lostItemService;

    // 发布失物信息
    @PostMapping("/lost/create")
    public Result submitLost(@RequestBody LostDTO lostDTO, HttpServletRequest request) {
        // 获得用户id
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) {
            return Result.error("未登录");
        }

        log.info("用户 {} 发布失物: {}", userId, lostDTO.getTitle());

        LostItem lostItem = lostItemService.submitLost(userId, lostDTO);

        if (lostItem != null) {
            return Result.success(lostItem.getId());
        }
        return Result.error("发布失败，请重试");
    }

    // 查看失物帖详情
    @GetMapping("/lost/{id}")
    public Result getLostDetail(@PathVariable Integer id) {
        log.info("获取失物详情，id: {}", id);
        LostDetailVO detail = lostItemService.getDetail(id);
        if (detail == null) {
            return Result.error("帖子不存在");
        }
        return Result.success(detail);
    }
}
