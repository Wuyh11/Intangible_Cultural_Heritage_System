package com.hubei.ich.system.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class OperationLogVO {

    private Long id;
    private String username;
    private String module;
    private String action;
    private String httpMethod;
    private String requestUri;
    private String requestIp;
    private String requestParam;
    private LocalDateTime createdAt;
}
