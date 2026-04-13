package com.jovi.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Admin {
    private Integer id;
    private String adminNum;
    private String name;
    private String password;
    private String phone;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
