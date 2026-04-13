package com.jovi.mapper;

import com.jovi.pojo.Post;
import com.jovi.pojo.TopRequest;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface TopRequestMapper {

    // 插入置顶申请
    @Insert("INSERT INTO top_request (item_id, item_type, requester_id, duration_hours, status, create_time) " +
            "VALUES (#{itemId}, #{itemType}, #{requesterId}, #{durationHours}, 0, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TopRequest topRequest);

    // 检查帖子是否存在
    @Select("SELECT COUNT(*) FROM lost_item WHERE id = #{id} AND status = 0")
    int checkLostItemExists(Integer id);

    @Select("SELECT COUNT(*) FROM found_item WHERE id = #{id} AND status = 0")
    int checkFoundItemExists(Integer id);

    // 检查是否已有待审批的置顶申请
    @Select("SELECT COUNT(*) FROM top_request WHERE item_id = #{itemId} AND item_type = #{itemType} AND status = 0")
    int checkPendingRequest(@Param("itemId") Integer itemId, @Param("itemType") Integer itemType);
}
