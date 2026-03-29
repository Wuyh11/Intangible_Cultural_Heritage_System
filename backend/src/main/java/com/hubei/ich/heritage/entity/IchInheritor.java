package com.hubei.ich.heritage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hubei.ich.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("ich_inheritor")
@EqualsAndHashCode(callSuper = true)
public class IchInheritor extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String gender;
    private Integer birthYear;
    private Long regionId;
    private String title;
    private String avatar;
    private String introduction;
    private String representativeWorks;
    private Integer status;
}
