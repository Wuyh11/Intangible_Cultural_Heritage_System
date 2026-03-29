package com.hubei.ich.stats.controller;

import com.alibaba.excel.EasyExcel;
import com.hubei.ich.common.result.ApiResponse;
import com.hubei.ich.heritage.dto.ProjectQueryDTO;
import com.hubei.ich.heritage.service.ProjectService;
import com.hubei.ich.heritage.vo.ProjectListVO;
import com.hubei.ich.stats.service.StatsService;
import com.hubei.ich.stats.vo.ChartItemVO;
import com.hubei.ich.stats.vo.StatsOverviewVO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN','EDITOR')")
public class AdminStatsController {

    private final StatsService statsService;
    private final ProjectService projectService;

    public AdminStatsController(StatsService statsService, ProjectService projectService) {
        this.statsService = statsService;
        this.projectService = projectService;
    }

    @GetMapping("/stats/overview")
    public ApiResponse<StatsOverviewVO> getOverview() {
        return ApiResponse.success(statsService.getOverview());
    }

    @GetMapping("/export/projects")
    public void exportProjects(ProjectQueryDTO dto, HttpServletResponse response) throws IOException {
        List<ProjectListVO> rows = projectService.listAllForExport(dto);
        exportExcel(response, "hubei-ich-projects.xlsx", ProjectListVO.class, rows, "项目数据");
    }

    @GetMapping("/export/stats")
    public void exportStats(HttpServletResponse response) throws IOException {
        StatsOverviewVO overview = statsService.getOverview();
        List<ChartItemVO> exportRows = new ArrayList<>();
        exportRows.add(new ChartItemVO("项目总数", overview.getProjectCount()));
        exportRows.add(new ChartItemVO("传承人总数", overview.getInheritorCount()));
        exportRows.add(new ChartItemVO("地区总数", overview.getRegionCount()));
        exportRows.add(new ChartItemVO("分类总数", overview.getCategoryCount()));
        exportRows.addAll(overview.getLevelDistribution());
        exportExcel(response, "hubei-ich-stats.xlsx", ChartItemVO.class, exportRows, "统计汇总");
    }

    private <T> void exportExcel(HttpServletResponse response,
                                 String fileName,
                                 Class<T> clazz,
                                 List<T> rows,
                                 String sheetName) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" +
                URLEncoder.encode(fileName, StandardCharsets.UTF_8));
        EasyExcel.write(response.getOutputStream(), clazz)
                .sheet(sheetName)
                .doWrite(rows);
    }
}
