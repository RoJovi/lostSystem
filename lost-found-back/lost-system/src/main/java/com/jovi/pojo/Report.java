package com.jovi.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Report {
    private Integer id;
    private Integer reporterId;     //谁举报的
    private Integer targetUserId;   //谁被举报了（举报用户时使用）
    private Integer itemId;         //被举报的物品ID（举报帖子时使用）
    private Integer itemType;       //物品类型：0-失物 1-拾物
    private Integer reportType;     //举报类型：0-举报帖子 1-举报用户
    private String reason;
    private Integer status;         //处理状态：0-待处理 1-已通过 2-已驳回
    private LocalDateTime createTime;
    private LocalDateTime handleTime;   //处理时间
}
