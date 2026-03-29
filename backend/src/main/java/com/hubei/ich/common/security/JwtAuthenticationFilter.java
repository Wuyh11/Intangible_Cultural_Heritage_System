package com.hubei.ich.common.security;

import com.hubei.ich.common.constant.SecurityConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final StringRedisTemplate stringRedisTemplate;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, StringRedisTemplate stringRedisTemplate) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader(SecurityConstants.HEADER_NAME);
        String token = resolveToken(authorization);
        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
            String username = jwtTokenProvider.getUsername(token);
            Long userId = jwtTokenProvider.getUserId(token);
            if (tokenStillValid(username, token)) {
                UserPrincipal principal = UserPrincipal.builder()
                        .userId(userId)
                        .username(username)
                        .password("")
                        .enabled(true)
                        .roles(jwtTokenProvider.getRoles(token))
                        .build();
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        principal.getAuthorities()
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }

    private String resolveToken(String authorization) {
        if (StringUtils.hasText(authorization) && authorization.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            return authorization.substring(SecurityConstants.TOKEN_PREFIX.length());
        }
        return null;
    }

    private boolean tokenStillValid(String username, String token) {
        try {
            String redisToken = stringRedisTemplate.opsForValue()
                    .get(SecurityConstants.REDIS_TOKEN_PREFIX + username);
            return !StringUtils.hasText(redisToken) || token.equals(redisToken);
        } catch (Exception ignored) {
            return true;
        }
    }
}
