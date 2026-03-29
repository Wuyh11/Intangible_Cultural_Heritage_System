package com.hubei.ich.heritage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CategorySaveDTO {

    private Long id;

    @NotBlank(message = "分类名称不能为空")
    private String name;

    @NotBlank(message = "分类编码不能为空")
    private String code;

    @NotNull(message = "排序不能为空")
    private Integer sortOrder;

    @NotNull(message = "状态不能为空")
    private Integer status;

    private String description;
}
