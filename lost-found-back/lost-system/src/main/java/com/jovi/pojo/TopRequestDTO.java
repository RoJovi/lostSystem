package com.jovi.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopRequestDTO {
    private Integer id;      // 帖子ID
    private Integer type;    // 0-失物 1-拾物
    private Integer hours;   // 置顶时长：24/48/72小时
}
