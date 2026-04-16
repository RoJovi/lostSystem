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

    // @Select("SELECT * FROM comment WHERE user_id != #{userId} ORDER BY create_time DESC")
// List<Comment> selectByUserId(Integer userId);

    // 新增正确的方法：查询别人评论我的帖子的通知
    @Select("SELECT c.* FROM comment c " +
            "LEFT JOIN lost_item l ON c.item_id = l.id AND c.item_type = 0 " +
            "LEFT JOIN found_item f ON c.item_id = f.id AND c.item_type = 1 " +
            "WHERE (l.user_id = #{userId} OR f.user_id = #{userId}) " +
            "AND c.user_id != #{userId} " +
            "ORDER BY c.create_time DESC")
    List<Comment> selectCommentsOnMyPosts(Integer userId);
}