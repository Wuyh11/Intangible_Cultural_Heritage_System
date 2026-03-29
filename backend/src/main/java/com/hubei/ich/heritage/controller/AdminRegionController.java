package com.hubei.ich.heritage.controller;

import com.hubei.ich.common.aspect.OperationLog;
import com.hubei.ich.common.result.ApiResponse;
import com.hubei.ich.heritage.dto.RegionSaveDTO;
import com.hubei.ich.heritage.service.RegionService;
import com.hubei.ich.heritage.vo.RegionVO;
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

import java.util.List;

@RestController
@RequestMapping("/api/admin/regions")
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN','EDITOR')")
public class AdminRegionController {

    private final RegionService regionService;

    public AdminRegionController(RegionService regionService) {
        this.regionService = regionService;
    }

    @GetMapping
    public ApiResponse<List<RegionVO>> listRegions() {
        return ApiResponse.success(regionService.listRegions(false));
    }

    @PostMapping
    @OperationLog(module = "基础数据", action = "新增地区")
    public ApiResponse<RegionVO> createRegion(@Valid @RequestBody RegionSaveDTO dto) {
        return ApiResponse.success(regionService.saveRegion(dto));
    }

    @PutMapping("/{id}")
    @OperationLog(module = "基础数据", action = "编辑地区")
    public ApiResponse<RegionVO> updateRegion(@PathVariable Long id, @Valid @RequestBody RegionSaveDTO dto) {
        dto.setId(id);
        return ApiResponse.success(regionService.saveRegion(dto));
    }

    @DeleteMapping("/{id}")
    @OperationLog(module = "基础数据", action = "删除地区")
    public ApiResponse<Void> deleteRegion(@PathVariable Long id) {
        regionService.deleteRegion(id);
        return ApiResponse.success();
    }
}
