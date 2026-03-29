package com.hubei.ich.heritage.controller;

import com.hubei.ich.common.aspect.OperationLog;
import com.hubei.ich.common.result.ApiResponse;
import com.hubei.ich.common.result.PageResponse;
import com.hubei.ich.heritage.dto.ProjectQueryDTO;
import com.hubei.ich.heritage.dto.ProjectSaveDTO;
import com.hubei.ich.heritage.service.ProjectService;
import com.hubei.ich.heritage.vo.ProjectDetailVO;
import com.hubei.ich.heritage.vo.ProjectListVO;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/projects")
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN','EDITOR')")
public class AdminProjectController {

    private final ProjectService projectService;

    public AdminProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public ApiResponse<PageResponse<ProjectListVO>> pageProjects(ProjectQueryDTO dto) {
        return ApiResponse.success(projectService.pageProjects(dto, false));
    }

    @GetMapping("/{id}")
    public ApiResponse<ProjectDetailVO> getProjectDetail(@PathVariable Long id) {
        return ApiResponse.success(projectService.getProjectDetail(id, false));
    }

    @PostMapping
    @OperationLog(module = "非遗项目", action = "新增项目")
    public ApiResponse<ProjectDetailVO> createProject(@Valid @RequestBody ProjectSaveDTO dto) {
        return ApiResponse.success(projectService.saveProject(dto));
    }

    @PutMapping("/{id}")
    @OperationLog(module = "非遗项目", action = "编辑项目")
    public ApiResponse<ProjectDetailVO> updateProject(@PathVariable Long id, @Valid @RequestBody ProjectSaveDTO dto) {
        dto.setId(id);
        return ApiResponse.success(projectService.saveProject(dto));
    }

    @DeleteMapping("/{id}")
    @OperationLog(module = "非遗项目", action = "删除项目")
    public ApiResponse<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ApiResponse.success();
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @OperationLog(module = "非遗项目", action = "上传图片")
    public ApiResponse<Map<String, String>> upload(@RequestPart("file") MultipartFile file) {
        return ApiResponse.success(Map.of("url", projectService.uploadImage(file)));
    }
}
