package com.hubei.ich.stats.controller;

import com.hubei.ich.common.result.ApiResponse;
import com.hubei.ich.heritage.dto.ProjectQueryDTO;
import com.hubei.ich.heritage.service.ProjectService;
import com.hubei.ich.stats.service.StatsService;
import com.hubei.ich.stats.vo.MapPointVO;
import com.hubei.ich.stats.vo.StatsOverviewVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
public class PublicStatsController {

    private final StatsService statsService;
    private final ProjectService projectService;

    public PublicStatsController(StatsService statsService, ProjectService projectService) {
        this.statsService = statsService;
        this.projectService = projectService;
    }

    @GetMapping("/stats/overview")
    public ApiResponse<StatsOverviewVO> getOverview() {
        return ApiResponse.success(statsService.getOverview());
    }

    @GetMapping("/map/points")
    public ApiResponse<Map<String, Object>> listMapPoints(ProjectQueryDTO dto) {
        List<MapPointVO> points = statsService.listMapPoints(dto);
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("points", points);
        payload.put("featuredProjects", projectService.listFeaturedProjects(6));
        return ApiResponse.success(payload);
    }
}
