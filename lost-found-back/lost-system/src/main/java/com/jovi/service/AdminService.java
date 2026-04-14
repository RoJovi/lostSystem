package com.jovi.service;

import com.jovi.pojo.*;

import java.util.List;

public interface AdminService {
    LoginAdmin login(Admin admin);

    Admin getById(Integer adminId);

    void update(Admin admin);

    void register(Admin admin);

    boolean checkId(String adminNum);

    void banUser(Integer userId, Integer status);

    List<UserForAdminVO> getAllUsers(String keyword);

    void deleteUser(Integer userId);

    List<TopRequestForAdminVO> getTopRequests(Integer status);

    void approveTopRequest(Integer id, Boolean approved);

    StatisticsVO getStatistics();

    boolean updatePassword(Integer adminId, String oldPassword, String newPassword);
}
