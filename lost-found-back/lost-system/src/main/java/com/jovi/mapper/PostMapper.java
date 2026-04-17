package com.jovi.mapper;

import com.jovi.pojo.Post;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface PostMapper {
    //用xml映射
    List<Post> SelectPostByUserId(Integer userId);
}
