package com.jovi.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentVO {
    private Integer id;
    private Integer userId;
    private String userNickname;
    private String userAvatar;
    private String content;
    private LocalDateTime createTime;
}
