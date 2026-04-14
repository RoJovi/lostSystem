package com.jovi.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopRequestForAdminVO {
    private Integer id;
    private Integer postId;
    private String postTitle;
    private String requesterName;
    private Integer durationHours;
    private Integer itemType;      // 0-失物 1-拾物
    private Integer status;        // 0-待审批 1-已通过 2-已拒绝
    private LocalDateTime createTime;
    private LocalDateTime approveTime;
}
