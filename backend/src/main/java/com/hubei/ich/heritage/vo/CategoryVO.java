package com.hubei.ich.heritage.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryVO {

    private Long id;
    private String name;
    private String code;
    private Integer sortOrder;
    private Integer status;
    private String description;
}
