package com.jovi.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedPostVO {
    private Integer id;
    private Integer type;           // 0-失物 1-拾物
    private String title;
    private String userNickname;
    private Integer userId;
    private LocalDateTime createTime;
    private Boolean isTop;
    private String imageUrl;
}
