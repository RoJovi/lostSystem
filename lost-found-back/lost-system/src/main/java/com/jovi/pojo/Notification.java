package com.jovi.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    private Integer id;
    private Integer userId;     //谁的信息栏
    private Integer fromUserId; //谁发的信息
    private Integer type;       //通知类型：1-评论 2-私信 3-系统
    private String content;
    private Integer targetId;   //关联的帖子ID（评论时使用）
    private Integer isRead;     //0-未读 1-已读
    private LocalDateTime createTime;
}
