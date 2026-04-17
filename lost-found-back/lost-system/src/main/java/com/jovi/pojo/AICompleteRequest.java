package com.jovi.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AICompleteRequest {
    private Integer type;        // 0-失物 1-拾物
    private String title;        // 物品名称
    private String description;  // 已有描述（可选）
}
