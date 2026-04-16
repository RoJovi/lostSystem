package com.jovi.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageVO {
    private Integer id;
    private String type;           // "private" 或 "comment"
    private Integer fromUserId;
    private Integer toUserId;
    private String fromUserNickname;
    private String fromUserAvatar;
    private String content;
    private Boolean isRead;
    private LocalDateTime createTime;
    // 评论类型特有字段
    private Integer postId;
    private Integer postType;
    private String postTitle;
}
