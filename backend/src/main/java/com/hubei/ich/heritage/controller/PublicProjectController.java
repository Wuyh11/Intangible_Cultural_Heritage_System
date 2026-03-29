package com.hubei.ich.heritage.controller;

import com.hubei.ich.common.result.ApiResponse;
import com.hubei.ich.common.result.PageResponse;
import com.hubei.ich.heritage.dto.ProjectQueryDTO;
import com.hubei.ich.heritage.service.CategoryService;
import com.hubei.ich.heritage.service.ProjectService;
import com.hubei.ich.heritage.service.RegionService;
import com.hubei.ich.heritage.vo.ProjectDetailVO;
import com.hubei.ich.heritage.vo.ProjectListVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/public/projects")
public class PublicProjectController {

    private final ProjectService projectService;
    private final CategoryService categoryService;
    private final RegionService regionService;

    public PublicProjectController(ProjectService projectService,
                                   CategoryService categoryService,
                                   RegionService regionService) {
        this.projectService = projectService;
        this.categoryService = categoryService;
        this.regionService = regionService;
    }

    @GetMapping
    public ApiResponse<PageResponse<ProjectListVO>> pageProjects(ProjectQueryDTO dto) {
        return ApiResponse.success(projectService.pageProjects(dto, true));
    }

    @GetMapping("/{id}")
    public ApiResponse<ProjectDetailVO> getProjectDetail(@PathVariable Long id) {
        return ApiResponse.success(projectService.getProjectDetail(id, true));
    }

    @GetMapping("/filters")
    public ApiResponse<Map<String, Object>> getFilters() {
        Map<String, Object> filters = new LinkedHashMap<>();
        filters.put("categories", categoryService.listCategories(true));
        filters.put("regions", regionService.listRegions(true));
        filters.put("levels", java.util.List.of("国家级", "省级", "市级", "县级"));
        return ApiResponse.success(filters);
    }
}
