package com.jovi.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    private Integer id;
    private Integer userId;
    private Integer itemId; //关联lost_item或found_item
    private Integer itemType;   //0-失物 1-拾物
    private String content;
    private LocalDateTime createTime;
}
