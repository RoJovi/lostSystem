package com.jovi.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    private Integer id;
    private String name;
    private Integer parentId;  //父级ID，0表示顶级
    private Integer isCustom;  //0-系统预置 1-用户自定义
    private Integer userId;     //自定义地点的创建者ID（is_custom=1时有效）
    private Integer useCount;   //被使用的次数（用于热门排序?）
    private LocalDateTime createTime;
}
