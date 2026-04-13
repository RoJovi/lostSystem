package com.jovi.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LostItem {
    private Integer id;
    private Integer userId;
    private String title;
    private Integer locationId;
    private String locationName;
    private LocalDateTime lostTime;
    private String description;
    private String contactVerify;   //核验字段（仅发布者+管理员可见）
    private String imageUrl;    //图片
    private Integer isTop;  //是否置顶：0-否 1-是
    private LocalDateTime topExpireTime;    //置顶过期时间
    private Integer status; //状态：0-进行中 1-已找回 2-已关闭
    private Integer viewCount;  //浏览次数
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
