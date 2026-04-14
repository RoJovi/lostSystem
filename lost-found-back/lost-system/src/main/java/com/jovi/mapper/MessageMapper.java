package com.jovi.mapper;

import com.jovi.pojo.Comment;
import com.jovi.pojo.Message;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MessageMapper {

    // 发送私信
    @Insert("INSERT INTO message (from_user_id, to_user_id, content, is_read, create_time) " +
            "VALUES (#{fromUserId}, #{toUserId}, #{content}, 0, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Message message);

    // 获取消息列表（收件箱 + 发件箱）
    @Select("SELECT * FROM message WHERE from_user_id = #{userId} OR to_user_id = #{userId} ORDER BY create_time DESC")
    List<Message> selectByUserId(Integer userId);

    // 获取未读消息数
    @Select("SELECT COUNT(*) FROM message WHERE to_user_id = #{userId} AND is_read = 0")
    int countUnread(Integer userId);

    // 标记消息已读
    @Update("UPDATE message SET is_read = 1 WHERE id = #{id} AND to_user_id = #{userId}")
    int markAsRead(@Param("id") Integer id, @Param("userId") Integer userId);
}