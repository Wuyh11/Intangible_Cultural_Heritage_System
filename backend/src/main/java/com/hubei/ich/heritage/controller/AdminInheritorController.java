package com.hubei.ich.heritage.controller;

import com.hubei.ich.common.aspect.OperationLog;
import com.hubei.ich.common.result.ApiResponse;
import com.hubei.ich.common.result.PageResponse;
import com.hubei.ich.heritage.dto.InheritorQueryDTO;
import com.hubei.ich.heritage.dto.InheritorSaveDTO;
import com.hubei.ich.heritage.service.InheritorService;
import com.hubei.ich.heritage.vo.InheritorVO;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/inheritors")
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN','EDITOR')")
public class AdminInheritorController {

    private final InheritorService inheritorService;

    public AdminInheritorController(InheritorService inheritorService) {
        this.inheritorService = inheritorService;
    }

    @GetMapping
    public ApiResponse<PageResponse<InheritorVO>> pageInheritors(InheritorQueryDTO dto) {
        return ApiResponse.success(inheritorService.pageInheritors(dto));
    }

    @PostMapping
    @OperationLog(module = "传承人", action = "新增传承人")
    public ApiResponse<InheritorVO> createInheritor(@Valid @RequestBody InheritorSaveDTO dto) {
        return ApiResponse.success(inheritorService.saveInheritor(dto));
    }

    @PutMapping("/{id}")
    @OperationLog(module = "传承人", action = "编辑传承人")
    public ApiResponse<InheritorVO> updateInheritor(@PathVariable Long id, @Valid @RequestBody InheritorSaveDTO dto) {
        dto.setId(id);
        return ApiResponse.success(inheritorService.saveInheritor(dto));
    }

    @DeleteMapping("/{id}")
    @OperationLog(module = "传承人", action = "删除传承人")
    public ApiResponse<Void> deleteInheritor(@PathVariable Long id) {
        inheritorService.deleteInheritor(id);
        return ApiResponse.success();
    }
}
