package com.jovi.service;

import com.jovi.pojo.Admin;
import com.jovi.pojo.LoginAdmin;

public interface AdminService {
    LoginAdmin login(Admin admin);

    Admin getById(Integer adminId);

    void update(Admin admin);

    void register(Admin admin);

    boolean checkId(String adminNum);
}
