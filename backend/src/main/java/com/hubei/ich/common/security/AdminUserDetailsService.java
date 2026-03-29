package com.hubei.ich.common.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hubei.ich.common.exception.BizException;
import com.hubei.ich.system.entity.SysUser;
import com.hubei.ich.system.mapper.SysUserMapper;
import com.hubei.ich.system.mapper.SysUserRoleMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminUserDetailsService implements UserDetailsService {

    private final SysUserMapper sysUserMapper;
    private final SysUserRoleMapper sysUserRoleMapper;

    public AdminUserDetailsService(SysUserMapper sysUserMapper, SysUserRoleMapper sysUserRoleMapper) {
        this.sysUserMapper = sysUserMapper;
        this.sysUserRoleMapper = sysUserRoleMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username)
                .last("LIMIT 1"));
        if (user == null || user.getDeleted() != 0) {
            throw new BizException(401, "管理员账号不存在");
        }
        List<String> roles = sysUserRoleMapper.findRoleCodesByUserId(user.getId());
        return UserPrincipal.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .enabled(user.getStatus() == 1)
                .roles(roles)
                .build();
    }
}
