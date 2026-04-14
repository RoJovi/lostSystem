package com.jovi.controller;

import com.jovi.pojo.*;
import com.jovi.service.CommentService;
import com.jovi.service.FoundItemService;
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
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 获取评论列表
     */
    @GetMapping("/comments")
    public Result getComments(@RequestParam Integer itemId,
                              @RequestParam Integer itemType) {
        log.info("获取评论列表，帖子ID: {}, 类型: {}", itemId, itemType);
        List<CommentVO> list = commentService.getComments(itemId, itemType);
        return Result.success(list);
    }

    /**
     * 发表评论
     */
    @PostMapping("/comment")
    public Result addComment(@RequestBody AddCommentRequest request,
                             HttpServletRequest httpRequest) {
        Integer userId = (Integer) httpRequest.getAttribute("userId");
        if (userId == null) {
            return Result.error("未登录");
        }

        log.info("用户 {} 发表评论，帖子ID: {}, 类型: {}", userId, request.getItemId(), request.getItemType());

        Integer commentId = commentService.addComment(userId, request.getItemId(),
                request.getItemType(), request.getContent());

        Map<String, Integer> data = new HashMap<>();
        data.put("id", commentId);

        return Result.success(data);
    }
}
