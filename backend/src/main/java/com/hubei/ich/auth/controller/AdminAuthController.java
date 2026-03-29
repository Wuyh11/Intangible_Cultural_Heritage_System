package com.hubei.ich.auth.controller;

import com.hubei.ich.auth.dto.AdminLoginDTO;
import com.hubei.ich.auth.service.AuthService;
import com.hubei.ich.auth.vo.LoginVO;
import com.hubei.ich.common.result.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/auth")
public class AdminAuthController {

    private final AuthService authService;

    public AdminAuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResponse<LoginVO> login(@Valid @RequestBody AdminLoginDTO dto) {
        return ApiResponse.success(authService.login(dto));
    }
}
