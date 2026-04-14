package com.jovi.mapper;

import com.jovi.pojo.*;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface LocationMapper {

    // 查询所有地点（包含 parent_id，用于构建树形）
    @Select("SELECT id, name, parent_id FROM location ORDER BY parent_id, id")
    List<Location> selectAll();

    // 根据ID查询地点
    @Select("SELECT id, name, parent_id FROM location WHERE id = #{id}")
    Location selectById(Integer id);

    // 插入自定义地点
    @Insert("INSERT INTO location (name, parent_id, is_custom, user_id, create_time) " +
            "VALUES (#{name}, #{parentId}, 1, #{userId}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Location location);

    // 增加使用次数（用于热门排序）
    @Update("UPDATE location SET use_count = use_count + 1 WHERE id = #{id}")
    int incrementUseCount(Integer id);
}
