package com.jovi.mapper;

import com.jovi.pojo.Comment;
import com.jovi.pojo.Location;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentMapper {

    // 获取评论列表
    @Select("SELECT id, user_id, content, create_time FROM comment " +
            "WHERE item_id = #{itemId} AND item_type = #{itemType} " +
            "ORDER BY create_time DESC")
    List<Comment> selectByItem(@Param("itemId") Integer itemId,
                               @Param("itemType") Integer itemType);

    // 插入评论
    @Insert("INSERT INTO comment (user_id, item_id, item_type, content, create_time) " +
            "VALUES (#{userId}, #{itemId}, #{itemType}, #{content}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Comment comment);

    // 删除评论（可选，用于删除帖子时级联删除）
    @Delete("DELETE FROM comment WHERE item_id = #{itemId} AND item_type = #{itemType}")
    int deleteByItem(@Param("itemId") Integer itemId,
                     @Param("itemType") Integer itemType);

    @Delete("DELETE FROM comment WHERE user_id = #{userId}")
    int deleteByUserId(Integer userId);
}