package com.hubei.ich.common.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hubei.ich.common.util.SecurityUtils;
import com.hubei.ich.system.entity.SysOperationLog;
import com.hubei.ich.system.service.OperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class OperationLogAspect {

    private final OperationLogService operationLogService;
    private final ObjectMapper objectMapper;

    public OperationLogAspect(OperationLogService operationLogService, ObjectMapper objectMapper) {
        this.operationLogService = operationLogService;
        this.objectMapper = objectMapper;
    }

    @AfterReturning("@annotation(operationLog)")
    public void afterReturning(JoinPoint joinPoint, OperationLog operationLog) {
        try {
            RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
            if (!(attributes instanceof ServletRequestAttributes servletAttributes)) {
                return;
            }
            HttpServletRequest request = servletAttributes.getRequest();
            SysOperationLog logEntity = new SysOperationLog();
            logEntity.setUserId(SecurityUtils.getUserId());
            logEntity.setUsername(SecurityUtils.getUsername());
            logEntity.setModule(operationLog.module());
            logEntity.setAction(operationLog.action());
            logEntity.setHttpMethod(request.getMethod());
            logEntity.setRequestUri(request.getRequestURI());
            logEntity.setRequestIp(resolveRequestIp(request));
            logEntity.setRequestParam(serializeArgs(joinPoint.getArgs()));
            operationLogService.saveLog(logEntity);
        } catch (Exception exception) {
            log.warn("Failed to save operation log", exception);
        }
    }

    private String serializeArgs(Object[] args) throws JsonProcessingException {
        Object[] safeArgs = Arrays.stream(args)
                .filter(arg -> !(arg instanceof MultipartFile))
                .filter(arg -> !(arg instanceof HttpServletRequest))
                .filter(arg -> !(arg instanceof HttpServletResponse))
                .toArray();
        return objectMapper.writeValueAsString(safeArgs);
    }

    private String resolveRequestIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
