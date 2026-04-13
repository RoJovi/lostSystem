package com.jovi.mapper;

import com.jovi.pojo.FoundItem;
import com.jovi.pojo.LostItem;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;

@Mapper
public interface LostItemMapper {

    // 插入新帖子并获取生成的主键id
    @Insert("INSERT INTO lost_item (user_id, title, location_id, location_name, lost_time, description, image_url, create_time, update_time) " +
            "VALUES (#{userId}, #{title}, #{locationId}, #{locationName}, #{lostTime}, #{description}, #{imageUrl}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(LostItem lostItem);

    // 根据ID查询
    @Select("SELECT * FROM lost_item WHERE id = #{id}")
    LostItem selectById(Integer id);

    // 增加浏览次数
    @Update("UPDATE lost_item SET view_count = view_count + 1 WHERE id = #{id}")
    void incrementViewCount(Integer id);

    // 获取帖子所属用户ID
    @Select("SELECT user_id FROM lost_item WHERE id = #{id}")
    Integer selectUserIdById(Integer id);

    // 删除帖子
    @Delete("DELETE FROM lost_item WHERE id = #{id}")
    int deleteById(Integer id);

    // 更新帖子
    @Update("UPDATE lost_item SET title = #{title}, location_id = #{locationId}, " +
            "location_name = #{locationName}, lost_time = #{time}, description = #{description}, " +
            "update_time = NOW() WHERE id = #{id}")
    int updatePost(@Param("id") Integer id,
                   @Param("title") String title,
                   @Param("locationId") Integer locationId,
                   @Param("locationName") String locationName,
                   @Param("time") LocalDateTime time,
                   @Param("description") String description);

    // 设置为已找回
    @Update("UPDATE lost_item SET status = 1, update_time = NOW() WHERE id = #{id}")
    int setResolved(Integer id);
}
