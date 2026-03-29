package com.hubei.ich.system.controller;

import com.hubei.ich.common.result.ApiResponse;
import com.hubei.ich.common.result.PageResponse;
import com.hubei.ich.system.service.OperationLogService;
import com.hubei.ich.system.vo.OperationLogVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/logs")
@PreAuthorize("hasAuthority('SUPER_ADMIN')")
public class AdminLogController {

    private final OperationLogService operationLogService;

    public AdminLogController(OperationLogService operationLogService) {
        this.operationLogService = operationLogService;
    }

    @GetMapping
    public ApiResponse<PageResponse<OperationLogVO>> pageLogs(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") long pageNum,
            @RequestParam(defaultValue = "10") long pageSize) {
        return ApiResponse.success(operationLogService.pageLogs(keyword, pageNum, pageSize));
    }
}
