package com.hubei.ich.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hubei.ich.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("sys_operation_log")
@EqualsAndHashCode(callSuper = true)
public class SysOperationLog extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String username;
    private String module;
    private String action;
    private String httpMethod;
    private String requestUri;
    private String requestIp;
    private String requestParam;
}
