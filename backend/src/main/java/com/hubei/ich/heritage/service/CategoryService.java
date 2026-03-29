package com.hubei.ich.heritage.service;

import com.hubei.ich.heritage.dto.CategorySaveDTO;
import com.hubei.ich.heritage.vo.CategoryVO;

import java.util.List;

public interface CategoryService {

    List<CategoryVO> listCategories(boolean onlyEnabled);

    CategoryVO saveCategory(CategorySaveDTO dto);

    void deleteCategory(Long id);
}
