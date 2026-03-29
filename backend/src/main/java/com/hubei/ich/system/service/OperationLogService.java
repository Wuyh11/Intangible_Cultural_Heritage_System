package com.hubei.ich.system.service;

import com.hubei.ich.common.result.PageResponse;
import com.hubei.ich.system.entity.SysOperationLog;
import com.hubei.ich.system.vo.OperationLogVO;

public interface OperationLogService {

    void saveLog(SysOperationLog logEntity);

    PageResponse<OperationLogVO> pageLogs(String keyword, long pageNum, long pageSize);
}
