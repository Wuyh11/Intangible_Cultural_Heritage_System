package com.hubei.ich.auth.service;

import com.hubei.ich.auth.dto.AdminLoginDTO;
import com.hubei.ich.auth.vo.LoginVO;

public interface AuthService {

    LoginVO login(AdminLoginDTO dto);
}
