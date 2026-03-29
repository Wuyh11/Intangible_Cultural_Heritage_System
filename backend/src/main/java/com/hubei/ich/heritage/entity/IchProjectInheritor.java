package com.hubei.ich.heritage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("ich_project_inheritor")
public class IchProjectInheritor {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long projectId;
    private Long inheritorId;
    private LocalDateTime createdAt;
}
