package com.hubei.ich.heritage.service;

import com.hubei.ich.common.result.PageResponse;
import com.hubei.ich.heritage.dto.ProjectQueryDTO;
import com.hubei.ich.heritage.dto.ProjectSaveDTO;
import com.hubei.ich.heritage.vo.ProjectDetailVO;
import com.hubei.ich.heritage.vo.ProjectListVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProjectService {

    PageResponse<ProjectListVO> pageProjects(ProjectQueryDTO dto, boolean publicOnly);

    ProjectDetailVO getProjectDetail(Long id, boolean publicOnly);

    ProjectDetailVO saveProject(ProjectSaveDTO dto);

    void deleteProject(Long id);

    String uploadImage(MultipartFile file);

    List<ProjectListVO> listFeaturedProjects(int limit);

    List<ProjectListVO> listAllForExport(ProjectQueryDTO dto);
}
