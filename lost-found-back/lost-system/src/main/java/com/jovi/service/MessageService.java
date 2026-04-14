package com.jovi.service;

import com.jovi.pojo.MessageVO;

import java.util.List;

public interface MessageService {
    Integer sendMessage(Integer fromUserId, Integer toUserId, String content);
    List<MessageVO> getMessageList(Integer userId);
    Integer getUnreadCount(Integer userId);
    boolean markMessageRead(Integer messageId, Integer userId);
}
