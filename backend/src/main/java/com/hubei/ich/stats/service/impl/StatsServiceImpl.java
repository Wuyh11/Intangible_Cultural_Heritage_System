package com.hubei.ich.stats.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hubei.ich.common.constant.SecurityConstants;
import com.hubei.ich.heritage.dto.ProjectQueryDTO;
import com.hubei.ich.heritage.entity.IchCategory;
import com.hubei.ich.heritage.entity.IchInheritor;
import com.hubei.ich.heritage.entity.IchProject;
import com.hubei.ich.heritage.entity.IchRegion;
import com.hubei.ich.heritage.mapper.IchCategoryMapper;
import com.hubei.ich.heritage.mapper.IchInheritorMapper;
import com.hubei.ich.heritage.mapper.IchProjectMapper;
import com.hubei.ich.heritage.mapper.IchRegionMapper;
import com.hubei.ich.stats.service.StatsService;
import com.hubei.ich.stats.vo.ChartItemVO;
import com.hubei.ich.stats.vo.MapPointVO;
import com.hubei.ich.stats.vo.StatsOverviewVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatsServiceImpl implements StatsService {

    private final IchProjectMapper ichProjectMapper;
    private final IchInheritorMapper ichInheritorMapper;
    private final IchRegionMapper ichRegionMapper;
    private final IchCategoryMapper ichCategoryMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    public StatsServiceImpl(IchProjectMapper ichProjectMapper,
                            IchInheritorMapper ichInheritorMapper,
                            IchRegionMapper ichRegionMapper,
                            IchCategoryMapper ichCategoryMapper,
                            StringRedisTemplate stringRedisTemplate,
                            ObjectMapper objectMapper) {
        this.ichProjectMapper = ichProjectMapper;
        this.ichInheritorMapper = ichInheritorMapper;
        this.ichRegionMapper = ichRegionMapper;
        this.ichCategoryMapper = ichCategoryMapper;
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public StatsOverviewVO getOverview() {
        String cacheKey = SecurityConstants.REDIS_STATS_PREFIX + "overview";
        try {
            String cacheValue = stringRedisTemplate.opsForValue().get(cacheKey);
            if (StringUtils.hasText(cacheValue)) {
                return objectMapper.readValue(cacheValue, StatsOverviewVO.class);
            }
        } catch (Exception ignored) {
        }
        List<IchProject> publishedProjects = ichProjectMapper.selectList(new LambdaQueryWrapper<IchProject>()
                .eq(IchProject::getStatus, 1));
        List<IchInheritor> inheritors = ichInheritorMapper.selectList(new LambdaQueryWrapper<IchInheritor>()
                .eq(IchInheritor::getStatus, 1));
        Map<Long, String> regionMap = ichRegionMapper.selectList(new LambdaQueryWrapper<>()).stream()
                .collect(Collectors.toMap(IchRegion::getId, IchRegion::getName));
        Map<Long, String> categoryMap = ichCategoryMapper.selectList(new LambdaQueryWrapper<>()).stream()
                .collect(Collectors.toMap(IchCategory::getId, IchCategory::getName));
        StatsOverviewVO overview = StatsOverviewVO.builder()
                .projectCount(publishedProjects.size())
                .inheritorCount(inheritors.size())
                .regionCount(regionMap.size())
                .categoryCount(categoryMap.size())
                .levelDistribution(groupByName(publishedProjects.stream()
                        .collect(Collectors.groupingBy(IchProject::getLevel, Collectors.counting()))))
                .categoryDistribution(groupByName(publishedProjects.stream()
                        .collect(Collectors.groupingBy(project -> categoryMap.get(project.getCategoryId()), Collectors.counting()))))
                .regionDistribution(groupByName(publishedProjects.stream()
                        .collect(Collectors.groupingBy(project -> regionMap.get(project.getRegionId()), Collectors.counting()))))
                .hotProjects(publishedProjects.stream()
                        .sorted(Comparator.comparing(IchProject::getViewCount, Comparator.nullsLast(Comparator.reverseOrder())))
                        .limit(8)
                        .map(project -> new ChartItemVO(project.getName(), project.getViewCount() == null ? 0L : project.getViewCount().longValue()))
                        .toList())
                .build();
        try {
            stringRedisTemplate.opsForValue().set(cacheKey, objectMapper.writeValueAsString(overview), Duration.ofMinutes(30));
        } catch (JsonProcessingException ignored) {
        } catch (Exception ignored) {
        }
        return overview;
    }

    @Override
    public List<MapPointVO> listMapPoints(ProjectQueryDTO dto) {
        List<IchProject> projects = ichProjectMapper.selectList(new LambdaQueryWrapper<IchProject>()
                .eq(IchProject::getStatus, 1)
                .like(StringUtils.hasText(dto.getKeyword()), IchProject::getName, dto.getKeyword())
                .eq(dto.getCategoryId() != null, IchProject::getCategoryId, dto.getCategoryId())
                .eq(dto.getRegionId() != null, IchProject::getRegionId, dto.getRegionId())
                .eq(StringUtils.hasText(dto.getLevel()), IchProject::getLevel, dto.getLevel()));
        if (projects.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, String> regionMap = ichRegionMapper.selectBatchIds(projects.stream().map(IchProject::getRegionId).distinct().toList())
                .stream()
                .collect(Collectors.toMap(IchRegion::getId, IchRegion::getName));
        Map<Long, String> categoryMap = ichCategoryMapper.selectBatchIds(projects.stream().map(IchProject::getCategoryId).distinct().toList())
                .stream()
                .collect(Collectors.toMap(IchCategory::getId, IchCategory::getName));
        return projects.stream()
                .filter(project -> StringUtils.hasText(project.getLongitude()) && StringUtils.hasText(project.getLatitude()))
                .map(project -> MapPointVO.builder()
                        .projectId(project.getId())
                        .projectName(project.getName())
                        .categoryId(project.getCategoryId())
                        .categoryName(categoryMap.get(project.getCategoryId()))
                        .regionId(project.getRegionId())
                        .regionName(regionMap.get(project.getRegionId()))
                        .level(project.getLevel())
                        .longitude(project.getLongitude())
                        .latitude(project.getLatitude())
                        .viewCount(project.getViewCount())
                        .build())
                .toList();
    }

    private List<ChartItemVO> groupByName(Map<String, Long> source) {
        return source.entrySet().stream()
                .filter(entry -> entry.getKey() != null)
                .map(entry -> new ChartItemVO(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(ChartItemVO::getValue).reversed())
                .toList();
    }
}
