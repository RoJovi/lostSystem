package com.jovi.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoundDTO {
    private String title;
    private Integer locationId;
    private LocalDateTime Time;
    private String description;
    private String imageUrl;    //图片
}
