package com.jovi.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
//用于外部帖子清单的显示
public class Post {
    private Integer id;
    private Integer type;       // 0-失物 1-拾物
    private String title;
    private Integer status;     // 0-进行中 1-已找回/已归还
    private Boolean isTop;      // 是否置顶（拾物默认为false）
    private Integer viewCount;  //浏览次数
    private LocalDateTime createTime;   //发帖日期
    private String imageUrl;     //显示图片

}
