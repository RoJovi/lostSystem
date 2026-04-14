package com.jovi.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequest {
    private Integer itemId;       // 帖子ID（举报帖子时）
    private Integer itemType;     // 帖子类型：0-失物 1-拾物
    private Integer targetUserId; // 被举报用户ID（举报用户时）
    private String reason;        // 举报原因
}
