package com.hubei.ich.heritage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hubei.ich.common.exception.BizException;
import com.hubei.ich.heritage.dto.RegionSaveDTO;
import com.hubei.ich.heritage.entity.IchRegion;
import com.hubei.ich.heritage.mapper.IchRegionMapper;
import com.hubei.ich.heritage.service.RegionService;
import com.hubei.ich.heritage.vo.RegionVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegionServiceImpl implements RegionService {

    private final IchRegionMapper ichRegionMapper;

    public RegionServiceImpl(IchRegionMapper ichRegionMapper) {
        this.ichRegionMapper = ichRegionMapper;
    }

    @Override
    public List<RegionVO> listRegions(boolean onlyEnabled) {
        LambdaQueryWrapper<IchRegion> wrapper = new LambdaQueryWrapper<IchRegion>()
                .orderByAsc(IchRegion::getSortOrder);
        if (onlyEnabled) {
            wrapper.eq(IchRegion::getStatus, 1);
        }
        return ichRegionMapper.selectList(wrapper).stream().map(region -> RegionVO.builder()
                .id(region.getId())
                .name(region.getName())
                .code(region.getCode())
                .sortOrder(region.getSortOrder())
                .longitude(region.getLongitude())
                .latitude(region.getLatitude())
                .status(region.getStatus())
                .build()).toList();
    }

    @Override
    public RegionVO saveRegion(RegionSaveDTO dto) {
        IchRegion region = dto.getId() == null ? new IchRegion() : ichRegionMapper.selectById(dto.getId());
        if (region == null) {
            throw new BizException("地区不存在");
        }
        region.setName(dto.getName());
        region.setCode(dto.getCode());
        region.setSortOrder(dto.getSortOrder());
        region.setLongitude(dto.getLongitude());
        region.setLatitude(dto.getLatitude());
        region.setStatus(dto.getStatus());
        if (dto.getId() == null) {
            ichRegionMapper.insert(region);
        } else {
            ichRegionMapper.updateById(region);
        }
        return RegionVO.builder()
                .id(region.getId())
                .name(region.getName())
                .code(region.getCode())
                .sortOrder(region.getSortOrder())
                .longitude(region.getLongitude())
                .latitude(region.getLatitude())
                .status(region.getStatus())
                .build();
    }

    @Override
    public void deleteRegion(Long id) {
        ichRegionMapper.deleteById(id);
    }
}
