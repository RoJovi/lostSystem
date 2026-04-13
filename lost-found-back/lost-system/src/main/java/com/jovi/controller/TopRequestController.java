package com.jovi.controller;

import com.jovi.pojo.Post;
import com.jovi.pojo.Result;
import com.jovi.pojo.TopRequestDTO;
import com.jovi.pojo.UpdatePostRequest;
import com.jovi.service.PostService;
import com.jovi.service.TopRequestService;
import com.jovi.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
public class TopRequestController {

    @Autowired
    private TopRequestService topRequestService;

    /**
     * 申请置顶
     */
    @PostMapping("/top/request")
    public Result applyTop(@RequestBody TopRequestDTO requestDTO,
                           HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) {
            return Result.error("未登录");
        }

        log.info("用户 {} 申请置顶帖子，帖子ID: {}, 类型: {}, 时长: {}小时",
                userId, requestDTO.getId(), requestDTO.getType(), requestDTO.getHours());

        boolean success = topRequestService.applyTop(userId, requestDTO);

        if (success) {
            return Result.success(null);
        } else {
            return Result.error("申请失败，帖子不存在或已被删除");
        }
    }
}