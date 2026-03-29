package com.hubei.ich.heritage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegionSaveDTO {

    private Long id;

    @NotBlank(message = "地区名称不能为空")
    private String name;

    @NotBlank(message = "地区编码不能为空")
    private String code;

    @NotNull(message = "排序不能为空")
    private Integer sortOrder;

    private String longitude;
    private String latitude;

    @NotNull(message = "状态不能为空")
    private Integer status;
}
