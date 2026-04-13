package com.jovi.controller;

import com.jovi.pojo.OldAndNewPassword;
import com.jovi.pojo.Result;
import com.jovi.pojo.User;
import com.jovi.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    //注册
    @PostMapping("/register")
    public Result register(@RequestBody User user) {

        //先检查有没有注册过
        if(userService.checkId(user.getEmail(),user.getPhone())){
            return Result.error("手机号或邮箱已经被绑定过了哦");
        }

        userService.register(user);
        return Result.success();
    }

    //用id查询用户信息
    @GetMapping("/info")
    public Result getInfo(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) {
            return Result.error("未登录");
        }

        User user = userService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 返回时去掉密码
        user.setPassword(null);
        return Result.success(user);

    }


    //更新基本信息
    @PutMapping("info")
    public Result update(@RequestBody User user, HttpServletRequest request) {
        //获得用户id
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) {
            return Result.error("未登录");
        }
        user.setId(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        if(userService.update(user)) {
            return Result.success();
        }
        return Result.error("信息修改失败");
    }

    //改密码
    @PutMapping("/password")
    public Result changePassword(@RequestBody OldAndNewPassword oldAndNewPassword, HttpServletRequest request) {
        //获得用户id
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) {
            return Result.error("未登录");
        }

        if (userService.getById(userId) == null) {
            return Result.error("用户不存在");
        }

        oldAndNewPassword.setUserId(userId);
        if(userService.updatePassword(oldAndNewPassword)) {
            return Result.success();
        }
        return Result.error("密码修改失败");
    }

    //用id查询其他用户信息
    @GetMapping("/{userId}/info")
    public Result getInfo(@PathVariable Integer userId) {
        if (userId == null) {
            return Result.error("查无此人");
        }

        User user = userService.getById(userId);

        if (user == null) {
            return Result.error("啊嘞？这个用户不存在");
        }

        // 返回时去掉多余信息
        user.setPassword(null);
        user.setPhone(null);
        user.setEmail(null);
        user.setStatus(null);
        user.setIsActive(null);
        return Result.success(user);

    }

}
