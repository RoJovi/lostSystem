package com.jovi.service;

import com.jovi.pojo.LoginUser;
import com.jovi.pojo.LoginUserDTO;
import com.jovi.pojo.OldAndNewPassword;
import com.jovi.pojo.User;

public interface UserService {
    LoginUser login(LoginUserDTO loginUserDTO);

    User getById(Integer userId);

    boolean update(User user);

    void register(User user);

    boolean checkId(String email,String phone);

    boolean updatePassword(OldAndNewPassword oldAndNewPassword);
}
