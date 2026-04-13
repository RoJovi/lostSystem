package com.jovi.controller;

import com.jovi.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping
public class MessageController {

    // 获取未读消息数（临时返回0）
    @GetMapping("/message/unread-count")
    public Result getUnreadCount(@RequestAttribute("userId") Integer userId) {
        log.info("获取用户 {} 的未读消息数", userId);
        return Result.success(0);
    }

    // 获取消息列表（临时返回空数组）
    @GetMapping("/message/list")
    public Result getMessageList(@RequestAttribute("userId") Integer userId) {
        log.info("获取用户 {} 的消息列表", userId);
        return Result.success(new ArrayList<>());
    }

    // 发送私信
    @PostMapping("/message/send")
    public Result sendMessage(@RequestBody Map<String, Object> request,
                              @RequestAttribute("userId") Integer userId) {
        log.info("用户 {} 发送私信: {}", userId, request);
        return Result.success(null);
    }

    // 标记消息已读
    @PutMapping("/message/{id}/read")
    public Result markMessageRead(@PathVariable Integer id,
                                  @RequestAttribute("userId") Integer userId) {
        log.info("用户 {} 标记消息 {} 为已读", userId, id);
        return Result.success(null);
    }

}