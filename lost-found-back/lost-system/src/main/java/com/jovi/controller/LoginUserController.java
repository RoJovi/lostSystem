package com.jovi.controller;

import com.jovi.pojo.LoginUser;
import com.jovi.pojo.LoginUserDTO;
import com.jovi.pojo.Result;
import com.jovi.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/user/login")
public class LoginUserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public Result Login(@RequestBody LoginUserDTO loginUserDTO) {
        log.info("普通用户登录尝试: {}", loginUserDTO.getAccount());
        LoginUser info = userService.login(loginUserDTO);
        if (info != null) {
            log.info("登录成功: {}, id: {}", loginUserDTO.getAccount(), info.getId());
            return Result.success(info);
        }
        log.warn("登录失败，账号或密码错误: {}", loginUserDTO.getAccount());
        return Result.error("用户名或密码错误");
    }
}
