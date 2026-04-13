package com.jovi.controller;

import com.jovi.pojo.Post;
import com.jovi.pojo.Result;
import com.jovi.pojo.UpdatePostRequest;
import com.jovi.pojo.User;
import com.jovi.service.PostService;
import com.jovi.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    //用我的id查询帖子列表
    @GetMapping("/user/posts")
    public Result getMyPost(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) {
            return Result.error("未登录");
        }

        List<Post> list = postService.getAll(userId);
        if (list.isEmpty()) {
            return Result.error("查无此帖");
        }

        return Result.success(list);

    }

    //用别人的id查询帖子列表
    @GetMapping("/user/{userId}/posts")
    public Result getHisPost(@PathVariable Integer userId) {
        if (userId == null) {
            return Result.error("未登录");
        }
        List<Post> list = postService.getAll(userId);
        if (list.isEmpty()) {
            return Result.error("查无此帖");
        }

        return Result.success(list);

    }

    //删帖
    @DeleteMapping("/post//{type}/{id}")
    public Result deletePost(@PathVariable String type,
                             @PathVariable Integer id,
                             @RequestAttribute("userId") Integer userId) {
        log.info("用户 {} 删除帖子，类型: {}, ID: {}", userId, type, id);

        boolean success = postService.deletePost(type, id, userId);

        if (!success) {
            return Result.error("帖子不存在或无权限删除");
        }

        return Result.success();
    }

    //更新帖子
    @PutMapping("/post/{type}/{id}")
    public Result updatePost(@PathVariable String type,
                             @PathVariable Integer id,
                             @RequestBody UpdatePostRequest request,
                             @RequestAttribute("userId") Integer userId) {
        log.info("用户 {} 更新帖子，类型: {}, ID: {}", userId, type, id);

        boolean success = postService.updatePost(type, id, request, userId);

        if (!success) {
            return Result.error("帖子不存在或无权限修改");
        }

        return Result.success(null);
    }

    //设置为已找回
    @PutMapping("/post/{type}/{id}/resolve")
    public Result setResolved(@PathVariable String type,
                              @PathVariable Integer id,
                              @RequestAttribute("userId") Integer userId) {
        log.info("用户 {} 标记帖子为已{}，类型: {}, ID: {}",
                userId, type.equals("lost") ? "找回" : "归还", type, id);

        boolean success = postService.setResolved(type, id, userId);

        if (!success) {
            return Result.error("帖子不存在或无权限操作");
        }

        return Result.success(null);
    }

}
