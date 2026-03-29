package com.hubei.ich.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hubei.ich.common.result.PageResponse;
import com.hubei.ich.system.entity.SysOperationLog;
import com.hubei.ich.system.mapper.SysOperationLogMapper;
import com.hubei.ich.system.service.OperationLogService;
import com.hubei.ich.system.vo.OperationLogVO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class OperationLogServiceImpl implements OperationLogService {

    private final SysOperationLogMapper sysOperationLogMapper;

    public OperationLogServiceImpl(SysOperationLogMapper sysOperationLogMapper) {
        this.sysOperationLogMapper = sysOperationLogMapper;
    }

    @Override
    public void saveLog(SysOperationLog logEntity) {
        sysOperationLogMapper.insert(logEntity);
    }

    @Override
    public PageResponse<OperationLogVO> pageLogs(String keyword, long pageNum, long pageSize) {
        Page<SysOperationLog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysOperationLog> wrapper = new LambdaQueryWrapper<SysOperationLog>()
                .orderByDesc(SysOperationLog::getCreatedAt);
        if (StringUtils.hasText(keyword)) {
            wrapper.and(q -> q.like(SysOperationLog::getUsername, keyword)
                    .or()
                    .like(SysOperationLog::getModule, keyword)
                    .or()
                    .like(SysOperationLog::getAction, keyword));
        }
        Page<SysOperationLog> result = sysOperationLogMapper.selectPage(page, wrapper);
        return PageResponse.<OperationLogVO>builder()
                .records(result.getRecords().stream().map(log -> OperationLogVO.builder()
                        .id(log.getId())
                        .username(log.getUsername())
                        .module(log.getModule())
                        .action(log.getAction())
                        .httpMethod(log.getHttpMethod())
                        .requestUri(log.getRequestUri())
                        .requestIp(log.getRequestIp())
                        .requestParam(log.getRequestParam())
                        .createdAt(log.getCreatedAt())
                        .build()).toList())
                .total(result.getTotal())
                .pageNum(result.getCurrent())
                .pageSize(result.getSize())
                .build();
    }
}
