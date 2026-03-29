package com.hubei.ich.heritage.controller;

import com.hubei.ich.common.aspect.OperationLog;
import com.hubei.ich.common.result.ApiResponse;
import com.hubei.ich.heritage.dto.CategorySaveDTO;
import com.hubei.ich.heritage.service.CategoryService;
import com.hubei.ich.heritage.vo.CategoryVO;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN','EDITOR')")
public class AdminCategoryController {

    private final CategoryService categoryService;

    public AdminCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ApiResponse<List<CategoryVO>> listCategories() {
        return ApiResponse.success(categoryService.listCategories(false));
    }

    @PostMapping
    @OperationLog(module = "基础数据", action = "新增分类")
    public ApiResponse<CategoryVO> createCategory(@Valid @RequestBody CategorySaveDTO dto) {
        return ApiResponse.success(categoryService.saveCategory(dto));
    }

    @PutMapping("/{id}")
    @OperationLog(module = "基础数据", action = "编辑分类")
    public ApiResponse<CategoryVO> updateCategory(@PathVariable Long id, @Valid @RequestBody CategorySaveDTO dto) {
        dto.setId(id);
        return ApiResponse.success(categoryService.saveCategory(dto));
    }

    @DeleteMapping("/{id}")
    @OperationLog(module = "基础数据", action = "删除分类")
    public ApiResponse<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ApiResponse.success();
    }
}
