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

import static com.jovi.utils.ValidationUtils.*;

@RestController
@Slf4j
@RequestMapping("/user/login")
public class LoginUserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public Result Login(@RequestBody LoginUserDTO loginUserDTO) {
        // 邮箱格式校验
        if (!isValidEmail(loginUserDTO.getAccount()) && !isValidPhone(loginUserDTO.getAccount())) {
            return Result.error("邮箱或手机号格式不正确");
        }
        // 密码长度校验
        if (!isValidPassword(loginUserDTO.getPassword())) {
            return Result.error(getPasswordRequirement());
        }
        log.info("普通用户登录尝试: {}", loginUserDTO.getAccount());
        LoginUser info = userService.login(loginUserDTO);
        if (info == null) {
            log.warn("登录失败，账号或密码错误: {}", loginUserDTO.getAccount());
            return Result.error("用户名或密码错误");
        }

        // 再判断封禁状态
        if (info.getStatus() == 0) {
            return Result.error("账号已被封禁，请联系管理员");
        }

        log.info("登录成功: {}, id: {}", loginUserDTO.getAccount(), info.getId());
        return Result.success(info);
    }
}
