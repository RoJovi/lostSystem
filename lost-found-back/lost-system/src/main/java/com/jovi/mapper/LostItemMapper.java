package com.jovi.mapper;

import com.jovi.pojo.FoundItem;
import com.jovi.pojo.LostItem;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
            "update_time = NOW() ,image_url = #{imageUrl} WHERE id = #{id}")
    int updatePost(@Param("id") Integer id,
                   @Param("title") String title,
                   @Param("locationId") Integer locationId,
                   @Param("locationName") String locationName,
                   @Param("time") LocalDateTime time,
                   @Param("description") String description,
                   @Param("imageUrl") String imageUrl);

    // 设置为已找回
    @Update("UPDATE lost_item SET status = 1, update_time = NOW() WHERE id = #{id}")
    int setResolved(Integer id);

    @Select("SELECT title FROM lost_item WHERE id = #{id}")
    String selectTitleById(Integer id);

    @Select("SELECT COUNT(*) FROM lost_item")
    int countAll();

    @Select("SELECT COUNT(*) FROM lost_item WHERE status = 1")
    int countResolved();

    @Select("SELECT location_name as name, COUNT(*) as count FROM lost_item GROUP BY location_name ORDER BY count DESC LIMIT 5")
    List<Map<String, Object>> getTopLocations();

    @Delete("DELETE FROM lost_item WHERE user_id = #{userId}")
    int deleteByUserId(Integer userId);

    @Update("UPDATE lost_item SET is_top = 1, top_expire_time = #{expireTime} WHERE id = #{id}")
    int setTop(@Param("id") Integer id, @Param("expireTime") LocalDateTime expireTime);

    //检查置顶过期
    @Update("UPDATE lost_item SET is_top = 0, top_expire_time = NULL " +
            "WHERE is_top = 1 AND top_expire_time < NOW()")
    int expireTopItems();
}
