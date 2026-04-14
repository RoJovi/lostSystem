package com.jovi.controller;

import com.jovi.pojo.LoginAdmin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import com.jovi.pojo.Admin;
import com.jovi.service.AdminService;
import com.jovi.utils.JwtUtils;
import com.jovi.pojo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/admin/login")
public class LoginAdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping
    public Result Login(@RequestBody Admin admin) {
        log.info("管理员登录尝试: {}", admin.getAdminNum());
        LoginAdmin info = adminService.login(admin);
        if (info != null) {
            log.info("登录成功: {}, id: {}", admin.getAdminNum(), admin.getId());
            return Result.success(info);
        }

        log.warn("登录失败，账号或密码错误");
        return Result.error("用户名或密码错误");
    }
}