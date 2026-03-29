package com.hubei.ich.heritage.dto;

import lombok.Data;

@Data
public class InheritorQueryDTO {

    private String keyword;
    private Long regionId;
    private Integer status;
    private long pageNum = 1;
    private long pageSize = 10;
}
