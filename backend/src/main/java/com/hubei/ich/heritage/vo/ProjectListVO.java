package com.hubei.ich.heritage.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectListVO {

    @ExcelProperty("项目名称")
    private String name;
    @ExcelProperty("类别")
    private String categoryName;
    @ExcelProperty("地区")
    private String regionName;
    @ExcelProperty("级别")
    private String level;
    @ExcelProperty("保护单位")
    private String protectionUnit;
    @ExcelProperty("状态")
    private String statusLabel;

    private Long id;
    private Long categoryId;
    private Long regionId;
    private Integer status;
    private Integer viewCount;
    private Integer featured;
    private String summary;
    private String coverImage;
    private String longitude;
    private String latitude;
}
