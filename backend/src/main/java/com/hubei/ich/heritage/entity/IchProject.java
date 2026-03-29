package com.hubei.ich.heritage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hubei.ich.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("ich_project")
@EqualsAndHashCode(callSuper = true)
public class IchProject extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Long categoryId;
    private Long regionId;
    private String level;
    private String batch;
    private String protectionUnit;
    private String coverImage;
    private String summary;
    private String content;
    private String longitude;
    private String latitude;
    private Integer status;
    private Integer viewCount;
    private Integer featured;
}
