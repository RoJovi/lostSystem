package com.jovi.service.impl;

import com.jovi.mapper.UserMapper;
import com.jovi.pojo.LoginUser;
import com.jovi.pojo.LoginUserDTO;
import com.jovi.pojo.OldAndNewPassword;
import com.jovi.pojo.User;
import com.jovi.service.UserService;
import com.jovi.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    //登录
    @Override
    public LoginUser login(LoginUserDTO loginUserDTO) {
        //先用手机号判断，后用邮箱判断
        User user = userMapper.selectByPhoneAndPassword(loginUserDTO);
        if(user == null){
            user = userMapper.selectByEmailAndPassword(loginUserDTO);
        }

        log.info("活到拿到token了吗？");

        if(user == null) {
            return null;
        }

        //token自定义信息
        Map<String,Object> dataMap = new HashMap<>();	//Map集合
        dataMap.put("userId",user.getId());
        dataMap.put("account",user.getPhone()+user.getEmail());//普通用户就用手机号+邮箱统一签署token，管理员用工号；
        dataMap.put("userType", 0);            //0表示普通用户
        String jwt = JwtUtils.generateToken(dataMap);

        return new LoginUser(user.getId(), user.getNickname(), user.getEmail(), user.getPhone(), user.getAvatar(), jwt);
    }

    //查询普通用户信息
    @Override
    public User getById(Integer Id) {
        return userMapper.selectById(Id);
    }

    //修改基本信息
    @Override
    public boolean update(User user) {
        user.setUpdateTime(LocalDateTime.now());
        return userMapper.update(user)!=0;
    }

    @Override
    public void register(User user) {
        user.setUpdateTime(LocalDateTime.now());
        user.setCreateTime(LocalDateTime.now());
        userMapper.insertNumAndPassword(user);
    }

    @Override
    public boolean checkId(String email,String phone) {
        int find1 = userMapper.SelectByEmail(email);
        int find2 = userMapper.SelectByPhone(phone);
        return (find1 != 0 && find2 != 0);
    }

    @Override
    public boolean updatePassword(OldAndNewPassword oldAndNewPassword) {
        return userMapper.updatePassword(oldAndNewPassword)!=0;
    }

}
