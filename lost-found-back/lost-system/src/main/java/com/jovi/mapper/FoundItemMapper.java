package com.jovi.mapper;

import com.jovi.pojo.FoundItem;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;

@Mapper
public interface FoundItemMapper {

    //插入新帖子并获取生成的主键id赋值回去
    @Insert("INSERT INTO found_item (user_id, title, location_id, location_name, lost_time, description, image_url, create_time, update_time) " +
            "VALUES (#{userId}, #{title}, #{locationId}, #{locationName}, #{lostTime}, #{description}, #{imageUrl}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(FoundItem foundItem);


    @Select("SELECT * FROM found_item WHERE id = #{id}")
    FoundItem selectById(Integer id);

    @Update("UPDATE found_item SET view_count = view_count + 1 WHERE id = #{id}")
    void incrementViewCount(Integer id);

    @Select("SELECT user_id FROM found_item WHERE id = #{id}")
    Integer selectUserIdById(Integer id);

    @Delete("DELETE FROM found_item WHERE id = #{id}")
    int deleteById(Integer id);

    @Update("UPDATE found_item SET title = #{title}, location_id = #{locationId}, " +
            "location_name = #{locationName}, found_time = #{time}, description = #{description}, " +
            "update_time = NOW() WHERE id = #{id}")
    int updatePost(@Param("id") Integer id,
                   @Param("title") String title,
                   @Param("locationId") Integer locationId,
                   @Param("locationName") String locationName,
                   @Param("time") LocalDateTime time,
                   @Param("description") String description);

    @Update("UPDATE found_item SET status = 1, update_time = NOW() WHERE id = #{id}")
    int setResolved(Integer id);


}
