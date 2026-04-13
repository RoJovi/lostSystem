package com.jovi.mapper;

import com.jovi.pojo.Location;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DormitoryMapper {

    @Select("SELECT id, building, room FROM dormitory")
    List<Location> findAll();
}
