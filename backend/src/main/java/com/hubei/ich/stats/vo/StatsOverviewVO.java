package com.hubei.ich.stats.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StatsOverviewVO {

    private long projectCount;
    private long inheritorCount;
    private long regionCount;
    private long categoryCount;
    private List<ChartItemVO> levelDistribution;
    private List<ChartItemVO> categoryDistribution;
    private List<ChartItemVO> regionDistribution;
    private List<ChartItemVO> hotProjects;
}
