package com.hubei.ich.heritage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hubei.ich.common.exception.BizException;
import com.hubei.ich.common.result.PageResponse;
import com.hubei.ich.heritage.dto.InheritorQueryDTO;
import com.hubei.ich.heritage.dto.InheritorSaveDTO;
import com.hubei.ich.heritage.entity.IchInheritor;
import com.hubei.ich.heritage.entity.IchProjectInheritor;
import com.hubei.ich.heritage.entity.IchRegion;
import com.hubei.ich.heritage.mapper.IchInheritorMapper;
import com.hubei.ich.heritage.mapper.IchProjectInheritorMapper;
import com.hubei.ich.heritage.mapper.IchRegionMapper;
import com.hubei.ich.heritage.service.InheritorService;
import com.hubei.ich.heritage.vo.InheritorVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InheritorServiceImpl implements InheritorService {

    private final IchInheritorMapper ichInheritorMapper;
    private final IchRegionMapper ichRegionMapper;
    private final IchProjectInheritorMapper ichProjectInheritorMapper;

    public InheritorServiceImpl(IchInheritorMapper ichInheritorMapper,
                                IchRegionMapper ichRegionMapper,
                                IchProjectInheritorMapper ichProjectInheritorMapper) {
        this.ichInheritorMapper = ichInheritorMapper;
        this.ichRegionMapper = ichRegionMapper;
        this.ichProjectInheritorMapper = ichProjectInheritorMapper;
    }

    @Override
    public PageResponse<InheritorVO> pageInheritors(InheritorQueryDTO dto) {
        LambdaQueryWrapper<IchInheritor> wrapper = new LambdaQueryWrapper<IchInheritor>()
                .orderByDesc(IchInheritor::getCreatedAt);
        if (StringUtils.hasText(dto.getKeyword())) {
            wrapper.and(q -> q.like(IchInheritor::getName, dto.getKeyword())
                    .or()
                    .like(IchInheritor::getTitle, dto.getKeyword()));
        }
        if (dto.getRegionId() != null) {
            wrapper.eq(IchInheritor::getRegionId, dto.getRegionId());
        }
        if (dto.getStatus() != null) {
            wrapper.eq(IchInheritor::getStatus, dto.getStatus());
        }
        Page<IchInheritor> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        Page<IchInheritor> result = ichInheritorMapper.selectPage(page, wrapper);
        Map<Long, String> regionMap = ichRegionMapper.selectList(new LambdaQueryWrapper<>()).stream()
                .collect(Collectors.toMap(IchRegion::getId, IchRegion::getName));
        List<InheritorVO> records = result.getRecords().stream()
                .map(inheritor -> toVO(inheritor, regionMap))
                .toList();
        return PageResponse.<InheritorVO>builder()
                .records(records)
                .total(result.getTotal())
                .pageNum(result.getCurrent())
                .pageSize(result.getSize())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InheritorVO saveInheritor(InheritorSaveDTO dto) {
        IchInheritor inheritor = dto.getId() == null ? new IchInheritor() : ichInheritorMapper.selectById(dto.getId());
        if (inheritor == null) {
            throw new BizException("传承人不存在");
        }
        inheritor.setName(dto.getName());
        inheritor.setGender(dto.getGender());
        inheritor.setBirthYear(dto.getBirthYear());
        inheritor.setRegionId(dto.getRegionId());
        inheritor.setTitle(dto.getTitle());
        inheritor.setAvatar(dto.getAvatar());
        inheritor.setIntroduction(dto.getIntroduction());
        inheritor.setRepresentativeWorks(dto.getRepresentativeWorks());
        inheritor.setStatus(dto.getStatus());
        if (dto.getId() == null) {
            ichInheritorMapper.insert(inheritor);
        } else {
            ichInheritorMapper.updateById(inheritor);
            ichProjectInheritorMapper.delete(new LambdaQueryWrapper<IchProjectInheritor>()
                    .eq(IchProjectInheritor::getInheritorId, inheritor.getId()));
        }
        if (dto.getProjectIds() != null) {
            for (Long projectId : dto.getProjectIds()) {
                IchProjectInheritor relation = new IchProjectInheritor();
                relation.setProjectId(projectId);
                relation.setInheritorId(inheritor.getId());
                relation.setCreatedAt(LocalDateTime.now());
                ichProjectInheritorMapper.insert(relation);
            }
        }
        Map<Long, String> regionMap = ichRegionMapper.selectList(new LambdaQueryWrapper<>()).stream()
                .collect(Collectors.toMap(IchRegion::getId, IchRegion::getName));
        return toVO(inheritor, regionMap);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteInheritor(Long id) {
        ichProjectInheritorMapper.delete(new LambdaQueryWrapper<IchProjectInheritor>()
                .eq(IchProjectInheritor::getInheritorId, id));
        ichInheritorMapper.deleteById(id);
    }

    @Override
    public List<InheritorVO> listByProjectId(Long projectId) {
        List<Long> ids = ichProjectInheritorMapper.findInheritorIdsByProjectId(projectId);
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, String> regionMap = ichRegionMapper.selectList(new LambdaQueryWrapper<>()).stream()
                .collect(Collectors.toMap(IchRegion::getId, IchRegion::getName));
        return ichInheritorMapper.selectBatchIds(ids).stream()
                .map(inheritor -> toVO(inheritor, regionMap))
                .toList();
    }

    private InheritorVO toVO(IchInheritor inheritor, Map<Long, String> regionMap) {
        return InheritorVO.builder()
                .id(inheritor.getId())
                .name(inheritor.getName())
                .gender(inheritor.getGender())
                .birthYear(inheritor.getBirthYear())
                .regionId(inheritor.getRegionId())
                .regionName(regionMap.get(inheritor.getRegionId()))
                .title(inheritor.getTitle())
                .avatar(inheritor.getAvatar())
                .introduction(inheritor.getIntroduction())
                .representativeWorks(inheritor.getRepresentativeWorks())
                .status(inheritor.getStatus())
                .projectIds(ichProjectInheritorMapper.findProjectIdsByInheritorId(inheritor.getId()))
                .build();
    }
}
