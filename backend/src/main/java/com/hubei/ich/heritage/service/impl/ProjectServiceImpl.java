package com.hubei.ich.heritage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hubei.ich.common.exception.BizException;
import com.hubei.ich.common.properties.FileStorageProperties;
import com.hubei.ich.common.result.PageResponse;
import com.hubei.ich.heritage.dto.ProjectQueryDTO;
import com.hubei.ich.heritage.dto.ProjectSaveDTO;
import com.hubei.ich.heritage.entity.IchCategory;
import com.hubei.ich.heritage.entity.IchProject;
import com.hubei.ich.heritage.entity.IchProjectInheritor;
import com.hubei.ich.heritage.entity.IchProjectMedia;
import com.hubei.ich.heritage.entity.IchRegion;
import com.hubei.ich.heritage.mapper.IchCategoryMapper;
import com.hubei.ich.heritage.mapper.IchProjectInheritorMapper;
import com.hubei.ich.heritage.mapper.IchProjectMapper;
import com.hubei.ich.heritage.mapper.IchProjectMediaMapper;
import com.hubei.ich.heritage.mapper.IchRegionMapper;
import com.hubei.ich.heritage.service.InheritorService;
import com.hubei.ich.heritage.service.ProjectService;
import com.hubei.ich.heritage.vo.InheritorVO;
import com.hubei.ich.heritage.vo.ProjectDetailVO;
import com.hubei.ich.heritage.vo.ProjectListVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final IchProjectMapper ichProjectMapper;
    private final IchCategoryMapper ichCategoryMapper;
    private final IchRegionMapper ichRegionMapper;
    private final IchProjectMediaMapper ichProjectMediaMapper;
    private final IchProjectInheritorMapper ichProjectInheritorMapper;
    private final InheritorService inheritorService;
    private final FileStorageProperties fileStorageProperties;

    public ProjectServiceImpl(IchProjectMapper ichProjectMapper,
                              IchCategoryMapper ichCategoryMapper,
                              IchRegionMapper ichRegionMapper,
                              IchProjectMediaMapper ichProjectMediaMapper,
                              IchProjectInheritorMapper ichProjectInheritorMapper,
                              InheritorService inheritorService,
                              FileStorageProperties fileStorageProperties) {
        this.ichProjectMapper = ichProjectMapper;
        this.ichCategoryMapper = ichCategoryMapper;
        this.ichRegionMapper = ichRegionMapper;
        this.ichProjectMediaMapper = ichProjectMediaMapper;
        this.ichProjectInheritorMapper = ichProjectInheritorMapper;
        this.inheritorService = inheritorService;
        this.fileStorageProperties = fileStorageProperties;
    }

    @Override
    public PageResponse<ProjectListVO> pageProjects(ProjectQueryDTO dto, boolean publicOnly) {
        Page<IchProject> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<IchProject> wrapper = buildProjectQuery(dto, publicOnly)
                .orderByDesc(IchProject::getFeatured)
                .orderByDesc(IchProject::getViewCount)
                .orderByDesc(IchProject::getCreatedAt);
        Page<IchProject> result = ichProjectMapper.selectPage(page, wrapper);
        List<ProjectListVO> records = convertProjectList(result.getRecords());
        return PageResponse.<ProjectListVO>builder()
                .records(records)
                .total(result.getTotal())
                .pageNum(result.getCurrent())
                .pageSize(result.getSize())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectDetailVO getProjectDetail(Long id, boolean publicOnly) {
        IchProject project = ichProjectMapper.selectById(id);
        if (project == null || (publicOnly && project.getStatus() != 1)) {
            throw new BizException("项目不存在");
        }
        if (publicOnly) {
            project.setViewCount((project.getViewCount() == null ? 0 : project.getViewCount()) + 1);
            ichProjectMapper.updateById(project);
        }
        return buildProjectDetail(project, publicOnly);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectDetailVO saveProject(ProjectSaveDTO dto) {
        IchProject project = dto.getId() == null ? new IchProject() : ichProjectMapper.selectById(dto.getId());
        if (project == null) {
            throw new BizException("非遗项目不存在");
        }
        project.setName(dto.getName());
        project.setCategoryId(dto.getCategoryId());
        project.setRegionId(dto.getRegionId());
        project.setLevel(dto.getLevel());
        project.setBatch(dto.getBatch());
        project.setProtectionUnit(dto.getProtectionUnit());
        project.setCoverImage(dto.getCoverImage());
        project.setSummary(dto.getSummary());
        project.setContent(dto.getContent());
        project.setLongitude(dto.getLongitude());
        project.setLatitude(dto.getLatitude());
        project.setStatus(dto.getStatus());
        project.setFeatured(dto.getFeatured());
        if (project.getViewCount() == null) {
            project.setViewCount(0);
        }
        if (dto.getId() == null) {
            ichProjectMapper.insert(project);
        } else {
            ichProjectMapper.updateById(project);
            ichProjectMediaMapper.delete(new LambdaQueryWrapper<IchProjectMedia>().eq(IchProjectMedia::getProjectId, project.getId()));
            ichProjectInheritorMapper.delete(new LambdaQueryWrapper<IchProjectInheritor>().eq(IchProjectInheritor::getProjectId, project.getId()));
        }
        saveProjectMedia(project.getId(), dto.getImageUrls());
        saveProjectInheritors(project.getId(), dto.getInheritorIds());
        return buildProjectDetail(project, false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProject(Long id) {
        ichProjectMediaMapper.delete(new LambdaQueryWrapper<IchProjectMedia>().eq(IchProjectMedia::getProjectId, id));
        ichProjectInheritorMapper.delete(new LambdaQueryWrapper<IchProjectInheritor>().eq(IchProjectInheritor::getProjectId, id));
        ichProjectMapper.deleteById(id);
    }

    @Override
    public String uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BizException("请上传文件");
        }
        String originalFilename = file.getOriginalFilename() == null ? "image.jpg" : file.getOriginalFilename();
        String extension = originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf('.'))
                : ".jpg";
        String relativeDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
        String filename = UUID.randomUUID().toString().replace("-", "") + extension;
        Path uploadDir = Path.of(fileStorageProperties.getUploadDir(), relativeDir);
        try {
            Files.createDirectories(uploadDir);
            Files.copy(file.getInputStream(), uploadDir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            return fileStorageProperties.getAccessPrefix() + relativeDir + "/" + filename;
        } catch (IOException exception) {
            throw new BizException("图片上传失败");
        }
    }

    @Override
    public List<ProjectListVO> listFeaturedProjects(int limit) {
        LambdaQueryWrapper<IchProject> wrapper = new LambdaQueryWrapper<IchProject>()
                .eq(IchProject::getStatus, 1)
                .eq(IchProject::getFeatured, 1)
                .orderByDesc(IchProject::getViewCount)
                .last("LIMIT " + limit);
        return convertProjectList(ichProjectMapper.selectList(wrapper));
    }

    @Override
    public List<ProjectListVO> listAllForExport(ProjectQueryDTO dto) {
        return convertProjectList(ichProjectMapper.selectList(buildProjectQuery(dto, false)));
    }

    private void saveProjectMedia(Long projectId, List<String> imageUrls) {
        if (CollectionUtils.isEmpty(imageUrls)) {
            return;
        }
        int sort = 1;
        for (String imageUrl : imageUrls) {
            IchProjectMedia media = new IchProjectMedia();
            media.setProjectId(projectId);
            media.setMediaType("IMAGE");
            media.setMediaUrl(imageUrl);
            media.setMediaTitle("项目图片" + sort);
            media.setSortOrder(sort++);
            ichProjectMediaMapper.insert(media);
        }
    }

    private void saveProjectInheritors(Long projectId, List<Long> inheritorIds) {
        for (Long inheritorId : inheritorIds) {
            IchProjectInheritor relation = new IchProjectInheritor();
            relation.setProjectId(projectId);
            relation.setInheritorId(inheritorId);
            relation.setCreatedAt(LocalDateTime.now());
            ichProjectInheritorMapper.insert(relation);
        }
    }

    private ProjectDetailVO buildProjectDetail(IchProject project, boolean publicOnly) {
        Map<Long, String> categoryMap = ichCategoryMapper.selectList(new LambdaQueryWrapper<>()).stream()
                .collect(Collectors.toMap(IchCategory::getId, IchCategory::getName));
        Map<Long, String> regionMap = ichRegionMapper.selectList(new LambdaQueryWrapper<>()).stream()
                .collect(Collectors.toMap(IchRegion::getId, IchRegion::getName));
        List<String> imageUrls = ichProjectMediaMapper.selectList(new LambdaQueryWrapper<IchProjectMedia>()
                        .eq(IchProjectMedia::getProjectId, project.getId())
                        .orderByAsc(IchProjectMedia::getSortOrder))
                .stream()
                .map(IchProjectMedia::getMediaUrl)
                .toList();
        List<InheritorVO> inheritors = inheritorService.listByProjectId(project.getId());
        List<ProjectListVO> recommendations = convertProjectList(ichProjectMapper.selectList(new LambdaQueryWrapper<IchProject>()
                .eq(IchProject::getCategoryId, project.getCategoryId())
                .eq(publicOnly, IchProject::getStatus, 1)
                .ne(IchProject::getId, project.getId())
                .orderByDesc(IchProject::getViewCount)
                .last("LIMIT 4")));
        return ProjectDetailVO.builder()
                .id(project.getId())
                .name(project.getName())
                .categoryId(project.getCategoryId())
                .categoryName(categoryMap.get(project.getCategoryId()))
                .regionId(project.getRegionId())
                .regionName(regionMap.get(project.getRegionId()))
                .level(project.getLevel())
                .batch(project.getBatch())
                .protectionUnit(project.getProtectionUnit())
                .coverImage(project.getCoverImage())
                .summary(project.getSummary())
                .content(project.getContent())
                .longitude(project.getLongitude())
                .latitude(project.getLatitude())
                .status(project.getStatus())
                .viewCount(project.getViewCount())
                .featured(project.getFeatured())
                .imageUrls(imageUrls)
                .inheritors(inheritors)
                .recommendations(recommendations)
                .build();
    }

    private LambdaQueryWrapper<IchProject> buildProjectQuery(ProjectQueryDTO dto, boolean publicOnly) {
        LambdaQueryWrapper<IchProject> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(dto.getKeyword())) {
            wrapper.like(IchProject::getName, dto.getKeyword());
        }
        if (dto.getCategoryId() != null) {
            wrapper.eq(IchProject::getCategoryId, dto.getCategoryId());
        }
        if (dto.getRegionId() != null) {
            wrapper.eq(IchProject::getRegionId, dto.getRegionId());
        }
        if (StringUtils.hasText(dto.getLevel())) {
            wrapper.eq(IchProject::getLevel, dto.getLevel());
        }
        if (dto.getFeatured() != null) {
            wrapper.eq(IchProject::getFeatured, dto.getFeatured());
        }
        if (publicOnly) {
            wrapper.eq(IchProject::getStatus, 1);
        } else if (dto.getStatus() != null) {
            wrapper.eq(IchProject::getStatus, dto.getStatus());
        }
        return wrapper;
    }

    private List<ProjectListVO> convertProjectList(List<IchProject> projects) {
        if (projects == null || projects.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, String> categoryMap = ichCategoryMapper.selectList(new LambdaQueryWrapper<>()).stream()
                .collect(Collectors.toMap(IchCategory::getId, IchCategory::getName));
        Map<Long, String> regionMap = ichRegionMapper.selectList(new LambdaQueryWrapper<>()).stream()
                .collect(Collectors.toMap(IchRegion::getId, IchRegion::getName));
        return projects.stream()
                .sorted(Comparator.comparing(IchProject::getFeatured, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(IchProject::getViewCount, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(project -> ProjectListVO.builder()
                        .id(project.getId())
                        .name(project.getName())
                        .categoryId(project.getCategoryId())
                        .categoryName(categoryMap.get(project.getCategoryId()))
                        .regionId(project.getRegionId())
                        .regionName(regionMap.get(project.getRegionId()))
                        .level(project.getLevel())
                        .protectionUnit(project.getProtectionUnit())
                        .status(project.getStatus())
                        .statusLabel(project.getStatus() != null && project.getStatus() == 1 ? "已发布" : "已隐藏")
                        .viewCount(project.getViewCount())
                        .featured(project.getFeatured())
                        .summary(project.getSummary())
                        .coverImage(project.getCoverImage())
                        .longitude(project.getLongitude())
                        .latitude(project.getLatitude())
                        .build())
                .toList();
    }
}
