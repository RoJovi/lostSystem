package com.jovi.controller;

import com.jovi.pojo.MessageVO;
import com.jovi.pojo.Result;
import com.jovi.pojo.SendMessageRequest;
import com.jovi.service.MessageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
public class MessageController {

    @Autowired
    private MessageService messageService;

    /**
     * 获取消息列表
     */
    @GetMapping("/message/list")
    public Result getMessageList(HttpServletRequest httpRequest) {
        Integer userId = (Integer) httpRequest.getAttribute("userId");
        if (userId == null) {
            return Result.error("未登录");
        }

        log.info("获取用户 {} 的消息列表", userId);
        List<MessageVO> list = messageService.getMessageList(userId);
        return Result.success(list);
    }
    /**
     * 发送私信
     */
    @PostMapping("/message/send")
    public Result sendMessage(@RequestBody SendMessageRequest request,
                              HttpServletRequest httpRequest) {
        Integer fromUserId = (Integer) httpRequest.getAttribute("userId");
        if (fromUserId == null) {
            return Result.error("未登录");
        }

        log.info("用户 {} 发送私信给用户 {}: {}", fromUserId, request.getToUserId(), request.getContent());

        Integer messageId = messageService.sendMessage(fromUserId, request.getToUserId(), request.getContent());

        Map<String, Integer> data = new HashMap<>();
        data.put("id", messageId);

        return Result.success(data);
    }

    /**
     * 获取未读消息数
     */
    @GetMapping("/message/unread-count")
    public Result getUnreadCount(HttpServletRequest httpRequest) {
        Integer userId = (Integer) httpRequest.getAttribute("userId");
        if (userId == null) {
            return Result.error("未登录");
        }

        Integer count = messageService.getUnreadCount(userId);
        log.info("用户 {} 未读消息数: {}", userId, count);
        return Result.success(count);
    }

    /**
     * 标记消息已读
     */
    @PutMapping("/message/{id}/read")
    public Result markMessageRead(@PathVariable Integer id,
                                  HttpServletRequest httpRequest) {
        Integer userId = (Integer) httpRequest.getAttribute("userId");
        if (userId == null) {
            return Result.error("未登录");
        }

        log.info("用户 {} 标记消息 {} 为已读", userId, id);
        boolean success = messageService.markMessageRead(id, userId);

        if (!success) {
            return Result.error("消息不存在或无权限");
        }

        return Result.success(null);
    }

}