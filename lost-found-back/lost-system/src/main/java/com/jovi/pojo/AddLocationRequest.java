package com.jovi.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddLocationRequest {
    private String name;      // 地点名称
    private Integer parentId; // 父地点ID（可选）
}
