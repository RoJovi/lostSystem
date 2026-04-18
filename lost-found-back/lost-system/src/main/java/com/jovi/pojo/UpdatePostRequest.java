package com.jovi.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
//用于外部帖子清单的显示
public class UpdatePostRequest {
    private String title;        // 物品名称
    private Integer locationId;  // 地点ID
    private LocalDateTime time;         // 丢失/拾到时间
    private String description;  // 描述
    private String imageUrl;

}
