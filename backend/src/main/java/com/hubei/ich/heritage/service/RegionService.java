package com.hubei.ich.heritage.service;

import com.hubei.ich.heritage.dto.RegionSaveDTO;
import com.hubei.ich.heritage.vo.RegionVO;

import java.util.List;

public interface RegionService {

    List<RegionVO> listRegions(boolean onlyEnabled);

    RegionVO saveRegion(RegionSaveDTO dto);

    void deleteRegion(Long id);
}
