package com.hubei.ich.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hubei.ich.auth.dto.AdminLoginDTO;
import com.hubei.ich.auth.service.AuthService;
import com.hubei.ich.auth.vo.LoginVO;
import com.hubei.ich.common.constant.SecurityConstants;
import com.hubei.ich.common.security.JwtTokenProvider;
import com.hubei.ich.common.security.UserPrincipal;
import com.hubei.ich.system.entity.SysUser;
import com.hubei.ich.system.mapper.SysUserMapper;
import com.hubei.ich.system.mapper.SysUserRoleMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final SysUserMapper sysUserMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final StringRedisTemplate stringRedisTemplate;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           JwtTokenProvider jwtTokenProvider,
                           SysUserMapper sysUserMapper,
                           SysUserRoleMapper sysUserRoleMapper,
                           StringRedisTemplate stringRedisTemplate) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.sysUserMapper = sysUserMapper;
        this.sysUserRoleMapper = sysUserRoleMapper;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public LoginVO login(AdminLoginDTO dto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
            );
            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
            List<String> roles = sysUserRoleMapper.findRoleCodesByUserId(principal.getUserId());
            UserPrincipal tokenPrincipal = UserPrincipal.builder()
                    .userId(principal.getUserId())
                    .username(principal.getUsername())
                    .password("")
                    .enabled(principal.isEnabled())
                    .roles(roles)
                    .build();
            String token = jwtTokenProvider.generateToken(tokenPrincipal);
            cacheToken(principal.getUsername(), token);
            SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                    .eq(SysUser::getUsername, dto.getUsername())
                    .last("LIMIT 1"));
            return LoginVO.builder()
                    .token(token)
                    .username(user.getUsername())
                    .nickname(user.getNickname())
                    .roles(roles)
                    .build();
        } catch (BadCredentialsException exception) {
            throw new com.hubei.ich.common.exception.BizException(401, "用户名或密码错误");
        } catch (DisabledException exception) {
            throw new com.hubei.ich.common.exception.BizException(403, "账号已被禁用");
        }
    }

    private void cacheToken(String username, String token) {
        try {
            stringRedisTemplate.opsForValue()
                    .set(SecurityConstants.REDIS_TOKEN_PREFIX + username, token, 1, TimeUnit.DAYS);
        } catch (Exception ignored) {
        }
    }
}
