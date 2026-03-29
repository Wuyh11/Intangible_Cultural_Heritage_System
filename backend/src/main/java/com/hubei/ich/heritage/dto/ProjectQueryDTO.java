package com.hubei.ich.heritage.dto;

import lombok.Data;

@Data
public class ProjectQueryDTO {

    private String keyword;
    private Long categoryId;
    private Long regionId;
    private String level;
    private Integer status;
    private Integer featured;
    private long pageNum = 1;
    private long pageSize = 10;
}
