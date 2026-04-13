package com.jovi.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor

//置顶请求
public class TopRequest {
    private Integer id;
    private Integer itemId;     //物品ID
    private Integer itemType;   //物品类型：0-失物 1-拾物
    private Integer requesterId;//申请人ID
    private Integer durationHours;//申请置顶时长
    private Integer status;         //状态：0-待审批 1-已通过 2-已拒绝
    private LocalDateTime createTime;
    private LocalDateTime approveTime;//审批时间
}
