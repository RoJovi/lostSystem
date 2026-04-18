package com.jovi.controller;

import com.jovi.pojo.*;
import com.jovi.service.AIService;
import com.jovi.service.AdminService;
import com.jovi.service.ReportService;
import com.jovi.utils.ValidationUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private AIService aiService;


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

        // 密码长度校验
        if (!ValidationUtils.isValidPassword(admin.getPassword())) {
            return Result.error(ValidationUtils.getPasswordRequirement());
        }

        adminService.update(admin);
        return Result.success();
    }
    /**
     * 获取举报列表（管理员）
     */
    @GetMapping("/reports")
    public Result getReports(@RequestParam(required = false) Integer status) {
        log.info("管理员获取举报列表，状态筛选: {}", status);
        List<ReportAdminVO> list = reportService.getAllReports(status);
        return Result.success(list);
    }

    /**
     * 处理举报（管理员）
     */
    @PutMapping("/report/{id}")
    public Result handleReport(@PathVariable Integer id,
                               @RequestBody Map<String, String> request) {
        String action = request.get("action"); // "approve" 或 "reject"
        log.info("管理员处理举报，ID: {}, 操作: {}", id, action);

        boolean success = reportService.handleReport(id, action);

        if (!success) {
            return Result.error("举报不存在");
        }

        return Result.success(null);
    }

    /**
     * 管理员修改密码
     */
    @PutMapping("/password")
    public Result updateAdminPassword(@RequestBody OldAndNewPassword request,
                                      HttpServletRequest httpRequest) {
        Integer adminId = (Integer) httpRequest.getAttribute("userId");
        if (adminId == null) {
            return Result.error("未登录");
        }

        log.info("管理员 {} 修改密码", adminId);

        boolean success = adminService.updatePassword(adminId, request.getOldPassword(), request.getNewPassword());

        if (!success) {
            return Result.error("原密码错误");
        }

        return Result.success(null);
    }

    /**
     * 获取所有用户列表
     */
    @GetMapping("/users")
    public Result getAllUsers(@RequestParam(required = false) String keyword) {
        log.info("管理员获取用户列表，关键词: {}", keyword);
        List<UserForAdminVO> list = adminService.getAllUsers(keyword);
        return Result.success(list);
    }

    /**
     * 封禁/解封用户
     */
    @PutMapping("/user/{userId}/ban")
    public Result banUser(@PathVariable Integer userId,
                          @RequestBody Map<String, Integer> request) {
        Integer status = request.get("status");
        log.info("管理员封禁/解封用户，ID: {}, 状态: {}", userId, status);

        adminService.banUser(userId, status);
        return Result.success(null);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/user/{userId}")
    public Result deleteUser(@PathVariable Integer userId) {
        log.info("管理员删除用户，ID: {}", userId);
        adminService.deleteUser(userId);
        return Result.success(null);
    }

    /**
     * 获取置顶申请列表
     */
    @GetMapping("/top-requests")
    public Result getTopRequests(@RequestParam(required = false) Integer status) {
        log.info("管理员获取置顶申请列表，状态: {}", status);
        List<TopRequestForAdminVO> list = adminService.getTopRequests(status);
        return Result.success(list);
    }

    /**
     * 审批置顶申请
     */
    @PutMapping("/top-request/{id}")
    public Result approveTopRequest(@PathVariable Integer id,
                                    @RequestBody Map<String, Boolean> request) {
        Boolean approved = request.get("approved");
        log.info("管理员审批置顶申请，ID: {}, 结果: {}", id, approved);

        adminService.approveTopRequest(id, approved);
        return Result.success(null);
    }

    /**
     * 获取统计数据
     */
    @GetMapping("/statistics")
    public Result getStatistics() {
        log.info("管理员获取统计数据");
        StatisticsVO stats = adminService.getStatistics();
        return Result.success(stats);
    }

    // AdminController.java 中添加

    /**
     * AI 统计分析 - 失物高发区域、热门丢失物品
     */
    @GetMapping("/ai/statistics")
    public Result getAIStatistics() {
        log.info("管理员请求AI统计分析");

        // 获取统计数据
        StatisticsVO stats = adminService.getStatistics();

        String prompt = String.format(
                "基于以下数据生成一段100字左右的失物招领分析报告：总帖子数%d，丢失帖子%d，拾取帖子%d，已找回%d。" +
                        "高发区域：%s。请分析哪个区域失物最多，哪种物品丢失最多，并给出建议。",
                stats.getTotalPosts(), stats.getTotalLost(), stats.getTotalFound(),
                stats.getTotalResolved(), stats.getTopLocations()
        );

        String analysis = aiService.analyzeStatistics(prompt);
        return Result.success(analysis);
    }
}