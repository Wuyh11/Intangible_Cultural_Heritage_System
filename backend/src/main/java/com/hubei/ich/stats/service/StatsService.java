package com.hubei.ich.stats.service;

import com.hubei.ich.heritage.dto.ProjectQueryDTO;
import com.hubei.ich.stats.vo.MapPointVO;
import com.hubei.ich.stats.vo.StatsOverviewVO;

import java.util.List;

public interface StatsService {

    StatsOverviewVO getOverview();

    List<MapPointVO> listMapPoints(ProjectQueryDTO dto);
}
