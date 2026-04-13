package com.jovi.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Integer id;
    private String email;
    private String phone;
    private String nickname;
    private String password;
    private String avatar;  //头像URL
    private Integer status;  // 状态：0-封禁 1-正常
    private Integer postCount;  // 发布次数
    private Integer commentCount;  // 评论次数
    private Integer isActive; //是否活跃用户：0-否 1-是
    private LocalDateTime lastActiveTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
