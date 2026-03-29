package com.hubei.ich.heritage.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegionVO {

    private Long id;
    private String name;
    private String code;
    private Integer sortOrder;
    private String longitude;
    private String latitude;
    private Integer status;
}
