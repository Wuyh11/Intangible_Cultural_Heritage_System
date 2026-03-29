package com.hubei.ich.stats.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChartItemVO {

    @ExcelProperty("名称")
    private String name;

    @ExcelProperty("数量")
    private Long value;
}
