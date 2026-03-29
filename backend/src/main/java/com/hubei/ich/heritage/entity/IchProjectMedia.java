package com.hubei.ich.heritage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hubei.ich.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("ich_project_media")
@EqualsAndHashCode(callSuper = true)
public class IchProjectMedia extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long projectId;
    private String mediaType;
    private String mediaUrl;
    private String mediaTitle;
    private Integer sortOrder;
}
