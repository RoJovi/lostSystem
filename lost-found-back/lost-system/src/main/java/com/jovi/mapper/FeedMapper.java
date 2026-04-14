package com.jovi.mapper;

import com.jovi.pojo.FeedPostVO;
import com.jovi.pojo.Message;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FeedMapper {

    @Select("<script>" +
            "SELECT * FROM (" +
            "  SELECT " +
            "    id, " +
            "    0 as type, " +
            "    title, " +
            "    (SELECT nickname FROM user WHERE id = lost_item.user_id) as userNickname, " +
            "    user_id as userId, " +
            "    create_time, " +
            "    is_top as isTop, " +
            "    image_url as imageUrl " +
            "  FROM lost_item " +
            "  WHERE status = 0 " +
            "  <if test='keyword != null and keyword != \"\"'>" +
            "    AND title LIKE CONCAT('%', #{keyword}, '%')" +
            "  </if>" +
            "  <if test='locationId != null'>" +
            "    AND location_id = #{locationId}" +
            "  </if>" +
            "  UNION ALL " +
            "  SELECT " +
            "    id, " +
            "    1 as type, " +
            "    title, " +
            "    (SELECT nickname FROM user WHERE id = found_item.user_id) as userNickname, " +
            "    user_id as userId, " +
            "    create_time, " +
            "    0 as isTop, " +
            "    image_url as imageUrl " +
            "  FROM found_item " +
            "  WHERE status = 0 " +
            "  <if test='keyword != null and keyword != \"\"'>" +
            "    AND title LIKE CONCAT('%', #{keyword}, '%')" +
            "  </if>" +
            "  <if test='locationId != null'>" +
            "    AND location_id = #{locationId}" +
            "  </if>" +
            ") AS feed " +
            "<if test='type != null and type != \"all\"'>" +
            "  WHERE type = #{type}" +
            "</if>" +
            "ORDER BY create_time ${sort}" +
            "</script>")
    List<FeedPostVO> getFeedList(@Param("type") String type,
                                 @Param("keyword") String keyword,
                                 @Param("sort") String sort,
                                 @Param("locationId") Integer locationId);
}