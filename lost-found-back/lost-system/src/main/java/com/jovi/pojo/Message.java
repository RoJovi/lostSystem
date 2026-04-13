package com.jovi.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private Integer id;
    private Integer fromUserId; //谁发的
    private Integer toUserId;   //谁收到的
    private String content;
    private Integer isRead;     //0-未读 1-已读
    private LocalDateTime createTime;
}
