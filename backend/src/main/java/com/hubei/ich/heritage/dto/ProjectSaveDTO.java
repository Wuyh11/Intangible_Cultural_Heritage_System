package com.hubei.ich.heritage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ProjectSaveDTO {

    private Long id;

    @NotBlank(message = "项目名称不能为空")
    private String name;

    @NotNull(message = "分类不能为空")
    private Long categoryId;

    @NotNull(message = "地区不能为空")
    private Long regionId;

    @NotBlank(message = "级别不能为空")
    private String level;

    private String batch;
    private String protectionUnit;
    private String coverImage;

    @NotBlank(message = "项目简介不能为空")
    private String summary;

    @NotBlank(message = "项目详情不能为空")
    private String content;

    private String longitude;
    private String latitude;

    @NotNull(message = "状态不能为空")
    private Integer status;

    @NotNull(message = "是否推荐不能为空")
    private Integer featured;

    @NotEmpty(message = "请至少关联一位传承人")
    private List<Long> inheritorIds;

    private List<String> imageUrls;
}
