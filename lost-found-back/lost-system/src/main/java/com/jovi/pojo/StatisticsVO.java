package com.jovi.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsVO {
    private Integer totalPosts;        // 总帖子数
    private Integer totalUsers;        // 总用户数
    private Integer totalLost;         // 失物帖子数
    private Integer totalFound;        // 拾物帖子数
    private Integer totalResolved;     // 已找回/已归还数
    private Integer pendingReports;    // 待处理举报数
    private Integer pendingTopRequests;// 待审批置顶申请数
    private Integer activeUsers;       // 活跃用户数
    private List<Map<String, Object>> topLocations; // 失物高发区域TOP5
}
