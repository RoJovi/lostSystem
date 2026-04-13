package com.jovi.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
//用来展示详情的
public class FoundDetailVO {
    private Integer id;
    private Integer type;           // 固定为 1
    private String title;
    private String description;
    private Integer locationId;
    private String locationName;
    private LocalDateTime foundTime;       // 拾到时间
    private String imageUrl;
    private Integer status;         // 0-进行中 1-已归还
    private Integer viewCount;
    private Integer userId;         // 发布者ID
    private String userNickname;    // 发布者昵称
    private String userAvatar;      // 发布者头像
}
