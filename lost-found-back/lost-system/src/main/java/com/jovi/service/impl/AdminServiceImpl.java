package com.jovi.service.impl;

import com.jovi.mapper.AdminMappper;
import com.jovi.pojo.Admin;
import com.jovi.pojo.LoginAdmin;
import com.jovi.service.AdminService;
import com.jovi.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMappper adminMappper;

    @Override
    public LoginAdmin login(Admin admin) {
        Admin a = adminMappper.selectByAdminNumAndPassword(admin);

        // token自定义信息
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("userId", a.getId());
        dataMap.put("adminNum", a.getAdminNum());
        dataMap.put("userType", 1);
        String jwt = JwtUtils.generateToken(dataMap);

        if (a != null) return new LoginAdmin(a.getId(), a.getAdminNum(), a.getName(), jwt);
        return null;
    }

    // 查询管理员信息
    @Override
    public Admin getById(Integer Id) {
        return adminMappper.selectById(Id);
    }

    // 修改基本信息
    @Override
    public void update(Admin admin) {
        admin.setUpdateTime(LocalDateTime.now());
        adminMappper.update(admin);
    }

    @Override
    public void register(Admin admin) {
        admin.setUpdateTime(LocalDateTime.now());
        admin.setCreateTime(LocalDateTime.now());
        adminMappper.insertNumAndPassword(admin);
    }

    @Override
    public boolean checkId(String adminNum) {
        int find = adminMappper.SelectByAdminNum(adminNum);
        return (find != 0);
    }
}
