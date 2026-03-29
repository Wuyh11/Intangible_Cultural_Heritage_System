package com.hubei.ich.system.controller;

import com.hubei.ich.common.aspect.OperationLog;
import com.hubei.ich.common.result.ApiResponse;
import com.hubei.ich.system.dto.AdminUserSaveDTO;
import com.hubei.ich.system.service.UserService;
import com.hubei.ich.system.vo.AdminUserVO;
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
@RequestMapping("/api/admin/users")
@PreAuthorize("hasAuthority('SUPER_ADMIN')")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ApiResponse<List<AdminUserVO>> listUsers() {
        return ApiResponse.success(userService.listUsers());
    }

    @PostMapping
    @OperationLog(module = "管理员", action = "新增管理员")
    public ApiResponse<AdminUserVO> createUser(@Valid @RequestBody AdminUserSaveDTO dto) {
        return ApiResponse.success(userService.saveUser(dto));
    }

    @PutMapping("/{id}")
    @OperationLog(module = "管理员", action = "编辑管理员")
    public ApiResponse<AdminUserVO> updateUser(@PathVariable Long id, @Valid @RequestBody AdminUserSaveDTO dto) {
        dto.setId(id);
        return ApiResponse.success(userService.saveUser(dto));
    }

    @DeleteMapping("/{id}")
    @OperationLog(module = "管理员", action = "删除管理员")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ApiResponse.success();
    }
}
