package com.hubei.ich.common.constant;

public final class SecurityConstants {

    private SecurityConstants() {
    }

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_NAME = "Authorization";
    public static final String REDIS_TOKEN_PREFIX = "ich:auth:token:";
    public static final String REDIS_STATS_PREFIX = "ich:stats:";
}
