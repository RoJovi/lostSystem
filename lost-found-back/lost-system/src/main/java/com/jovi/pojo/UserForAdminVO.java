package com.jovi.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserForAdminVO {
    private Integer id;
    private String nickname;
    private String email;
    private String phone;
    private String avatar;
    private Integer status;        // 1-正常 0-封禁
    private Integer postCount;
    private Integer commentCount;
    private LocalDateTime createTime;
}
