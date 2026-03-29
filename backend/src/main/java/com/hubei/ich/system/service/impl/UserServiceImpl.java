package com.hubei.ich.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hubei.ich.common.exception.BizException;
import com.hubei.ich.system.dto.AdminUserSaveDTO;
import com.hubei.ich.system.entity.SysRole;
import com.hubei.ich.system.entity.SysUser;
import com.hubei.ich.system.entity.SysUserRole;
import com.hubei.ich.system.mapper.SysRoleMapper;
import com.hubei.ich.system.mapper.SysUserMapper;
import com.hubei.ich.system.mapper.SysUserRoleMapper;
import com.hubei.ich.system.service.UserService;
import com.hubei.ich.system.vo.AdminUserVO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(SysUserMapper sysUserMapper,
                           SysRoleMapper sysRoleMapper,
                           SysUserRoleMapper sysUserRoleMapper,
                           PasswordEncoder passwordEncoder) {
        this.sysUserMapper = sysUserMapper;
        this.sysRoleMapper = sysRoleMapper;
        this.sysUserRoleMapper = sysUserRoleMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<AdminUserVO> listUsers() {
        List<SysUser> users = sysUserMapper.selectList(new LambdaQueryWrapper<SysUser>()
                .orderByDesc(SysUser::getCreatedAt));
        return users.stream().map(user -> AdminUserVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .status(user.getStatus())
                .roleCodes(sysUserRoleMapper.findRoleCodesByUserId(user.getId()))
                .createdAt(user.getCreatedAt())
                .build()).toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdminUserVO saveUser(AdminUserSaveDTO dto) {
        SysUser user;
        boolean creating = dto.getId() == null;
        if (creating) {
            if (!StringUtils.hasText(dto.getPassword())) {
                throw new BizException("新建管理员时必须设置密码");
            }
            long exists = sysUserMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                    .eq(SysUser::getUsername, dto.getUsername()));
            if (exists > 0) {
                throw new BizException("用户名已存在");
            }
            user = new SysUser();
            user.setUsername(dto.getUsername());
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        } else {
            user = sysUserMapper.selectById(dto.getId());
            if (user == null) {
                throw new BizException("管理员不存在");
            }
            if (StringUtils.hasText(dto.getPassword())) {
                user.setPassword(passwordEncoder.encode(dto.getPassword()));
            }
        }
        user.setNickname(dto.getNickname());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setStatus(dto.getStatus());
        if (creating) {
            sysUserMapper.insert(user);
        } else {
            sysUserMapper.updateById(user);
            sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, user.getId()));
        }
        List<SysRole> roles = sysRoleMapper.selectList(new LambdaQueryWrapper<SysRole>()
                .in(SysRole::getRoleCode, dto.getRoleCodes()));
        if (roles.size() != dto.getRoleCodes().size()) {
            throw new BizException("存在无效角色");
        }
        Map<String, SysRole> roleMap = roles.stream()
                .collect(Collectors.toMap(SysRole::getRoleCode, Function.identity()));
        for (String roleCode : dto.getRoleCodes()) {
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(user.getId());
            userRole.setRoleId(roleMap.get(roleCode).getId());
            userRole.setCreatedAt(LocalDateTime.now());
            sysUserRoleMapper.insert(userRole);
        }
        return AdminUserVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .status(user.getStatus())
                .roleCodes(dto.getRoleCodes())
                .createdAt(user.getCreatedAt())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            return;
        }
        if ("admin".equals(user.getUsername())) {
            throw new BizException("默认管理员不可删除");
        }
        sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, id));
        sysUserMapper.deleteById(id);
    }
}
