package com.jovi.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportAdminVO {
    private Integer id;
    private Integer itemId;
    private Integer itemType;
    private String postTitle;      // 被举报帖子标题
    private String reporterName;   // 举报人昵称
    private String reason;         // 举报原因
    private Integer status;        // 0-待处理 1-已通过 2-已驳回
    private LocalDateTime createTime;
    private LocalDateTime handleTime;
}
