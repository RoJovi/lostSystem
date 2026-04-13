package com.jovi.mapper;

import com.jovi.pojo.*;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface LocationMapper {

    //获取所有地点（平铺）
    @Select("SELECT id, name, parent_id FROM location ORDER BY id")
    List<Location> selectAll();

    //找名字
    @Select("SELECT id, name FROM location WHERE id = #{id}")
    Location selectById(Integer id);

    //自定义
    @Insert("INSERT INTO location (name, parent_id, is_custom, user_id, create_time) " +
            "VALUES (#{name}, #{parentId}, 1, #{userId}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Location location);

}
