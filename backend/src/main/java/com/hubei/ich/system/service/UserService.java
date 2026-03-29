package com.hubei.ich.system.service;

import com.hubei.ich.system.dto.AdminUserSaveDTO;
import com.hubei.ich.system.vo.AdminUserVO;

import java.util.List;

public interface UserService {

    List<AdminUserVO> listUsers();

    AdminUserVO saveUser(AdminUserSaveDTO dto);

    void deleteUser(Long id);
}
