package com.jovi.controller;

import com.jovi.pojo.Admin;
import com.jovi.pojo.Result;
import com.jovi.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    //注册
    @PostMapping("register")
    public Result register(@RequestBody Admin admin) {

        //先检查有没有注册过
        if(adminService.checkId(admin.getAdminNum())){
            return Result.error("账号已存在");
        }

        adminService.register(admin);
        return Result.success();
    }

    // 用id查询管理员信息
    @GetMapping("info")
    public Result getInfo(HttpServletRequest request) {
        Integer adminId = (Integer) request.getAttribute("userId");
        if (adminId == null) {
            return Result.error("未登录");
        }

        Admin admin = adminService.getById(adminId);
        if (admin == null) {
            return Result.error("用户不存在");
        }

        // 返回时去掉密码
        admin.setPassword(null);
        return Result.success(admin);
    }

    // 更新基本信息
    @PutMapping("info")
    public Result update(@RequestBody Admin admin, HttpServletRequest request) {
        // 获得管理员id
        Integer adminId = (Integer) request.getAttribute("userId");
        if (adminId == null) {
            return Result.error("未登录");
        }
        admin.setId(adminId);
        if (admin == null) {
            return Result.error("用户不存在");
        }

        adminService.update(admin);
        return Result.success();
    }
}