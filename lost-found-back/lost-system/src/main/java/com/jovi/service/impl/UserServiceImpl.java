package com.jovi.service.impl;

import com.jovi.mapper.UserMapper;
import com.jovi.pojo.LoginUser;
import com.jovi.pojo.LoginUserDTO;
import com.jovi.pojo.OldAndNewPassword;
import com.jovi.pojo.User;
import com.jovi.service.UserService;
import com.jovi.utils.JwtUtils;
import com.jovi.utils.PasswordEncoder;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

    //登录
    @Override
    public LoginUser login(LoginUserDTO loginUserDTO) {
        //1.先用手机号判断，后用邮箱判断
        User user = userMapper.selectByPhone(loginUserDTO.getAccount());
        if(user == null){
            user = userMapper.selectByEmail(loginUserDTO.getAccount());
        }

        if(user == null) {
            return null;
        }

        // 2. 验证密码（明文 vs 密文）
        if (!passwordEncoder.matches(loginUserDTO.getPassword(), user.getPassword())) {
            return null;
        }

        //token自定义信息
        Map<String,Object> dataMap = new HashMap<>();	//Map集合
        dataMap.put("userId",user.getId());
        dataMap.put("account",user.getPhone()+user.getEmail());//普通用户就用手机号+邮箱统一签署token，管理员用工号；
        dataMap.put("userType", 0);            //0表示普通用户
        String jwt = JwtUtils.generateToken(dataMap);

        return new LoginUser(user.getId(), user.getNickname(), user.getEmail(), user.getPhone(), user.getAvatar(), jwt,user.getStatus());
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
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userMapper.insertNumAndPassword(user);
    }

    @Override
    public boolean checkId(String email,String phone) {
        int find1 = userMapper.SelectByEmail(email);
        int find2 = userMapper.SelectByPhone(phone);
        return (find1 != 0 || find2 != 0);
    }

    //改密码，检查密文
    @Override
    public boolean updatePassword(OldAndNewPassword oldAndNewPassword) {
        // 1. 先根据用户ID查询当前用户信息
        User user = userMapper.selectById(oldAndNewPassword.getUserId());
        if (user == null) {
            return false;
        }

        // 2. 验证原密码是否正确（明文 vs 密文）
        if (!passwordEncoder.matches(oldAndNewPassword.getOldPassword(), user.getPassword())) {
            return false;
        }

        // 3. 加密新密码
        oldAndNewPassword.setNewPassword(passwordEncoder.encode(oldAndNewPassword.getNewPassword()));

        // 4. 更新密码
        return userMapper.updatePassword(oldAndNewPassword) != 0;
    }

    //检查邮箱、手机号有没有被其它用户占用
    @Override
    public boolean checkUpdate(User user) {
        // 检查邮箱是否被其他用户占用
        User existingByEmail = userMapper.selectByEmail(user.getEmail());
        if (existingByEmail != null && !existingByEmail.getId().equals(user.getId())) {
            throw new RuntimeException("该邮箱已被其他用户绑定");
        }

        // 检查手机号是否被其他用户占用
        User existingByPhone = userMapper.selectByPhone(user.getPhone());
        if (existingByPhone != null && !existingByPhone.getId().equals(user.getId())) {
            throw new RuntimeException("该手机号已被其他用户绑定");
        }

        user.setUpdateTime(LocalDateTime.now());
        return userMapper.update(user) != 0;
    }

}
