package com.jovi.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginUser {
    private Integer id;
    private String nickname;
    private String email;
    private String phone;
    private String avatar;
    private String token;
    private Integer status;  // 状态：0-封禁 1-正常
}
