package com.jovi.service.impl;

import com.jovi.mapper.*;
import com.jovi.pojo.*;
import com.jovi.service.AdminService;
import com.jovi.utils.JwtUtils;
import com.jovi.utils.PasswordEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private FoundItemMapper foundItemMapper;

    @Autowired
    private LostItemMapper lostItemMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private TopRequestMapper topRequestMapper;

    @Autowired
    private ReportMapper reportMapper;

    @Override
    public LoginAdmin login(Admin admin) {
        Admin a = adminMapper.selectByAdminNum(admin);

        if (a == null) {
            return null;
        }

        // 2. 验证密码（明文 vs 密文）
        if (!passwordEncoder.matches(admin.getPassword(), a.getPassword())) {
            return null;
        }

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
        return adminMapper.selectById(Id);
    }

    // 修改基本信息
    @Override
    public void update(Admin admin) {
        admin.setUpdateTime(LocalDateTime.now());
        adminMapper.update(admin);
    }

    @Override
    public void register(Admin admin) {
        admin.setUpdateTime(LocalDateTime.now());
        admin.setCreateTime(LocalDateTime.now());
        adminMapper.insertNumAndPassword(admin);
    }

    @Override
    public boolean checkId(String adminNum) {
        int find = adminMapper.SelectByAdminNum(adminNum);
        return (find != 0);
    }

    @Override
    public List<UserForAdminVO> getAllUsers(String keyword) {
        List<User> users;
        if (keyword != null && !keyword.isEmpty()) {
            users = userMapper.selectByKeyword(keyword);
        } else {
            users = userMapper.selectAll();
        }

        return users.stream().map(this::convertToAdminVO).collect(Collectors.toList());
    }

    @Override
    public void banUser(Integer userId, Integer status) {
        userMapper.updateStatus(userId, status);
    }

    @Override
    @Transactional
    public void deleteUser(Integer userId) {
        // 删除用户的所有帖子
        lostItemMapper.deleteByUserId(userId);
        foundItemMapper.deleteByUserId(userId);
        // 删除用户的评论
        commentMapper.deleteByUserId(userId);
        // 删除用户
        userMapper.deleteById(userId);
    }

    @Override
    public List<TopRequestForAdminVO> getTopRequests(Integer status) {
        List<TopRequest> requests;
        if (status != null) {
            requests = topRequestMapper.selectByStatus(status);
        } else {
            requests = topRequestMapper.selectAll();
        }

        List<TopRequestForAdminVO> result = new ArrayList<>();
        for (TopRequest req : requests) {
            TopRequestForAdminVO vo = new TopRequestForAdminVO();
            vo.setId(req.getId());
            vo.setPostId(req.getItemId());
            vo.setItemType(req.getItemType());
            vo.setDurationHours(req.getDurationHours());
            vo.setStatus(req.getStatus());
            vo.setCreateTime(req.getCreateTime());
            vo.setApproveTime(req.getApproveTime());

            // 获取帖子标题
            String title = req.getItemType() == 0 ?
                    lostItemMapper.selectTitleById(req.getItemId()) :
                    foundItemMapper.selectTitleById(req.getItemId());
            vo.setPostTitle(title);

            // 获取申请人名称
            User user = userMapper.selectById(req.getRequesterId());
            vo.setRequesterName(user != null ? user.getNickname() : "未知用户");

            result.add(vo);
        }
        return result;
    }

    @Override
    @Transactional
    public void approveTopRequest(Integer requestId, Boolean approved) {
        TopRequest topRequest = topRequestMapper.selectById(requestId);
        if (topRequest == null) {
            throw new RuntimeException("申请不存在");
        }

        Integer status = approved ? 1 : 2;
        topRequestMapper.updateStatus(requestId, status, LocalDateTime.now());

        if (approved) {
            // 设置帖子置顶
            LocalDateTime expireTime = LocalDateTime.now().plusHours(topRequest.getDurationHours());
            if (topRequest.getItemType() == 0) {
                lostItemMapper.setTop(topRequest.getItemId(), expireTime);
            } else {
                // 拾物没有置顶功能，可以忽略或记录日志
                log.warn("拾物帖子不支持置顶，申请ID: {}", requestId);
            }
        }
    }

    @Override
    public StatisticsVO getStatistics() {
        StatisticsVO stats = new StatisticsVO();

        // 总用户数
        stats.setTotalUsers(userMapper.countAll());

        // 活跃用户数（发帖+评论 >= 5）
        stats.setActiveUsers(userMapper.countActiveUsers());
        // 失物统计
        stats.setTotalLost(lostItemMapper.countAll());
        stats.setTotalFound(foundItemMapper.countAll());
        stats.setTotalPosts(stats.getTotalLost() + stats.getTotalFound());

        // 已找回/已归还
        stats.setTotalResolved(lostItemMapper.countResolved() + foundItemMapper.countResolved());

        // 待处理举报
        stats.setPendingReports(reportMapper.countByStatus(0));

        // 待审批置顶申请
        stats.setPendingTopRequests(topRequestMapper.countByStatus(0));

        // 失物高发区域 TOP 5
        stats.setTopLocations(lostItemMapper.getTopLocations());

        return stats;
    }

    @Override
    public boolean updatePassword(Integer adminId, String oldPassword, String newPassword) {
        // 1. 验证原密码是否正确
        Admin admin = adminMapper.selectPasswordById(adminId);
        if (admin == null) {
            return false;
        }

        if (!passwordEncoder.matches(oldPassword, admin.getPassword())) {
            return false;
        }

        // 2. 更新密码
        adminMapper.updatePassword(adminId, passwordEncoder.encode(newPassword));
        log.info("管理员 {} 密码修改成功", adminId);

        return true;
    }

    private UserForAdminVO convertToAdminVO(User user) {
        UserForAdminVO vo = new UserForAdminVO();
        vo.setId(user.getId());
        vo.setNickname(user.getNickname());
        vo.setEmail(user.getEmail());
        vo.setPhone(user.getPhone());
        vo.setAvatar(user.getAvatar());
        vo.setStatus(user.getStatus());
        vo.setPostCount(user.getPostCount());
        vo.setCommentCount(user.getCommentCount());
        vo.setCreateTime(user.getCreateTime());
        vo.setIsActive(user.getIsActive());  // 使用 isActive
        return vo;
    }
}
