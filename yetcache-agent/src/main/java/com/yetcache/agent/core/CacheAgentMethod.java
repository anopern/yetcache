package com.yetcache.agent.core;

import java.util.Optional;

/**
 * @author walter.yan
 * @since 2025/7/16
 */
public enum CacheAgentMethod {
    GET,
    LIST_ALL,
    REFRESH_ALL,
    INVALIDATE,
    INVALIDATE_ALL,
    PUT,
    PUT_ALL;

    public static Optional<CacheAgentMethod> from(String methodName) {
        try {
            return Optional.of(CacheAgentMethod.valueOf(methodName.toUpperCase()));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
