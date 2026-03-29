package com.hubei.ich.heritage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class InheritorSaveDTO {

    private Long id;

    @NotBlank(message = "姓名不能为空")
    private String name;

    @NotBlank(message = "性别不能为空")
    private String gender;

    @NotNull(message = "出生年份不能为空")
    private Integer birthYear;

    @NotNull(message = "地区不能为空")
    private Long regionId;

    private String title;
    private String avatar;

    @NotBlank(message = "简介不能为空")
    private String introduction;

    private String representativeWorks;

    @NotNull(message = "状态不能为空")
    private Integer status;

    private List<Long> projectIds;
}
