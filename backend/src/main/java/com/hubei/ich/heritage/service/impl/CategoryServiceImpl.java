package com.hubei.ich.heritage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hubei.ich.common.exception.BizException;
import com.hubei.ich.heritage.dto.CategorySaveDTO;
import com.hubei.ich.heritage.entity.IchCategory;
import com.hubei.ich.heritage.mapper.IchCategoryMapper;
import com.hubei.ich.heritage.service.CategoryService;
import com.hubei.ich.heritage.vo.CategoryVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final IchCategoryMapper ichCategoryMapper;

    public CategoryServiceImpl(IchCategoryMapper ichCategoryMapper) {
        this.ichCategoryMapper = ichCategoryMapper;
    }

    @Override
    public List<CategoryVO> listCategories(boolean onlyEnabled) {
        LambdaQueryWrapper<IchCategory> wrapper = new LambdaQueryWrapper<IchCategory>()
                .orderByAsc(IchCategory::getSortOrder);
        if (onlyEnabled) {
            wrapper.eq(IchCategory::getStatus, 1);
        }
        return ichCategoryMapper.selectList(wrapper).stream().map(category -> CategoryVO.builder()
                .id(category.getId())
                .name(category.getName())
                .code(category.getCode())
                .sortOrder(category.getSortOrder())
                .status(category.getStatus())
                .description(category.getDescription())
                .build()).toList();
    }

    @Override
    public CategoryVO saveCategory(CategorySaveDTO dto) {
        IchCategory category = dto.getId() == null ? new IchCategory() : ichCategoryMapper.selectById(dto.getId());
        if (category == null) {
            throw new BizException("分类不存在");
        }
        category.setName(dto.getName());
        category.setCode(dto.getCode());
        category.setSortOrder(dto.getSortOrder());
        category.setStatus(dto.getStatus());
        category.setDescription(dto.getDescription());
        if (dto.getId() == null) {
            ichCategoryMapper.insert(category);
        } else {
            ichCategoryMapper.updateById(category);
        }
        return CategoryVO.builder()
                .id(category.getId())
                .name(category.getName())
                .code(category.getCode())
                .sortOrder(category.getSortOrder())
                .status(category.getStatus())
                .description(category.getDescription())
                .build();
    }

    @Override
    public void deleteCategory(Long id) {
        ichCategoryMapper.deleteById(id);
    }
}
